package pt.isel

import java.io.File

class YamlFolderParser<T : Any>(private val parser: AbstractYamlParser<T>) {
    fun parseFolderEager(path: String): List<T> {
        val folder = File(path)
        require(folder.isDirectory) { "Path is not a directory: $path" }
        return folder.listFiles()?.mapNotNull { file ->
            if (file.isFile && file.extension == "yaml") {
                parser.parseObject(file.reader())
            } else {
                null
            }
        } ?: emptyList()
    }

    fun parseFolderLazy(path: String): Sequence<T> {
        val folder = File(path)
        require(folder.isDirectory) { "Path is not a directory: $path" }
        return sequence {
            folder.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "yaml") {
                    yield(parser.parseObject(file.reader()))
                }
            }
        }
    }
}
