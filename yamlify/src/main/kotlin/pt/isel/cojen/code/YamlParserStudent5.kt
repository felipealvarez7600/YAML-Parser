package pt.isel.cojen.code

import pt.isel.AbstractYamlParser
import pt.isel.cojen.types.Student2
import kotlin.reflect.KClass

class YamlParserStudent5 : AbstractYamlParser<Student2>(Student2::class) {
    override fun <T : Any> yamlParser(type: KClass<T>): AbstractYamlParser<T> {
        TODO("Not yet implemented")
    }

//    class Student2 @JvmOverloads constructor (
//        val name: String,
//        val nr: Int,
//        val from: String
//        val address: Address2? = null,
//        val grades: List<Grade2> = emptyList()
//    )

    override fun newInstance(args: Map<String, Any>): Student2 {
        val name: String = args["street"] as String
        val nr: Int = (args["nr"] as String).toInt()
        val from: String = args["from"] as String
        // TODO: address and grades
        return Student2(name, nr, from)
    }
}