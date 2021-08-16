package com.example.cis_600_project

import java.io.Serializable

data class User(val uid: String, val username: String, val useremail: String, val profileImageUrl: String, val favorid: String,val sellingid: String,val soldid: String){
    constructor():this("", "", "", "","","","")
}

data class item
    (
    val itemid:String,
    val ItemImageUrl:String,
    val sellerid:String,
    val title:String,
    val price:String,
    val location:String,
    val description:String,
    val delete: Boolean

): Serializable
{
    constructor():this("","","","","","","",false)
}