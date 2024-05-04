package pt.isel.cojen.code

import pt.isel.AbstractYamlParser
import pt.isel.cojen.types.Grade2
import kotlin.reflect.KClass


//class Grade2(val subject: String, val classification: Int)

class YamlParserGrade2 : AbstractYamlParser<Grade2>(Grade2::class) {
    override fun <T : Any> yamlParser(type: KClass<T>): AbstractYamlParser<T> {
        TODO("Not yet implemented")
    }

    override fun newInstance(args: Map<String, Any>): Grade2 {
        val subject: String = args["subject"] as String
        val classification: Int = (args["classification"] as String).toInt()

        return Grade2(subject, classification)
    }
}