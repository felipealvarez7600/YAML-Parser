package pt.isel.test

import pt.isel.annotations.YamlArg

class Student @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    @YamlArg("city of birth")
    val from: String,
    val address: Address? = null,
    val grades: List<Grade> = emptyList()
)