/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.commands

import ng.baconi.cmonit.database.Database
import org.bukkit.World
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.BukkitCommandActor

class WorldStatisticsCommand {
    @Command("cmonit worldstats")
    fun worldStats(actor: BukkitCommandActor, world: World) {
        val chunks = Database.chunkDao.queryForAll().filter { it.chunk.world == world }

        // use basic statistics to calculate the upper outliers for the most recent entry of a chunk
        val entityStats = chunks.map { it.entities.last().entities }
        val tileEntityStats = chunks.map { it.tileEntities.last().tileEntites }
        val redstoneStats = chunks.map { it.redstone.last().newCurrent }

        val entityIQR = entityStats.sorted().let { it[it.size / 4 * 3] - it[it.size / 4] }
        val entityUpperBound = entityStats.sorted().let { it[it.size / 4 * 3] + 1.5 * entityIQR }

        val tileEntityIQR = tileEntityStats.sorted().let { it[it.size / 4 * 3] - it[it.size / 4] }
        val tileEntityUpperBound = tileEntityStats.sorted().let { it[it.size / 4 * 3] + 1.5 * tileEntityIQR }

        val entityOutliers = chunks.filter { it.entities.last().entities > entityUpperBound }
        val tileEntityOutliers = chunks.filter { it.tileEntities.last().tileEntites > tileEntityUpperBound }

        if (entityOutliers.isNotEmpty()) {
            actor.reply("World ${world.name} has ${entityOutliers.size} chunks with abnormal entity count:")
            for (chunk in entityOutliers) {
                actor.reply("  Chunk at (${chunk.chunk.x}, ${chunk.chunk.z}) has ${chunk.entities.last().entities} entities")
            }
        }

        if (tileEntityOutliers.isNotEmpty()) {
            actor.reply("World ${world.name} has ${tileEntityOutliers.size} chunks with abnormal tile entity count:")
            for (chunk in tileEntityOutliers) {
                actor.reply("  Chunk at (${chunk.chunk.x}, ${chunk.chunk.z}) has ${chunk.tileEntities.last().tileEntites} tile entities")
            }
        }
    }
}