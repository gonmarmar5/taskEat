package com.bugastudio.taskeat.utils.model

data class ItemData(var id:String, var name:String){
    companion object {
        private var currentId = 1

        fun generateId(): String {
            currentId++
            return currentId.toString()
        }
    }

    constructor(name: String) : this(generateId(), name)

    constructor() : this("", "")

}