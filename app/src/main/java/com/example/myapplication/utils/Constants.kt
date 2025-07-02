package com.example.myapplication.utils

import com.example.myapplication.DataClasses.userType
import java.util.UUID

object Constants {

    var getData = 0
   var move = 0
    var userType=0
    var uniqueId:String?= UUID.randomUUID().toString()

    var user:userType?=null
}