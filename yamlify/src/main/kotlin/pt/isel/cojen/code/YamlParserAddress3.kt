package pt.isel.cojen.code

import pt.isel.AbstractYamlParser
import pt.isel.cojen.types.Address2
import kotlin.reflect.KClass

class YamlParserAddress3 : AbstractYamlParser<Address2>(Address2::class) {
    override fun <T : Any> yamlParser(type: KClass<T>): AbstractYamlParser<T> {
        TODO("Not yet implemented")
    }

    //class Address2(val street: String, val nr: Int, val city: String)

    override fun newInstance(args: Map<String, Any>): Address2 {
        val street: String = args["street"] as String
        val nr: Int = (args["nr"] as String).toInt()
        val city: String = args["city"] as String

        return Address2(street, nr, city)
    }
}