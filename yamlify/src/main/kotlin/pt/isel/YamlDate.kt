package pt.isel

import java.time.LocalDate

class YamlDate : YamlConverter<LocalDate> {
    override fun convert(value: String): LocalDate = LocalDate.parse(value)
}