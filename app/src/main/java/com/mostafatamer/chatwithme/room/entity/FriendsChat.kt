package com.mostafatamer.chatwithme.room.entity
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class FriendsChat(
    @PrimaryKey val uid: Int,
    val tag: String,
    var username: String,
    var nickname: String,
)
