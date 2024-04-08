package pt.isel

interface YamlCustomParser<T> {
    fun parse(value: String): T
}