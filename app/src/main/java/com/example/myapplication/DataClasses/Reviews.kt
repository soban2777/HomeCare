package com.example.myapplication.DataClasses

import android.media.Rating

data class Reviews(
    val reviewId: String="",
    val reviewerId: String="",
    val reviewerName: String="",
    val rating:Float= 0f,
    val reviewTime:com.google.firebase.Timestamp?=null,
    val reviewText:String="",
    val workerId:String=""



) {

}
