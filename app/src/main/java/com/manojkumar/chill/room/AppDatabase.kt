package com.manojkumar.chill.room
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.manojkumar.chill.data.model.Products
import com.manojkumar.chill.room.dao.ProductDao

@Database(entities = [Products.Product::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun productDao(): ProductDao

    companion object {
        private const val TAG = "AppDatabase"
        private const val DATABASE_NAME = "chill_db"

        // Singleton
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(this) {
                    Log.d(TAG, " >>> Creating new database instance")
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }
            Log.d(TAG, " >>> Getting the database instance")
            return INSTANCE
        }

        fun destroyInstance() {
            Log.d(TAG, " >>> Destroying app database instance")
            INSTANCE = null
        }
    }
}