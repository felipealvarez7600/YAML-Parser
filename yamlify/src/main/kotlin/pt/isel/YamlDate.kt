package pt.isel

import java.time.LocalDate
class YamlDate {
    fun convert(input: String): LocalDate? {
        val datePattern = "\\{year=(\\d{4}), month=(\\d{2}), day=(\\d{2})}".toRegex()
        val matchResult = datePattern.find(input) ?: throw IllegalArgumentException("Invalid date format: $input")
        val (year, month, day) = matchResult.destructured
        return LocalDate.of(year.toInt(), month.toInt(), day.toInt())
    }
}
