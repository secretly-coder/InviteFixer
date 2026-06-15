package com.github.ribro.invitefixer

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PreHook

@AliucordPlugin
class InviteFixer : Plugin() {
    override fun start(context: Context) {
        val inviteCodeClass = Class.forName("com.discord.stores.StoreInviteSettings\$InviteCode")

        inviteCodeClass.declaredConstructors
            .filter { constructor ->
                val types = constructor.parameterTypes
                types.size >= 3 &&
                    types[0] == String::class.java &&
                    types[1] == String::class.java &&
                    types[2] == java.lang.Long::class.java
            }.forEach { constructor ->
                patcher.patch(
                    constructor,
                    PreHook { param ->
                        val originalCode = param.args[0] as? String ?: return@PreHook
                        val normalizedCode = normalizeInviteCode(originalCode)

                        if (normalizedCode != originalCode || param.args[2] != null) {
                            param.args[0] = normalizedCode
                            // v126 sends scheduled-event IDs with invite joins. Discord can reject
                            // those legacy requests even though joining the guild itself is valid.
                            param.args[2] = null
                            logger.info("Normalized invite code before opening the join sheet")
                        }
                    },
                )
            }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

    internal fun normalizeInviteCode(value: String): String {
        val withoutFragment = value.substringBefore('#')
        val withoutQuery = withoutFragment.substringBefore('?')
        val withoutTrailingSlash = withoutQuery.trim().trimEnd('/')

        return withoutTrailingSlash
            .substringAfterLast('/')
            .removePrefix("+")
            .ifEmpty { value }
    }
}
