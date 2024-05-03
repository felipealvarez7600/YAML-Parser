package pt.isel.test

import pt.isel.Details
import pt.isel.UrlComponents
import pt.isel.YamlAny
import pt.isel.annotations.YamlArg
import pt.isel.annotations.YamlConvert
import java.time.LocalDate

class NewStudent @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    @YamlArg("city of birth")
    val from: String,
    val address: Address? = null,
    val grades: List<Grade> = emptyList(),
    @YamlConvert(YamlAny::class)
    val birth: LocalDate? = null,
    @YamlConvert(YamlAny::class)
    val details: Details? = null,
)