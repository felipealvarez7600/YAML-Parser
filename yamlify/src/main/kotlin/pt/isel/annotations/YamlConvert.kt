package pt.isel.annotations

import pt.isel.YamlCustomParser
import kotlin.reflect.KClass

annotation class YamlConvert(val parser: KClass<out YamlCustomParser<*>>)