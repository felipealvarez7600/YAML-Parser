package pt.isel.interfaces

interface IYamlAny {
    fun convert(input: String, typeName: String): Any?

}