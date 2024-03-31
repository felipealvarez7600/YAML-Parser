package pt.isel


import java.util.UUID

class YamlUUID : YamlConverter<UUID> {
    override fun convert(value: String): UUID = UUID.fromString(value)
}