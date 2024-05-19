package pt.isel

import pt.isel.interfaces.IYamlAny
import java.net.URI
import java.time.LocalDate

class YamlToDate : IYamlAny<LocalDate> {
    companion object {
        var count = 0
    }
    override fun convert(input: String, typeName: String) : LocalDate {
        count++
        return LocalDate.parse(input.drop(1).dropLast(1).split(", ").joinToString("-") { it.split("=")[1] })
    }
}

class YamlToDetails : IYamlAny<Details> {
    companion object {
        var count = 0
    }
    override fun convert(input: String, typeName: String) : Details {
        count++
        val subjectsMap = mutableMapOf<String, Any?>()
        input.drop(1).dropLast(1).split(", ").forEach { entry ->
            val keyValue = entry.split("=")
            val key = keyValue[0]
            val value = if (key != "asFinished") keyValue[1].toIntOrNull() else keyValue[1].toBoolean()
            subjectsMap[key] = value
        }

        return Details(
            age = subjectsMap["age"] as Int?,
            height = subjectsMap["height"] as Int?,
            year = subjectsMap["year"] as Int?,
            asFinished = subjectsMap["asFinished"] as Boolean?,
        )
    }
}

class YamlToUrlComponents : IYamlAny<UrlComponents> {
    companion object {
        var count = 0
    }
    override fun convert(input: String, typeName: String) : UrlComponents {
        count++
        val uri = URI(input)
        val url = uri.toURL()
        return UrlComponents(
            protocol = uri.scheme,
            host = uri.host,
            port = uri.port.takeIf { it != -1 } ?: url.defaultPort,
            path = uri.path,
            query = uri.query,
            ref = uri.fragment
        )
    }
}

data class UrlComponents(
    val protocol: String?,
    val host: String?,
    val port: Int?,
    val path: String?,
    val query: String?,
    val ref: String?
)

data class Details(
    val age: Int? = null,
    val height: Int? = null,
    val year: Int? = null,
    val asFinished: Boolean? = null
)