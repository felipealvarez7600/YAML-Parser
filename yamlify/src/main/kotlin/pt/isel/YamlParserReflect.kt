package pt.isel

import YamlAny
import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
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
    private val constructor: KFunction<T>
    private val paramNames: List<String>
    private val parametersOfType: List<KParameter>
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
        parametersOfType = constructor.parameters.filter { parameter ->
            parameter.name != null || !parameter.isOptional || parameter.findAnnotation<YamlArg>() != null
        }
        paramNames = parametersOfType.map { it.name ?: it.findAnnotation<YamlArg>()?.paramName!! }
        parametersBuilders = paramNames.associateWith { ParametersBuilder(parametersOfType[paramNames.indexOf(it)]) }
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
        // Filter the parameters that are present in the args map
        if (args.containsKey("#")) {
            return args["#"]?.let { primitives[type]?.invoke(it) } as T
        } else {
            val parametersToPass = parametersOfType.filter { args.containsKey(it.name) || !it.isOptional || it.findAnnotation<YamlArg>() != null}
            val objValues = parametersToPass.associateWith { p ->
                val paramName = p.findAnnotation<YamlArg>()?.paramName ?: p.name
                ?: throw IllegalArgumentException("Parameter name not found")
                val entry = args.entries.find { (key, _) -> key == paramName || key == p.name }
                entry?.let { parametersBuilders[p.name]?.buildParameter(it.value) ?: throw IllegalArgumentException("Missing parameter ${entry.key}") }
            }

            return constructor.callBy(objValues)
        }


//        // Associate only the parameters that are present in the args map or are not optional
//        val parameters = parametersToPass.associateWith { parameter ->
//            val argValue = args[parameter.name] ?: run {
//                val yamlArgAnnotation = parameter.findAnnotation<YamlArg>()
//                val argKey = yamlArgAnnotation?.paramName ?: parameter.name
//                args[argKey] ?: throw IllegalArgumentException("Missing parameter $argKey")
//            }
//            when {
//                parameter.findAnnotation<YamlConvert>() != null -> {
//                    val customParserClass = parameter.findAnnotation<YamlConvert>()!!.parser
//                    val customParserInstance = instantiateCustomParser(customParserClass)
//                    parameter.name?.let { customParserInstance.convert(argValue.toString(), it) }
//                }
//                parameter.type.classifier is KClass<*> -> {
//                    if (argValue is Map<*, *>) {
//                        yamlParser(parameter.type.classifier as KClass<*>).newInstance(argValue as Map<String, Any>)
//                    } else if (argValue is List<*>) {
//                        val listArgType = parameter.type.arguments.first().type!!.classifier as KClass<*>
//                        argValue.map {
//                            yamlParser(listArgType).newInstance(it as Map<String, Any>)
//                        }
//                    } else {
//                        convertToType(argValue, parameter.type.classifier as KClass<*>)
//                    }
//                }
//                else -> argValue
//            }
//        }
//        // Call the constructor with the parameters
//        return constructor.callBy(parameters)

    }

    inner class ParametersBuilder(param: KParameter) {
        private val parameterBuild: (Any/* object from abstractYamlParser */) -> Any // Object that has been instantiated by reflection

        init {
//            val autoRouteAnnotations = param.annotations
//                .map { it.annotationClass }
//                .filter { it == YamlArg::class }
//                .toList()

//            val paramName = param.name ?: param.findAnnotation<YamlArg>()?.paramName!!

            parameterBuild = getParameterType(param)
        }

        private fun getParameterType(param: KParameter): (Any) -> Any {
            val paramKClass = param.type.classifier as KClass<*>
            return when {
                param.findAnnotation<YamlConvert>() != null -> {
                    val parserClass = param.findAnnotation<YamlConvert>()!!.parser
                    val parser = instantiateCustomParser(parserClass)
                    return { any -> parser.convert(any.toString(), param.name!!) ?: throw IllegalArgumentException("Could not convert")}
                }
                // E.g: When the parameter is grades from Student
                paramKClass == List::class -> {
                    val listType = param.type.arguments.first().type?.classifier as? KClass<*> ?: throw IllegalArgumentException("List element type not found")
                    val listTypeConstructor = listType.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found for list element type")
                    val listParametersFiltered = filterParameters(listTypeConstructor.parameters)
                    val listBuildParameters = listParametersFiltered.associate { p -> (p.name!! to getParameterType(p)) }
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
                    val parametersFiltered = filterParameters(objConstructor.parameters)
                    val buildParameters = parametersFiltered.associate { p -> (p.name!! to getParameterType(p)) }
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

        private fun filterParameters(parametersToBeFiltered: List<KParameter>): List<KParameter> {
            return parametersToBeFiltered.filter { parameter ->
                parameter.name != null || !parameter.isOptional || parameter.findAnnotation<YamlArg>() != null
            }
        }

        private fun getPrimitiveCreator(paramKClass: KClass<*>): (Any) -> Any {
            val primitiveCreator = primitives[paramKClass]
            return { any ->
                primitiveCreator?.invoke(any) ?: throw IllegalArgumentException("No primitive type has been found")
            }
        }

        private fun objectBuildParameters(buildParameters: Map<String, (Any) -> Any>, constructor: KFunction<Any>, map: Any): Any {
            if (map !is Map<*,*>) throw IllegalArgumentException("Expected a map")
            val parameterValues = buildParameters.map { (paramName, value) ->
                val paramWithoutAnnotation = constructor.parameters.find { it.name == paramName }!!
                val parameter = map[paramName] ?: map[paramWithoutAnnotation.findAnnotation<YamlArg>()?.paramName!!] ?: throw IllegalArgumentException("Missing parameter $paramName")
                value(parameter)
            }.toTypedArray()

            try {
                return constructor.call(*parameterValues)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        fun buildParameter(value: Any): Any {
            return parameterBuild(value)
        }
    }

    private fun instantiateCustomParser(parserClass: KClass<out Any>): YamlAny {
        return when (parserClass) {
            YamlAny::class -> YamlAny()
            else -> throw IllegalArgumentException("Unknown custom parser class: $parserClass")
        }
    }
}

