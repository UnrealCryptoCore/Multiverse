package com.cryptocore.multiverse

import com.cryptocore.multiverse.events.MultiverseEventHandler
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

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

    private fun loadUniverses() {
        val map = mutableMapOf<String, MutableList<String>>()
        Bukkit.getWorldContainer().listFiles().forEach {
            if (!it.name.startsWith(MULTIVERSE_PREFIX)) {
                return@forEach
            }
            val name = getUniverseNameFromWorldName(it.name) ?: return@forEach
            val e = map.getOrPut(name) { mutableListOf() }
            e.add(it.name)
        }


        map.forEach { (name, worldNames) ->
            if (worldNames.size < 3) {
                return@forEach
            }
            Universe(name)
            logger.info("Loaded universe $name.")
        }
    }

    @JvmStatic
    fun getUniverse(world: World): Universe? {
        universes.forEach {
            if (world in it.worlds()) {
                return it
            }
        }
        return null
    }

    @JvmStatic
    fun getUniverse(name: String): Universe? {
        universes.forEach {
            if (name == it.name) {
                return it
            }
        }
        return null
    }

    @JvmStatic
    fun createOrLoadUniverse(name: String, seed: Long = Random.nextLong()): Universe {
        var u = getUniverse(name)
        if (u != null) return u
        u = Universe(name, seed)
        return u
    }
}