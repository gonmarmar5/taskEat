package com.bugastudio.taskeat.utils.model

data class ListData(var id: String, var name:String, var isExpandable: Boolean = false, var nestedList: List<ItemData> = emptyList())