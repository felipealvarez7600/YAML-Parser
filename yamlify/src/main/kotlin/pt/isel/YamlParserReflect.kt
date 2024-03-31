package pt.isel

import pt.isel.annotations.YamlArg
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * A YamlParser that uses reflection to parse objects.
 */
class YamlParserReflect<T : Any>(private val type: KClass<T>) : AbstractYamlParser<T>(type) {
    companion object {
        /**
         *Internal cache of YamlParserReflect instances.
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

            when (val classifier = parameter.type.classifier) {
                is KClass<*> -> {
                    if (argValue is Map<*, *>) {
                        yamlParser(classifier).newInstance(argValue as Map<String, Any>)
                    } else if (argValue is List<*>) {
                        val listArgType = parameter.type.arguments.first().type!!.classifier as KClass<*>
                        val convertedList = argValue.map { element ->
                            if (element is Map<*, *>) {
                                yamlParser(listArgType).newInstance(element as Map<String, Any>)
                            } else {
                                element
                            }
                        }
                        convertToType(convertedList, classifier)
                    } else {
                        convertToType(argValue, classifier)
                    }
                }
                else -> argValue // For primitive types
            }
        }
        // Call the constructor with the parameters
        return constructor.callBy(parameters)
    }

}

