package com.example.hotelcheckin

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

//
//@Entity (tableName = "reservation")

data class ReserveList(
//    @PrimaryKey(autoGenerate = true)
//    val userId: Long =0 ,
    val people: People,
    var roomnum: Int,
    var checkin: LocalDate,
    var checkout: LocalDate,
    val price: Int
)
