package com.cryptocore.multiverse

import com.cryptocore.multiverse.events.MultiverseEventHandler
import org.bukkit.plugin.java.JavaPlugin

object MultiverseAPI {
    private var initialized = false
    lateinit var logger: java.util.logging.Logger

    @JvmStatic
    fun init(plugin: JavaPlugin) {
        if (initialized) return

        logger = plugin.logger
        logger.info("Loading Universes...")
        plugin.server.pluginManager.registerEvents(MultiverseEventHandler(), plugin)
        loadUniverses()

        initialized = true
    }
}