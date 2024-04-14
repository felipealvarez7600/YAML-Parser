package pt.isel.interfaces

import java.net.URL
import java.time.LocalDate
interface IYamlAny {
    fun convert(input: String, typeName: String): Any?

}
