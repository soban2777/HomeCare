package com.example.myapplication.DataClasses

import com.google.firebase.database.PropertyName

data class Workers(var id: String = "",
                   val name: String = "",
                   val email: String = "",
                   val username: String = "",
                   val skill: String="",
                   val phone: String="",
                   val about: String = "",
                   val location: String = "",
                   var numberOfReviews: Int = 0,
                   var totalRatingSum: Double = 0.0
    )
{



    // Helper function to calculate average rating safely

}


