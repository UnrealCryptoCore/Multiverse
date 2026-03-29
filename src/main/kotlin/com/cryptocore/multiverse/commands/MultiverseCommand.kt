package com.cryptocore.multiverse.commands

import com.cryptocore.multiverse.MultiverseAPI
import com.cryptocore.multiverse.universes
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player
import java.util.UUID

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
                                    MultiverseAPI.createOrLoadUniverse(name, ctx.getArgument("seed", Long::class.java))
                                    1
                                })
                        .executes { ctx ->
                            val name = ctx.getArgument("name", String::class.java)
                            MultiverseAPI.createOrLoadUniverse(name)
                            1
                        })
                .executes {
                    MultiverseAPI.createOrLoadUniverse(UUID.randomUUID().toString())
                    1
                })
        .then(
            Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.word()))
                .executes { ctx ->
                    val name = ctx.getArgument("name", String::class.java)
                    val universe = MultiverseAPI.getUniverse(name) ?: return@executes 0
                    universe.deleteUniverse()
                    ctx.source.sender.sendMessage("Deleted world $name.")
                    1
                })
        .then(
            Commands.literal("spawn")
                .then(
                    Commands.argument("world", StringArgumentType.word())
                        .then(Commands.argument("player", ArgumentTypes.players()).executes { ctx ->
                            val name = ctx.getArgument("world", String::class.java)
                            val universe = MultiverseAPI.getUniverse(name)
                            if (universe == null) {
                                ctx.source.sender.sendMessage("World not found.")
                                return@executes 0
                            }
                            val resolver =
                                ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                            val players = resolver.resolve(ctx.source)
                            players.forEach {
                                universe.spawnPlayer(it)
                            }
                            1
                        })
                        .executes { ctx ->
                            val name = ctx.getArgument("world", String::class.java)
                            val universe = MultiverseAPI.getUniverse(name)
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