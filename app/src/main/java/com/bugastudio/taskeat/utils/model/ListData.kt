package com.bugastudio.taskeat.utils.model

data class ListData(var id: String, var name:String, var isExpandable: Boolean = false, var nestedList: List<ItemData> = emptyList()) {
    companion object {
        private var currentId = 1

        fun generateId(): String {
            currentId++
            return currentId.toString()
        }
    }

    constructor(name: String) : this(generateId(), name, false, emptyList())
    constructor() : this("", "", false, emptyList())

}