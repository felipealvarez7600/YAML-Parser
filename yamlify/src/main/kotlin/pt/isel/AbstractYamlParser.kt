package pt.isel

import java.io.Reader
import kotlin.reflect.KClass

abstract class AbstractYamlParser<T : Any>(private val type: KClass<T>) : YamlParser<T> {
    /**
     * Used to get a parser for other Type using this same parsing approach.
     */
    abstract fun <T : Any> yamlParser(type: KClass<T>) : AbstractYamlParser<T>
    /**
     * Creates a new instance of T through the first constructor
     * that has all the mandatory parameters in the map and optional parameters for the rest.
     */
    abstract fun newInstance(args: Map<String, Any>): T


    final override fun parseObject(yaml: Reader): T {
        val yamlLinesList = yaml.readLines().toMutableList()
        if (yamlLinesList.isEmpty()) throw IllegalArgumentException("Empty yaml")
        val iteration = iterateOverObject(yamlLinesList, -1)
        return newInstance(iteration)
    }

    private fun iterateOverObject(yamlLinesList: MutableList<String>, indentCounter: Int) : Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val possibleList = mutableListOf<String>()
        while (yamlLinesList.isNotEmpty()) {
            val line = yamlLinesList.removeAt(0)
            if (line.isBlank() || line.isEmpty()) continue
            val indentCounterNew = line.takeWhile { it == ' ' }.length
            if (indentCounterNew <= indentCounter) {
                yamlLinesList.add(0, line)
                break
            }
            val (key, value) = line.split(":").map { it.trim() }
            if (value.isBlank() || value.isEmpty()) {
                if(yamlLinesList.first().contains("-")) {
                    val currentListIndent = yamlLinesList.first().takeWhile { it == ' ' }.length
                    while(yamlLinesList.isNotEmpty() && currentListIndent <= yamlLinesList.first().takeWhile { it == ' ' }.length) {
                        possibleList.add(yamlLinesList.removeAt(0))
                    }
                    val list = iterateOverList(possibleList)
                    map[key] = list
                } else {
                    val newValue = iterateOverObject(yamlLinesList, indentCounterNew)
                    map[key] = newValue
                    yamlLinesList.drop(newValue.size)
                }
            } else {
                map[key] = value
            }
        }
        return map
    }

    override fun parseList(yaml: Reader): List<T> {
        val yamlLinesList = yaml.readLines().toMutableList()
        val finalList = iterateOverList(yamlLinesList)
        return finalList.map { if(it is Map<*, *>) newInstance(it as Map<String, Any>) else it as T}
    }

    private fun iterateOverList(yamlLinesList: MutableList<String>) : List<Any> {
        val newObject = mutableListOf<String>()
        val finalList = mutableListOf<Any>()
        while(yamlLinesList.isNotEmpty()) {
            val line = yamlLinesList.removeAt(0)
            if(line.contains("-")) {
                val indentCounterNew = line.takeWhile { it == ' ' }.length
                val (key, value) = line.split("-").map { it.trim() }
                if(value.isBlank() || value.isEmpty()) {
                    while (indentCounterNew < yamlLinesList.first().takeWhile { it == ' ' }.length){
                        newObject.add(yamlLinesList.removeAt(0))
                        if (yamlLinesList.isEmpty()) break
                    }
                    val obj = iterateOverObject(newObject, -1)
                    finalList.add(obj)
                } else finalList.add(convertToType(value, type))
            }

        }
        return finalList
    }

    fun convertToType(argValue: Any, type: KClass<*>): Any {
        return when {
            type == String::class -> argValue
            type == Int::class -> argValue.toString().toInt()
            type == Long::class -> argValue.toString().toLong()
            type == Double::class -> argValue.toString().toDouble()
            type == Float::class -> argValue.toString().toFloat()
            type == List::class -> argValue as List<*>
            type == Sequence::class && argValue is Iterable<*> -> argValue.asSequence()
            else -> throw IllegalArgumentException("Unsupported type $type")
        }
    }

}
