package pt.isel.cojen.code

import pt.isel.AbstractYamlParser
import pt.isel.YamlParserCojen
import pt.isel.cojen.types.Address2
import pt.isel.cojen.types.Student2
import kotlin.reflect.KClass

class YamlParserStudent4 : AbstractYamlParser<Student2>(Student2::class) {
    override fun <T : Any> yamlParser(type: KClass<T>): AbstractYamlParser<T> {
        TODO("Not yet implemented")
    }

//    class Student2 @JvmOverloads constructor (
//        val name: String,
//        val nr: Int,
//        val from: String
//        val address: Address2? = null,
//    )

    override fun newInstance(args: Map<String, Any>): Student2 {
        val name: String = args["street"] as String
        val nr: Int = (args["nr"] as String).toInt()
        val from: String = args["from"] as String
        val addressArgs: Map<String, Any> = args["address"] as Map<String, Any>
        val addressYamlParser: AbstractYamlParser<Address2> = YamlParserAddress3()
        val address: Address2 = addressYamlParser.newInstance(addressArgs)
        return Student2(name, nr, from, address)
    }
}