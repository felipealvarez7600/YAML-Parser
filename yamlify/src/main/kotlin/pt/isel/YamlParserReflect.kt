package pt.isel

import pt.isel.annotations.YamlArg
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * A YamlParser that uses reflection to parse objects.
 */
class YamlParserReflect<T : Any>(private val type: KClass<T>) : AbstractYamlParser<T>(type) {

    // Map of parameters of the names of the constructor e.g.: Map<String, KParameter>

    //name -> KParameter(name)
    //city of birth -> KParameter(from)

    //Classes that have maps of parameters
    val constructor: KFunction<T>
    val parametersBuilders: List<ParametersBuilder>
    init {
        constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
        parametersBuilders = constructor.parameters.map { p -> ParametersBuilder(p) }

//        parameters = constructor.parameters.associateBy {
//            (it.name ?: run {
//                val yamlArgAnnotation = it.findAnnotation<YamlArg>()
//                yamlArgAnnotation?.paramName ?: it.name
//            }).toString()
//        }
    }

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
//        // Is this string the key of the property of yaml object?
//        val constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
//        // Filter the parameters that are present in the args map or are not optional
//        val parametersToPass = constructor.parameters.filter { parameter ->
//            args.containsKey(parameter.name) || !parameter.isOptional || parameter.findAnnotation<YamlArg>() != null
//        }
//        // Associate only the parameters that are present in the args map or are not optional
//        val parameters = parametersToPass.associateWith { parameter ->
//            val argValue = args[parameter.name] ?: run {
//                val yamlArgAnnotation = parameter.findAnnotation<YamlArg>()
//                val argKey = yamlArgAnnotation?.paramName ?: parameter.name
//                args[argKey] ?: throw IllegalArgumentException("Missing parameter $argKey")
//            }
//
//            when (val classifier = parameter.type.classifier) {
//                is KClass<*> -> {
//                    if (argValue is Map<*, *>) {
//                        yamlParser(classifier).newInstance(argValue as Map<String, Any>)
//                    } else if (argValue is List<*>) {
//                        val listArgType = parameter.type.arguments.first().type!!.classifier as KClass<*>
//                        val convertedList = argValue.map { element ->
//                            if (element is Map<*, *>) {
//                                yamlParser(listArgType).newInstance(element as Map<String, Any>)
//                            } else {
//                                element
//                            }
//                        }
//                        convertToType(convertedList, classifier)
//                    } else {
//                        convertToType(argValue, classifier)
//                    }
//                }
//                else -> argValue // For primitive types
//            }
//        }
//        // Call the constructor with the parameters
//        return constructor.callBy(parameters)
        val objValues = parametersBuilders.filter { it.buildParameter(args) != null }
            .map { it.buildParameter(args) }
            .toTypedArray()

        return constructor.call(objValues)
    }

    /** Why did the teacher told me to have a map of key String and value KPArameter (Map<String, KParameter)? */
    class ParametersBuilder(param: KParameter) {
        private val parameterBuild: (Map<String, KParameter>) -> Any // Map<String, Any> -> T in YamlParserReflect

        init {
//            val autoRouteAnnotations = param.annotations
//                .map { it.annotationClass }
//                .filter { it == YamlArg::class }
//                .toList()
            val paramName = param.name ?: param.findAnnotation<YamlArg>()?.paramName!!

            parameterBuild = getParameterType(param, paramName)
        }

        private fun getParameterType(param: KParameter, paramName: String): (Map<String, KParameter>) -> Any {
            val paramKClass = param.type.classifier as KClass<*>
            return if (paramKClass == String::class) {
                { map -> map[paramName]!! }
            } else {
                if (paramKClass.javaPrimitiveType != null) {
                    val constructors = paramKClass.constructors
                    if (constructors.size != 1) {
                        throw IllegalArgumentException("Type '$param' with none or more constructors")
                    }
                    val constructor = constructors.first()
                    val buildParameters = constructor.parameters.map { p -> getParameterType(p, p.name!!) }
                    return { map ->
                        val parameters = buildParameters.map { ac -> ac(map) }.toTypedArray()
                        try {
                            constructor.call(parameters)
                        } catch (e: Exception) {
                            throw RuntimeException(e)
                        }
                    }
                } else {
                    val primitiveCreator = primitives[param.type.classifier as KClass<*>]
                    return { map ->
                        val parameter = map[paramName]
                        primitiveCreator?.invoke(parameter!!) ?: throw IllegalArgumentException("No primitive creator for $paramName")
                    }
                }
            }
        }

        private val primitives = mapOf<KClass<*>, (Any) -> Any>(
            Int::class to { str -> str.toString().toInt() },
            Long::class to { str -> str.toString().toLong() },
            Double::class to { str -> str.toString().toDouble() },
            Float::class to { str -> str.toString().toFloat() },
            Short::class to { str -> str.toString().toShort() },
            Byte::class to { str -> str.toString().toByte() },
            Char::class to { str -> str.toString().first() },
            Boolean::class to { str -> str.toString().toBoolean() },
            List::class to { str -> str.toString().split(",").map { it.trim() } },
            Sequence::class to { str -> str.toString().split(",").asSequence() }
        )

        fun buildParameter(args: Map<String, KParameter>): Any {
            return parameterBuild(args)
        }
    }
}

