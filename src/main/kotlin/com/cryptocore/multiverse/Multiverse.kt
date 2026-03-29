package com.cryptocore.multiverse

import com.cryptocore.multiverse.commands.buildCommand
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

class Multiverse : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        MultiverseAPI.init(this)
        initCommands()
    }

    fun initCommands() {
        this.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()
            commands.register(buildCommand())
        }
    }

    override fun onDisable() {}

    companion object {
        lateinit var plugin: Multiverse
    }
}
