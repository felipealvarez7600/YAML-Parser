package pt.isel

import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * A YamlParser that uses reflection to parse objects.
 */
class YamlParserReflect<T : Any>(private val type: KClass<T>) : AbstractYamlParser<T>(type) {
    companion object {
        /**
         * Internal cache of YamlParserReflect instances.
         */
        private val yamlParsers: MutableMap<KClass<*>, YamlParserReflect<*>> = mutableMapOf()

        /**
         * Creates a YamlParser for the given type using reflection if it does not already exist.
         * Keep it in an internal cache of YamlParserReflect instances.
         */
        fun <T : Any> yamlParser(type: KClass<T>): AbstractYamlParser<T> {
            return yamlParsers.getOrPut(type) { YamlParserReflect(type) } as YamlParserReflect<T>
        }
    }

    /**
     * Used to get a parser for other Type using the same parsing approach.
     */
    override fun <T : Any> yamlParser(type: KClass<T>) = YamlParserReflect.yamlParser(type)

    /**
     * Creates a new instance of T through the first constructor
     * that has all the mandatory parameters in the map and optional parameters for the rest.
     */

    override fun newInstance(args: Map<String, Any>): T {
        val constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")

        if (args.containsKey("#")) {
            return args["#"]?.let { convertToType(it, type) } as T
        } else {
            // Filter the parameters that are present in the args map or are not optional
            val parametersToPass = constructor.parameters.filter { parameter ->
                args.containsKey(parameter.name) || !parameter.isOptional || parameter.findAnnotation<YamlArg>() != null
            }
            // Associate only the parameters that are present in the args map or are not optional
            val parameters = parametersToPass.associateWith { parameter ->
                val argValue = args[parameter.name] ?: run {

                    val yamlArgAnnotation = parameter.findAnnotation<YamlArg>()
                    val argKey = yamlArgAnnotation?.paramName ?: parameter.name
                    args[argKey] ?: throw IllegalArgumentException("Missing parameter $argKey")
                }

                when {
                    parameter.findAnnotation<YamlConvert>() != null -> {
                        val customParserClass = parameter.findAnnotation<YamlConvert>()!!.parser
                        val customParserInstance = instantiateCustomParser(customParserClass)
                        parameter.name?.let { customParserInstance.convert(argValue.toString(), it) }
                    }
                    parameter.type.classifier is KClass<*> -> {
                        if (argValue is Map<*, *>) {
                            yamlParser(parameter.type.classifier as KClass<*>).newInstance(argValue as Map<String, Any>)
                        } else if (argValue is List<*>) {
                            val listArgType = parameter.type.arguments.first().type!!.classifier as KClass<*>
                            val convertedList = argValue.map { element ->
                                yamlParser(listArgType).newInstance(element as Map<String, Any>)
                            }
                            convertToType(convertedList, parameter.type.classifier as KClass<*>)
                        } else {
                            convertToType(argValue, parameter.type.classifier as KClass<*>)
                        }
                    }
                    else -> argValue
                }
            }
            return constructor.callBy(parameters)
        }
    }

    private fun instantiateCustomParser(parserClass: KClass<out Any>): YamlAny {
        return when (parserClass) {
            YamlAny::class -> YamlAny()
            else -> throw IllegalArgumentException("Unknown custom parser class: $parserClass")
        }
    }

    /**
     * Function that converts the value to the type of the parameter.
     */
    private fun convertToType(argValue: Any, type: KClass<*>): Any {
        return when (type) {
            String::class -> argValue.toString()
            Boolean::class -> argValue.toString().toBoolean()
            Short::class -> argValue.toString().toShort()
            Int::class -> argValue.toString().toInt()
            Long::class -> argValue.toString().toLong()
            Double::class -> argValue.toString().toDouble()
            Float::class -> argValue.toString().toFloat()
            Char::class -> argValue.toString().first()
            Byte::class -> argValue.toString().toByte()
            List::class -> argValue as List<*>
            else -> throw IllegalArgumentException("Unsupported type $type")
        }
    }
}

