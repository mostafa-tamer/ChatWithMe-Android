package com.mostafatamer.chatwithme.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mostafatamer.chatwithme.room.entity.FriendsChat


@Dao
interface FriendsChatDao {
    @Query("SELECT * FROM friendschat")
    fun getAll(): List<FriendsChat>

    @Query("SELECT * FROM friendschat WHERE uid IN (:friendsChatsIds)")
    fun loadAllByIds(friendsChatsIds: IntArray): List<FriendsChat>

//    @Query("SELECT * FROM friendsChat WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): FriendsChat

    @Insert
    fun insertAll(vararg friendsChats: FriendsChat)

    @Delete
    fun delete(friendsChats: FriendsChat)
}
