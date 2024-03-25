package pt.isel

import java.io.Reader
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberExtensionProperties
import kotlin.reflect.full.memberProperties

abstract class AbstractYamlParser<T : Any>(private val type: KClass<T>) : YamlParser<T> {
    /**
     * Used to get a parser for other Type using this same parsing approach.
     */
    abstract fun <T : Any> yamlParser(type: KClass<T>) : AbstractYamlParser<T>
    /**
     * Creates a new instance of T through the first constructor
     * that has all the mandatory parameters in the map and optional parameters for the rest.
     */
    abstract fun newInstance(args: Map<String, Any>): T


    final override fun parseObject(yaml: Reader): T {
        val map = yaml.readLines().mapNotNull {
            if (it.isBlank()) {
                return@mapNotNull null
            }
            val (key, value) = it.split(":")
            if (it.contains(":")) {
                key.trim() to value.trim()
            } else {
                null
            }
        }.toMap()
        return newInstance(map)
    }

    final override fun parseList(yaml: Reader): List<T> {
        TODO("Not yet implemented")
    }



}
