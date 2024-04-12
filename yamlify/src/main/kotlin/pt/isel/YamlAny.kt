package pt.isel

import java.time.LocalDate
class YamlAny {
    fun convert(input: String): LocalDate? = LocalDate.parse(input.drop(1).dropLast(1).split(", ").joinToString("-") { it.split("=")[1] })
}
