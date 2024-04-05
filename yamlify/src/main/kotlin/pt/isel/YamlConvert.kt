package pt.isel

interface YamlConverter<T> {
    fun convert(value: String): T
}