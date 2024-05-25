package pt.isel.test

class StudentSequence @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    val from: String,
    val address: Address? = null,
    val grades: Sequence<Grade> = emptySequence()
)