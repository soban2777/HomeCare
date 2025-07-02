package com.example.myapplication.DataClasses

data class customerprofile(var id: String = "",
                           val name: String="",
                           val email: String="",
                           val username: String="",
                           val password: String="",
                           val phone: String="",
                           val location: String = "city, country"
    )
