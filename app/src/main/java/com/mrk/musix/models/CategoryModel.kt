package com.mrk.musix.models

data class CategoryModel(
    val name : String,
    val coverUrl : String,
    var songs : List<String>
){
    constructor() : this("","", listOf())
}
