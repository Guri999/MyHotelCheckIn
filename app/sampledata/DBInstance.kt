//package com.example.hotelcheckin
//
//import android.app.Application
//import androidx.room.Room
//import androidx.room.Room.databaseBuilder
//
//class DBInstance : Application() {
//    companion object {
//        lateinit var database: AppDatabase
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        database = databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "reservation_list"
//        ).build()
//    }
//}
