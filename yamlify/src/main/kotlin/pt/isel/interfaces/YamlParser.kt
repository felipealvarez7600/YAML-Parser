package pt.isel.interfaces

import java.io.Reader

interface YamlParser<T> {
    fun parseObject(yaml: Reader): T
    fun parseList(yaml: Reader): List<T>
    fun parseSequence(yaml: Reader): Sequence<T>
}