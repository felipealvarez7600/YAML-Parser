package pt.isel

import pt.isel.interfaces.YamlParser
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

    /**
     * Parses a yaml object into a T object.
     * The function just passes the yaml to a mutable List and calls the iterateOverObject function to handle the parsing and finally calls the newInstance.
     */
    final override fun parseObject(yaml: Reader): T {
        val yamlLinesList = yaml.readLines()
        require(yamlLinesList.isNotEmpty()) { "Empty yaml" }
        val iteration = iterateOverObject(yamlLinesList, -1, 0)
        return newInstance(iteration.first)
    }

    /**
     * Function that iterates over the yaml object and creates a map with the key value pairs.
     * The function is recursive and calls itself when it finds a new object inside the object.
     * The function also calls the iterateOverList function when it finds a list inside the object.
     */
    private fun iterateOverObject(yamlLinesList: List<String>, indentCounter: Int, lineIndex: Int) : Pair<Map<String, Any>, Int> {
        val map = mutableMapOf<String, Any>()
        var index = lineIndex
        while(index < yamlLinesList.size){
            val line = yamlLinesList[index++]
            // Skip empty lines
            if (line.isBlank()) continue
            val indentCounterNew = line.indexOfFirst { it != ' ' }
            // If the indentCounterNew is less than the indentCounter it means that the object has ended.
            if (indentCounterNew < indentCounter) {
                break
            }
            val (key, value) = line.split(":").map { it.quickTrim() }
            // Check if the value is blank, if it isn't, it means that it's a normal pair to add to the map.
            if(value.isNotBlank()){
                map[key] = value
            } else {
                // Check if the value is a list or an object.
                if(!yamlLinesList[index].contains("-")){
                    val currentIndent = yamlLinesList[index].indexOfFirst { it != ' ' }
                    val newValue = iterateOverObject(yamlLinesList, currentIndent, index)
                    index = newValue.second - 1
                    map[key] = newValue.first
                } else {
                    val currentIndent = yamlLinesList[index + 1].indexOfFirst { it != ' ' }
                    val list = iterateOverList(yamlLinesList, currentIndent, index)
                    index = if(yamlLinesList.size > list.second) list.second - 1 else list.second
                    map[key] = list.first
                }
            }
        }
        return map to index
    }

    /**
     * Function that iterates over the yaml list and creates a list of objects.
     * The function passes the yaml to a mutable List and calls the iterateOverList function to handle the parsing.
     */
    override fun parseList(yaml: Reader): List<T> {
        val yamlLinesList = yaml.readLines()
        return iterateOverList(yamlLinesList, -1, 0).first.map { newInstance(it) }

    }

    /**
     * Function that iterates over the yaml list and creates a list of Any.
     * The function calls the iterateOverObject function when it finds a new object inside the list.
     */
    private fun iterateOverList(yamlLinesList: List<String>, indentCounter: Int, lineIndex:Int) : Pair<List<Map<String, Any>>, Int> {
        val finalList = mutableListOf<Map<String, Any>>()
        var index = lineIndex
        while(index < yamlLinesList.size){
            val line = yamlLinesList[index++]
            // Skip empty lines
            if (line.isBlank()) continue
            val currentIndent = yamlLinesList[index].indexOfFirst { it != ' ' }
            if(currentIndent < indentCounter) break
            if(line.contains("-")){
                val value = line.split("-").last().quickTrim()
                if(value.isNotBlank()){
                    finalList.add(mapOf("#" to value))
                } else {
                    val obj = iterateOverObject(yamlLinesList, currentIndent, index)
                    index = if(yamlLinesList.size > obj.second) obj.second - 1 else obj.second
                    finalList.add(obj.first)
                }
            }
        }
        return finalList to index
    }

    override fun parseSequence(yaml: Reader): Sequence<T> = sequence {
            val yamlLinesList = yaml.readLines()
            require(yamlLinesList.isNotEmpty()) { "Empty yaml" }
            require(yamlLinesList.any { it.trim().startsWith("-") }) { "YAML root element is not a list" }

            val parsedList = iterateOverList(yamlLinesList, -1, 0).first

            parsedList.forEach { yield(newInstance(it)) }
        }


    private fun String.quickTrim() : String {
        var start = 0
        var end = length
        while (start < end && this[start] == ' ') start++
        while (end > start && this[end - 1] == ' ') end--
        return substring(start, end)
    }
}
