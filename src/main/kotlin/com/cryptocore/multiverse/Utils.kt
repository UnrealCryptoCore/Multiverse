package com.cryptocore.multiverse

import org.bukkit.Bukkit
import org.bukkit.advancement.Advancement
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import java.io.File
import java.util.function.Consumer


fun resetPlayer(player: Player) {
    player.inventory.clear()
    player.enderChest.clear()

    player.health = 20.0
    player.foodLevel = 20
    player.saturation = 20f
    player.totalExperience = 0
    player.level = 0
    player.exp = 0f

    player.activePotionEffects
        .forEach(Consumer { effect: PotionEffect? -> player.removePotionEffect(effect!!.type) })

    Bukkit.getServer().advancementIterator().forEachRemaining(Consumer { adv: Advancement? ->
        player.getAdvancementProgress(adv!!).awardedCriteria
            .forEach(Consumer { criterion: String? ->
                player.getAdvancementProgress(adv).revokeCriteria(criterion!!)
            })
    })

    player.setRespawnLocation(null, true)
}

fun deleteDirectory(directory: File) {
    if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        directory.delete()
    }
}