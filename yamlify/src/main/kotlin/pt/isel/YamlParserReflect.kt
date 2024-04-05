package pt.isel

import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * A YamlParser that uses reflection to parse objects.
 */
class YamlParserReflect<T : Any>(private val type: KClass<T>) : AbstractYamlParser<T>(type) {
    val constructor: KFunction<T>
    val parametersList: List<KParameter>

    init {
        constructor = type.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found")
        // Filter the parameters that are present in the args map or are not optional
        parametersList = constructor.parameters
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
        // Filter the parameters that are present in the args map or are not optional
        val parametersToPass = parametersList.filter { parameter ->
            args.containsKey(parameter.name) || !parameter.isOptional || parameter.findAnnotation<YamlArg>() != null
        }
        // Associate only the parameters that are present in the args map or are not optional
        val parameters = parametersToPass.associateWith { parameter ->
            val argValue = args[parameter.name] ?: throw IllegalArgumentException("Missing parameter ${parameter.name}")
            parameter.findAnnotation<YamlConvert>()?.let { annotation ->
                val converterClass = annotation.converter
                val converterInstance = converterClass.objectInstance as? YamlConverter<*>
                    ?: converterClass.createInstance()
                converterInstance.convert(argValue.toString())
            } ?: run {
                // Existing conversion logic
                when (val classifier = parameter.type.classifier) {
                    is KClass<*> -> convertToType(argValue, classifier)
                    else -> argValue
                }
            }
        }
        // Call the constructor with the parameters
        return constructor.callBy(parameters)
    }
}

