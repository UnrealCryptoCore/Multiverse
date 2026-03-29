package com.cryptocore.multiverse.events

import com.cryptocore.multiverse.getUniverse
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent

class MultiverseEventHandler : Listener {
    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (event.isBedSpawn || event.isAnchorSpawn) {
            return
        }
        val world =
            when (event.respawnReason) {
                PlayerRespawnEvent.RespawnReason.PLUGIN -> return
                PlayerRespawnEvent.RespawnReason.DEATH -> event.player.lastDeathLocation!!.world
                PlayerRespawnEvent.RespawnReason.END_PORTAL -> event.player.world
            }

        val universe = getUniverse(world) ?: return
        event.setRespawnLocation(universe.world.spawnLocation)
    }

    @EventHandler
    fun onPortalEnter(event: PlayerPortalEvent) {
        val fromWorld = event.from.world
        val universe = getUniverse(fromWorld) ?: return

        val target = when (event.cause) {
            PlayerTeleportEvent.TeleportCause.NETHER_PORTAL -> {
                if (fromWorld.environment == World.Environment.NETHER) {
                    universe.world
                } else {
                    universe.nether
                }
            }

            PlayerTeleportEvent.TeleportCause.END_PORTAL -> {
                if (fromWorld.environment == World.Environment.THE_END) {
                    universe.world
                } else {
                    universe.end
                }
            }

            else -> {
                null
            }
        }

        if (target != null) {
            event.to.world = target
            event.to.setWorld(target)
        }
    }
}