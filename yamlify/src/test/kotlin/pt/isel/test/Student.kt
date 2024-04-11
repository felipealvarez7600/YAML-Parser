package pt.isel.test

import pt.isel.YamlDate
import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
import java.time.LocalDate

class Student @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    @YamlArg("city of birth")
    val from: String,
    val address: Address? = null,
    val grades: List<Grade> = emptyList(),
    @YamlConvert(YamlDate::class)
    val birth: LocalDate? = null,
)