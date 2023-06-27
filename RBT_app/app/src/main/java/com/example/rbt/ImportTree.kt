package com.example.rbt

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class NodeData(
    val key : String? = null,
    val keyParent : String? = "P",
    val keyLeft : String? = "L",
    val keyRight : String? = "R",
    val treePosition : String? = null,
    val color : String? = null)

class ImportTree {
    fun getTree(string:String): List<NodeData>? {
        if(string.isNotEmpty()) {
            return Gson().fromJson(string, object : TypeToken<List<NodeData>>() {}.type)
        }
        return null
    }
}