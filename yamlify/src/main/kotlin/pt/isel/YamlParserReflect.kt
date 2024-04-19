package pt.isel

import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * A YamlParser that uses reflection to parse objects.
 */
class YamlParserReflect<T : Any>(private val type: KClass<T>) : AbstractYamlParser<T>(type) {
    private val constructor: KFunction<T>
    private val allParametersOfType: List<KParameter>
    private val parametersBuilders: Map<String, ParametersBuilder>
    private val primitives: Map<KClass<*>, (Any) -> Any>
    init {
        primitives = mapOf(
            String::class to { str -> str },
            Int::class to { str -> str.toString().toInt() },
            Long::class to { str -> str.toString().toLong() },
            Double::class to { str -> str.toString().toDouble() },
            Float::class to { str -> str.toString().toFloat() },
            Short::class to { str -> str.toString().toShort() },
            Byte::class to { str -> str.toString().toByte() },
            Char::class to { str -> str.toString().first() },
            Boolean::class to { str -> str.toString().toBoolean() },
        )
        constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
        allParametersOfType = constructor.parameters/*.filter { it.name != null || it.findAnnotation<YamlArg>() != null }*/
        parametersBuilders = allParametersOfType.associate {
            val paramName = it.findAnnotation<YamlArg>()?.paramName ?: it.name ?: throw IllegalArgumentException("Parameter name not found")
            paramName to ParametersBuilder(it)
        }
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
        if (args.containsKey("#")) {
            return args["#"]?.let { primitives[type]?.invoke(it) } as T
        } else { // Filter the parameters that are present in the args map
            val arguments: Map<KParameter, Any> = args.entries.associate { createCallByArgEntry(it) }
            return constructor.callBy(arguments)
        }
    }

    private fun createCallByArgEntry(entry: Map.Entry<String, Any>): Pair<KParameter, Any> {
        parametersBuilders[entry.key]?.let { builder ->
            return builder.param to builder.buildParameter(entry.value)
        }
        throw IllegalArgumentException("Missing parameter ${entry.key}")
    }

    inner class ParametersBuilder(val param: KParameter) {
        private val parameterBuild: (Any/* object from abstractYamlParser */) -> Any // Object that is going to be instantiated by reflection

        init { parameterBuild = getParameterType(param) }

        private fun getParameterType(param: KParameter): (Any) -> Any {
            val paramKClass = param.type.classifier as KClass<*>
            return when {
                param.findAnnotation<YamlConvert>() != null -> {
                    val parserClass = param.findAnnotation<YamlConvert>()!!.parser
                    val parser = instantiateCustomParser(parserClass)
                    return { any ->
                        parser.convert(any.toString(), param.name!!) ?: throw IllegalArgumentException("Could not convert")
                    }
                }
                // E.g: When the parameter is grades from Student
                paramKClass == List::class -> {
                    val listType = param.type.arguments.first().type?.classifier as? KClass<*> ?: throw IllegalArgumentException("List element type not found")
                    val listTypeConstructor = listType.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found for list element type")
                    val listParameters = listTypeConstructor.parameters
                    val listBuildParameters = listParameters.associate { p ->
                        val paramName = p.findAnnotation<YamlArg>()?.paramName ?: p.name ?: throw IllegalArgumentException("Parameter name not found")
                        (paramName to (p to getParameterType(p)))
                    }
                    return { list ->
                        if (list !is List<*>) throw IllegalArgumentException("Expected a list")
                        if (list.isEmpty()) { // This condition may be unnecessary
                            emptyList<Any>()
                        } else {
                            (list).map { element ->
                                if (element != null) {
                                    objectBuildParameters(listBuildParameters, listTypeConstructor, element)
                                } else {
                                    throw IllegalArgumentException("Expected a list")
                                }
                            }
                        }
                    }
                }
                paramKClass.javaPrimitiveType == null && paramKClass != String::class -> {
                    val objConstructor = paramKClass.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
                    val objParameters = objConstructor.parameters
                    val buildParameters = objParameters.associate { p ->
                        val paramName = p.findAnnotation<YamlArg>()?.paramName ?: p.name ?: throw IllegalArgumentException("Parameter name not found")
                        (paramName to (p to getParameterType(p)))
                    }
                    return { map ->
                        objectBuildParameters(buildParameters, objConstructor, map)
                    }
                }
                else -> getPrimitiveCreator(paramKClass)
            }
        }

        private fun instantiateCustomParser(parserClass: KClass<out Any>): YamlAny {
            return when (parserClass) {
                YamlAny::class -> YamlAny()
                else -> throw IllegalArgumentException("Unknown custom parser class: $parserClass")
            }
        }

        private fun argumentEntry(entry: Map.Entry<*, *>, buildParameters: Map<String, Pair<KParameter, (Any) -> Any>>): Pair<KParameter, Any?> {
            buildParameters[entry.key]?.let { builder ->
                return builder.first to entry.value?.let { builder.second(it) }
            }
            throw IllegalArgumentException("Missing parameter ${entry.key}")
        }

        private fun getPrimitiveCreator(paramKClass: KClass<*>): (Any) -> Any {
            val primitiveCreator = primitives[paramKClass]
            return { any ->
                primitiveCreator?.invoke(any) ?: throw IllegalArgumentException("No primitive type has been found")
            }
        }

        private fun objectBuildParameters(buildParameters: Map<String, Pair<KParameter, (Any) -> Any>>, constructor: KFunction<Any>, map: Any): Any {
            if (map !is Map<*,*>) throw IllegalArgumentException("Expected a map")
            val argumentsToPass: Map<KParameter, Any?> = map.entries.associate { argumentEntry(it, buildParameters) }
            try {
                return constructor.callBy(argumentsToPass)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        fun buildParameter(value: Any): Any {
            return parameterBuild(value)
        }
    }
}

