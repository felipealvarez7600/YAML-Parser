package pt.isel.annotations

import YamlAny
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class YamlConvert(val parser: KClass< out YamlAny>)