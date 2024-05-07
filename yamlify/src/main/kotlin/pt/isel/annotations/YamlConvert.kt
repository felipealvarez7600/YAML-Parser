package pt.isel.annotations

import pt.isel.interfaces.IYamlAny
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class YamlConvert(val parser: KClass<out IYamlAny<*>>)
