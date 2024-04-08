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

    /**
     * Parses a yaml object into a T object.
     * The function just passes the yaml to a mutable List and calls the iterateOverObject function to handle the parsing and finally calls the newInstance.
     */
    final override fun parseObject(yaml: Reader): T {
        val yamlLinesList = yaml.readLines().toMutableList()
        if (yamlLinesList.isEmpty()) throw IllegalArgumentException("Empty yaml")
        val iteration = iterateOverObject(yamlLinesList, -1)
        return newInstance(iteration)
    }

    /**
     * Function that iterates over the yaml object and creates a map with the key value pairs.
     * The function is recursive and calls itself when it finds a new object inside the object.
     * The function also calls the iterateOverList function when it finds a list inside the object.
     */
    private fun iterateOverObject(yamlLinesList: MutableList<String>, indentCounter: Int) : Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val possibleList = mutableListOf<String>()
        while (yamlLinesList.isNotEmpty()) {
            val line = yamlLinesList.removeAt(0)
            // Skip empty lines
            if (line.isBlank() || line.isEmpty()) continue
            val indentCounterNew = line.takeWhile { it == ' ' }.length
            // If the indentCounterNew is less than the indentCounter it means that the object has ended.
            if (indentCounterNew <= indentCounter) {
                yamlLinesList.add(0, line)
                break
            }
            val (key, value) = line.split(":").map { it.trim() }
            // Check if the value is empty or blank, if it is, it means that it's a normal pair to add to the map or list.
            if (value.isBlank() || value.isEmpty()) {
                // Check if the value is a list or an object.
                if(yamlLinesList.first().contains("-")) {
                    val currentListIndent = yamlLinesList.first().takeWhile { it == ' ' }.length
                    // Iterate over the lines to get the complete list.
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

    /**
     * Function that iterates over the yaml list and creates a list of objects.
     * The function passes the yaml to a mutable List and calls the iterateOverList function to handle the parsing.
     */
    override fun parseList(yaml: Reader): List<T> {
        val yamlLinesList = yaml.readLines().toMutableList()
        val finalList = iterateOverList(yamlLinesList)
        // If it's a map then call the newInstance function to create the object and if not just return the value as T.
        return finalList.map { if(it is Map<*, *>) newInstance(it as Map<String, Any>) else it as T}
    }

    /**
     * Function that iterates over the yaml list and creates a list of Any.
     * The function calls the iterateOverObject function when it finds a new object inside the list.
     */
    private fun iterateOverList(yamlLinesList: MutableList<String>) : List<Any> {
        val newObject = mutableListOf<String>()
        val finalList = mutableListOf<Any>()
        while(yamlLinesList.isNotEmpty()) {
            val line = yamlLinesList.removeAt(0)
            // If the line does not contain "-" skip it since it's not a list.
            if(line.contains("-")) {
                val indentCounterNew = line.takeWhile { it == ' ' }.length
                val (key, value) = line.split("-").map { it.trim() }
                // Check if the value is empty or blank, if it is, it means that it's a new object and if not it's a simple value.
                if(value.isBlank() || value.isEmpty()) {
                    // Iterate over the lines to get the complete object.
                    while (indentCounterNew < yamlLinesList.first().takeWhile { it == ' ' }.length){
                        newObject.add(yamlLinesList.removeAt(0))
                        if (yamlLinesList.isEmpty()) break
                    }
                    // Call the iterateOverObject function to get the object and add it to the list
                    val obj = iterateOverObject(newObject, -1)
                    finalList.add(obj)
                } else finalList.add(value)
                // else finalList.add(convertToType(value, type))
            }
        }
        return finalList
    }
}
