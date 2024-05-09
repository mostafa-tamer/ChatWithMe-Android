package com.mostafatamer.chatwithme.room;

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mostafatamer.chatwithme.room.dao.FriendsChatDao
import com.mostafatamer.chatwithme.room.entity.FriendsChat


@Database(entities = [(FriendsChat::class)], version = 1)
abstract class CustomerRoomDatabase : RoomDatabase() {
    abstract fun friendsChatDao(): FriendsChatDao

    companion object {
        private var INSTANCE: CustomerRoomDatabase? = null
        fun getInstance(context: Context): CustomerRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CustomerRoomDatabase::class.java,
                        "chat_with_me_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}