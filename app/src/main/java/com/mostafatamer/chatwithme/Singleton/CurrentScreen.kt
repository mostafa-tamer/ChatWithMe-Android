package com.mostafatamer.chatwithme.Singleton

import com.mostafatamer.chatwithme.enumeration.Screens

class CurrentScreen {
    companion object {
        var screen: Screens? = null
    }
}