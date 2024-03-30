package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
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
            args.containsKey(parameter.name) || !parameter.isOptional
        }
        // Associate only the parameters that are present in the args map or are not optional
        val parameters = parametersToPass.associateWith { parameter ->
            val argValue = args[parameter.name] ?: throw IllegalArgumentException("Missing parameter ${parameter.name}")
            if (argValue is Map<*, *>) {
                // If the value is a map, recursively call newInstance
                yamlParser(parameter.type.classifier as KClass<*>).newInstance(argValue as Map<String, Any>)
            } else if(argValue is List<*>){
                // If the value is a list, recursively call newInstance for each element
                convertToType(argValue.map { element ->
                    if (element is Map<*, *>) {
                        yamlParser(parameter.type.arguments.first().type!!.classifier as KClass<*>).newInstance(element as Map<String, Any>)
                    } else {
                        element
                    }
                },
                    parameter.type.classifier as KClass<*>
                )
            } else if (argValue::class == parameter.type.classifier) {
                // i the value is already in the corresponding type just return it
                argValue
            } else {
                // Convert the value to the corresponding type
                convertToType(argValue, parameter.type.classifier as KClass<*>)
            }
        }
        // Call the constructor with the parameters
        return constructor.callBy(parameters)
    }
}

