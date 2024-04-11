package pt.isel.annotations

import pt.isel.YamlDate
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class YamlConvert(val parser: KClass<YamlDate>)
