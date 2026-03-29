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

class Universe {
    constructor(
        name: String = UUID.randomUUID().toString(),
        seed: Long = Random.nextLong()
    ) {
        this.name = name
        this.seed = seed
        worldName = MULTIVERSE_PREFIX + name + WORLD_SUFFIX
        netherName = MULTIVERSE_PREFIX + name + NETHER_SUFFIX
        endName = MULTIVERSE_PREFIX + name + END_SUFFIX

        val universe = MultiverseAPI.getUniverse(name)
        if (universe != null) {
            throw RuntimeException("Universe already exists.")
        }
        loadUniverse()
    }

    val name: String
    var seed: Long = 0
    lateinit var world: World
    lateinit var nether: World
    lateinit var end: World

    val worldName: String
    val netherName: String
    val endName: String

    private fun loadUniverse(dist: Int = 8) {
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

    fun deleteUniverse() {
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

fun getUniverseNameFromWorldName(name: String): String? {
    val split = name.split("_")
    if (split.size < 3) {
        return null
    }
    if (split[0] + "_" != MULTIVERSE_PREFIX) {
        return null
    }
    return split[1]
}