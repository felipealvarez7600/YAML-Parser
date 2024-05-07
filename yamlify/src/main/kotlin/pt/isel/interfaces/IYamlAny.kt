package pt.isel.interfaces

interface IYamlAny<T> {
    fun convert(input: String, typeName: String): T?

}