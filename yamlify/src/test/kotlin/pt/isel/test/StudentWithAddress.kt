package pt.isel.test

class StudentWithAddress @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    val from: String,
    val address: Address? = null
)