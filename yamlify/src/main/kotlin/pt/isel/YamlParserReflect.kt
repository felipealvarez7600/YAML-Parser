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
    private val constructor: KFunction<T>
    private val paramNames: List<String>
    private val parameters: List<KParameter>
    private val parametersBuilders: Map<String, ParametersBuilder>
    init {
        constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
        parameters = constructor.parameters
        paramNames = parameters.map { it.name ?: it.findAnnotation<YamlArg>()?.paramName!! }
        parametersBuilders = paramNames.associateWith { ParametersBuilder(parameters[paramNames.indexOf(it)]) }
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
        val parametersToPass = parameters.filter { parameter ->
            args.containsKey(parameter.name) || !parameter.isOptional || parameter.findAnnotation<YamlArg>() != null
        }
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
//                            yamlParser(listArgType).newInstance(element as Map<String, Any>)
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

        val objValues = parametersToPass.associateWith { p ->
            val entry = args.entries.find { (key, _) -> key == p.name }
            entry?.let { parametersBuilders[entry.key]?.buildParameter(it.value) ?: throw IllegalArgumentException("Missing parameter ${entry.key}") }
        }

        return constructor.callBy(objValues)
    }

    class ParametersBuilder(param: KParameter) {
        private val parameterBuild: (Any/* object from abstractYamlParser */) -> Any // Object that has been instantiated by reflection
        private val primitives: Map<KClass<*>, (Any) -> Any> = mapOf(
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

        init {
//            val autoRouteAnnotations = param.annotations
//                .map { it.annotationClass }
//                .filter { it == YamlArg::class }
//                .toList()
            val paramName = param.name ?: param.findAnnotation<YamlArg>()?.paramName!!

            parameterBuild = getParameterType(param, paramName)
        }

        private fun getParameterType(param: KParameter, paramName: String): (Any) -> Any {
            val paramKClass = param.type.classifier as KClass<*>
//            when (val paramKClass = parameter.type.classifier) {
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
            return when {
                paramName == "#" -> getPrimitiveCreator(paramKClass, paramName)
                paramKClass == List::class -> {
                    val listType = param.type.arguments.first().type?.classifier as? KClass<*> ?: throw IllegalArgumentException("List element type not found")
                    val listTypeConstructor = listType.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found for list element type")
                    val listBuildParameters = listTypeConstructor.parameters.map { p -> getParameterType(p, p.name!!) }
                    return { list ->
                        if (list is List<*> && list.isEmpty()) {
                            emptyList<Any>()
                        } else {
                            (list as? List<*>)?.map { element ->
                                val elementParameters = listBuildParameters.mapNotNull { ac ->
                                    element?.let { ac(it) }
                                }.toTypedArray()
                                try {
                                    listTypeConstructor.call(elementParameters)
                                } catch (e: Exception) {
                                    throw RuntimeException(e)
                                }
                            } ?: throw IllegalArgumentException("Expected a list")
                        }
                    }
                }
                paramKClass.javaPrimitiveType == null && paramKClass != String::class -> {
                    val constructor = paramKClass.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
                    val buildParameters = constructor.parameters.map { p -> getParameterType(p, p.name!!) }
                    return { map ->
                        val parameters = buildParameters.map { ac -> ac(map) }.toTypedArray()
                        try {
                            constructor.call(parameters)
                        } catch (e: Exception) {
                            throw RuntimeException(e)
                        }
                    }
                }
                else -> getPrimitiveCreator(paramKClass, paramName)
            }
        }

        private fun getPrimitiveCreator(paramKClass: KClass<*>, paramName: String): (Any) -> Any {
            val primitiveCreator = primitives[paramKClass]
            return { any ->
                primitiveCreator?.invoke(any) ?: throw IllegalArgumentException("No primitive creator for $paramName")
            }
        }

        fun buildParameter(value: Any): Any {
            return parameterBuild(value)
        }
    }
}

