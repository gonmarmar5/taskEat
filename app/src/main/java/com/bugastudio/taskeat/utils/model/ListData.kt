package com.bugastudio.taskeat.utils.model

data class ListData(var id: Int, var name:String, var isExpandable: Boolean = false, var nestedList: List<ItemData> = emptyList())