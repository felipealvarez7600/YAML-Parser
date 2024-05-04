package pt.isel.cojen.code

import pt.isel.AbstractYamlParser
import pt.isel.cojen.types.Classroom2
import pt.isel.cojen.types.Student2
import kotlin.reflect.KClass

//class Classroom2(val id: String, val students: List<Student2>)

//class YamlParserClassroom2 : AbstractYamlParser<Classroom2>(Classroom2::class) {
//    override fun <T : Any> yamlParser(type: KClass<T>): AbstractYamlParser<T> {
//        TODO("Not yet implemented")
//    }
//
////    class Student2 @JvmOverloads constructor (
////        val name: String,
////        val nr: Int,
////        val from: String
////        val address: Address2? = null,
////        val grades: List<Grade2> = emptyList()
////    )
//
//    override fun newInstance(args: Map<String, Any>): Classroom2 {
//        val id: String = args["id"] as String
////        val students: Student2 =
//        // TODO: students
////        return Classroom2(id, students)
//    }
//}