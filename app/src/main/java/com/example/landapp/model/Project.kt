package com.example.landapp.model

data class Project(
    var uid : String = "",
    var id : String = "",
    var title : String = "",
    var description: String = "",
    var categoryId: String = "",
    var businessId: String = "",
    var url: String = "",
    var url2: String = "",
    var url3: String = "",
    var timestamp: Long = 0,
    var viewCount : Long = 0,
    var price : String = "",
    var isFavorite : Boolean = false
)