package pt.isel.test

import pt.isel.Details
import pt.isel.UrlComponents
import pt.isel.YamlAny
import pt.isel.annotations.YamlConvert
import java.time.LocalDate

class Student @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    val from: String,
    val address: Address? = null,
    val grades: List<Grade> = emptyList()
)