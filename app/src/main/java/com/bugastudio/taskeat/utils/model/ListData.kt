package com.bugastudio.taskeat.utils.model

data class ListData(var listId:String, var list:String, var nestedList: List<ToDoData> = emptyList(), var isExpandable: Boolean = false){


}