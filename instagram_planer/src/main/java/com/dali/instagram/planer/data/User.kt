package com.dali.instagram.planer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User {
    @PrimaryKey
    var id: Long = 0
    var name: String? = null
}