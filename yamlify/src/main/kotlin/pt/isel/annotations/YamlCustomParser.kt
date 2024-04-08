package pt.isel.annotations

interface YamlCustomParser<T> {
    fun parse(value: String): T
}