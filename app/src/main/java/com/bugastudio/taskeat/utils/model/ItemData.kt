package com.bugastudio.taskeat.utils.model

data class ItemData(var id:String, var name:String, var categoryId:String? ){
    companion object {
        private var currentId = 1

        fun generateId(): String {
            currentId++
            return currentId.toString()
        }
    }

    constructor(name: String, categoryId: String?) : this(generateId(), name, categoryId)

    constructor() : this("", "", "")

}