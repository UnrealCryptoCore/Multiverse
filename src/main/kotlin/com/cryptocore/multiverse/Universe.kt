package com.cryptocore.multiverse

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.random.Random

const val WORLD_SUFFIX: String = "_world"
const val NETHER_SUFFIX: String = "_world_nether"
const val END_SUFFIX: String = "_world_the_end"
const val MULTIVERSE_PREFIX: String = "mv_"

val universes = mutableListOf<Universe>()

class Universe(val name: String = UUID.randomUUID().toString(), val seed: Long = Random.nextLong()) {
    lateinit var world: World
    lateinit var nether: World
    lateinit var end: World

    val worldName = MULTIVERSE_PREFIX + name + WORLD_SUFFIX
    val netherName = MULTIVERSE_PREFIX + name + NETHER_SUFFIX
    val endName = MULTIVERSE_PREFIX + name + END_SUFFIX

    fun createUniverse(dist: Int = 8) {
        world = Bukkit.createWorld(WorldCreator(worldName).seed(seed))!!
        nether = Bukkit.createWorld(WorldCreator(netherName).seed(seed).environment(World.Environment.NETHER))!!
        end = Bukkit.createWorld(WorldCreator(endName).seed(seed).environment(World.Environment.THE_END))!!
        for (x in -dist..<dist) {
            for (y in -dist..<dist) {
                world.getChunkAtAsync(
                    world.spawnLocation.chunk.x + x,
                    world.spawnLocation.chunk.z + y
                )
            }
        }
        universes.add(this)
    }


    fun spawnPlayer(player: Player) {
        resetPlayer(player)
        player.teleport(world.spawnLocation)
    }

    fun cleanupWorld() {
        for (world in worlds()) {
            world.isAutoSave = false
            val b = Bukkit.unloadWorld(world, false) // don't save
            if (!b) {
                MultiverseAPI.logger.warning("Could not delete $name")
            }

            val worldFolder = File(Bukkit.getWorldContainer(), name)
            MultiverseAPI.logger.info(Bukkit.getWorldContainer().absolutePath)
            if (worldFolder.exists()) {
                deleteDirectory(worldFolder)
                MultiverseAPI.logger.info("Deleted world folder: $name")
            }
        }
    }

    fun worlds(): List<World> {
        return listOf(world, nether, end)
    }
}

fun loadUniverses() {
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
        val universe = Universe(name)
        universe.createUniverse()
    }
}

fun getUniverse(world: World): Universe? {
    universes.forEach {
        if (world in it.worlds()) {
            return it
        }
    }
    return null
}

fun getUniverse(name: String): Universe? {
    universes.forEach {
        if (name == it.name) {
            return it
        }
    }
    return null
}

fun getUniverseNameFromWorldName(name: String): String? {
    val split = name.split("_")
    if (split.size < 3) {
        return null
    }
    return split[1]
}