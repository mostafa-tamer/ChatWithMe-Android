package com.mostafatamer.chatwithme.static

import com.mostafatamer.chatwithme.network.entity.dto.UserDto

class AppUser {
    companion object {
        private lateinit var instance: UserDto
        fun getInstance(userDto: UserDto? = null): UserDto {
            return if (Companion::instance.isInitialized && userDto == null) {
                instance
            } else {
                userDto!!.also { instance = userDto }
            }
        }
    }
}