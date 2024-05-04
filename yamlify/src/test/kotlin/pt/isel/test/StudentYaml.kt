package pt.isel.test

import pt.isel.Details
import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
import java.time.LocalDate

class StudentYaml @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    @YamlArg("city of birth")
    val from: String,
    val address: Address? = null
)