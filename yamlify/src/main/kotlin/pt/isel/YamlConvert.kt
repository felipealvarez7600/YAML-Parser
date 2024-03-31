package pt.isel

import kotlin.reflect.KClass

annotation class YamlConvert(val converter: KClass<out YamlConverter<*>>)

interface YamlConverter<T> {
    fun convert(value: String): T
}