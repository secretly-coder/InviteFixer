package com.github.Ribro

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin

@AliucordPlugin
class InviteFixer : Plugin() {

    override fun start(context: Context) {
        // Plugin initialized successfully
        // Agle step me hum yahan join logic hook karenge
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}