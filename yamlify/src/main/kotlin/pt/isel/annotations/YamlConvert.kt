package pt.isel.annotations

import pt.isel.YamlAny
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class YamlConvert(val parser: KClass< out YamlAny>)
