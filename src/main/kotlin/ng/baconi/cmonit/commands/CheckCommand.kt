/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.commands

import org.bukkit.Bukkit
import org.bukkit.World
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.bukkit.BukkitCommandActor
import revxrsal.commands.bukkit.player

@Deprecated("use WorldStatisticsCommand", replaceWith = ReplaceWith("WorldStatisticsCommand"), level = DeprecationLevel.WARNING)
class CheckCommand {
    data class ChunkCoordinate(val x: Int, val z: Int)

    @Command("cmonit scan")
    fun scan(actor: BukkitCommandActor, @Optional world: String) {
        actor.reply("Scanning...")

        val aWorld = Bukkit.getWorld(world)

        if (aWorld != null) {
            processWorld(actor, aWorld)
        } else {
            for (w in actor.player.server.worlds) {
                processWorld(actor, w)
            }
        }


        actor.reply("Scan complete.")
    }

    fun processWorld(actor: BukkitCommandActor, world: World) {
        var entities = mutableMapOf<ChunkCoordinate, Int>()
        var tileEntities = mutableMapOf<ChunkCoordinate, Int>()
        for (chunk in world.loadedChunks) {
            entities[ChunkCoordinate(chunk.x, chunk.z)] = chunk.entities.size
            tileEntities[ChunkCoordinate(chunk.x, chunk.z)] = chunk.tileEntities.size
        }


        var eq1 = entities.entries.sortedBy { it.value }.take(entities.size / 4).last().value
        var eq3 = entities.entries.sortedBy { it.value }.takeLast(entities.size / 4).last().value
        var eiqr = eq3 - eq1
        var elowerBound = eq1 - 1.5 * eiqr
        var eupperBound = eq3 + 1.5 * eiqr

        var eoutliers = entities.filter { it.value < elowerBound || it.value > eupperBound }
        if (eoutliers.isNotEmpty()) {
            actor.reply("World ${world.name} has ${eoutliers.size} chunks with abnormal entity count:")
            for ((coord, count) in eoutliers) {
                actor.reply("  Chunk at (${coord.x}, ${coord.z}) has $count entities")
            }
        }


        var tq1 = tileEntities.entries.sortedBy { it.value }.take(tileEntities.size / 4).last().value
        var tq3 = tileEntities.entries.sortedBy { it.value }.takeLast(tileEntities.size / 4).last().value
        var tiqr = tq3 - tq1
        var tlowerBound = tq1 - 1.5 * tiqr
        var tupperBound = tq3 + 1.5 * tiqr

        var toutliers = tileEntities.filter { it.value < tlowerBound || it.value > tupperBound }
        if (toutliers.isNotEmpty()) {
            actor.reply("World ${world.name} has ${toutliers.size} chunks with abnormal tile entity count:")
            for ((coord, count) in toutliers) {
                actor.reply("  Chunk at (${coord.x}, ${coord.z}) has $count tile entities")
            }
        }

        // debug
        actor.reply("EQ1: ${eq1}; EQ3: ${eq3}; EIQR: ${eiqr}")
        actor.reply("TQ1: ${tq1}; TQ3: ${tq3}; TIQR: ${tiqr}")
    }
}