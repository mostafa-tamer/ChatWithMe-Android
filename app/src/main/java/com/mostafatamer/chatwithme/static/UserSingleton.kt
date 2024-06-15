package com.mostafatamer.chatwithme.static

import com.mostafatamer.chatwithme.network.entity.dto.UserDto

class UserSingleton {
    companion object {
        @Volatile
        private var instance: UserDto? = null
        fun getInstance(userDto: UserDto? = null): UserDto {
            if (instance == null && userDto == null)
                throw IllegalStateException("instance is null")

            return instance ?: synchronized(this) {
                userDto.also { instance = userDto }!!
            }
        }
    }
}