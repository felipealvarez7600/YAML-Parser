package pt.isel.annotations

import pt.isel.YamlConverter
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class YamlConvert(val converter: KClass<out YamlConverter<*>>)
