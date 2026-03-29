package com.cryptocore.multiverse.commands

import com.cryptocore.multiverse.Universe
import com.cryptocore.multiverse.getUniverse
import com.cryptocore.multiverse.universes
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun buildCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("multiverse")
        .then(
            Commands.literal("create")
                .then(
                    Commands.argument("name", StringArgumentType.string())
                        .then(
                            Commands.argument("seed", LongArgumentType.longArg())
                                .executes { ctx ->
                                    val name = ctx.getArgument("name", String::class.java)
                                    Universe(name, ctx.getArgument("seed", Long::class.java)).loadUniverse()
                                    1
                                })
                        .executes { ctx ->
                            val name = ctx.getArgument("name", String::class.java)
                            Universe(name).loadUniverse()
                            1
                        })
                .executes {
                    Universe().loadUniverse()
                    1
                })
        .then(
            Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.word()))
                .executes { ctx ->
                    val name = ctx.getArgument("name", String::class.java)
                    val universe = getUniverse(name) ?: return@executes 0
                    universe.deleteUniverse()
                    ctx.source.sender.sendMessage("Deleted world $name.")
                    1
                })
        .then(
            Commands.literal("spawn")
                .then(
                    Commands.argument("world", StringArgumentType.word())
                        .then(Commands.argument("player", StringArgumentType.word()).executes { ctx ->
                            val name = ctx.getArgument("name", String::class.java)
                            val universe = getUniverse(name)
                            if (universe == null) {
                                ctx.source.sender.sendMessage("World not found.")
                                return@executes 0
                            }
                            val player =
                                Bukkit.getPlayer(ctx.getArgument("player", String::class.java)) ?: return@executes 0
                            universe.spawnPlayer(player)
                            1
                        })
                        .executes { ctx ->
                            val name = ctx.getArgument("world", String::class.java)
                            val universe = getUniverse(name)
                            if (universe == null) {
                                ctx.source.sender.sendMessage("World not found.")
                                return@executes 0
                            }
                            if (ctx.source.sender !is Player) {
                                return@executes 0
                            }
                            universe.spawnPlayer(ctx.source.sender as Player)
                            1
                        })
        )
        .then(Commands.literal("list").executes { ctx ->
            val worlds = universes.joinToString("\n") { universe -> " - " + universe.name }
            ctx.source.sender.sendMessage("Worlds:\n$worlds")
            1
        })
        .build()
}