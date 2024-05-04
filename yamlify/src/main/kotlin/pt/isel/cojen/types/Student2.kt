package pt.isel.cojen.types

class Student2 @JvmOverloads constructor (
    val name: String,
    val nr: Int,
    val from: String,
    val address: Address2? = null,
    val grades: List<Grade2> = emptyList(),
)