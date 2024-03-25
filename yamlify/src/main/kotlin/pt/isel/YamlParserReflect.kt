package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * A YamlParser that uses reflection to parse objects.
 */
class YamlParserReflect<T : Any>(type: KClass<T>) : AbstractYamlParser<T>(type) {
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
        // Get the primary constructor of the current class (YamlParserReflect)
        val constructor = this::class.primaryConstructor ?: error("Primary constructor not found")
        val params = constructor.parameters

        // Use reflection to get the actual type parameter (T) of the class
        val typeParameter = this::class.supertypes.find { it.classifier == AbstractYamlParser::class }!!.arguments[0].type

        // Cast the type parameter to KClass<T>
        val kClass = typeParameter.asReified<KClass<T>>()

        val argsArray = params.map { param ->
            args[param.name] ?: error("Missing parameter ${param.name}")
        }.toTypedArray()

        // Use reflection to get the primary constructor of the actual type (T)
        val actualConstructor = kClass.primaryConstructor ?: error("Primary constructor not found for $kClass")

        // Call the constructor of the actual type with the arguments
        return actualConstructor.call(*argsArray) as T
    }

    private fun castArgValue(value: Any, targetType: KClass<*>): Any {
        return targetType.javaObjectType.cast(value)
    }
}

