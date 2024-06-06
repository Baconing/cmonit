/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.listeners

import ng.baconi.cmonit.CMonit
import ng.baconi.cmonit.database.Database
import ng.baconi.cmonit.models.ChunkRedstoneEntry
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockRedstoneEvent
import org.bukkit.event.EventPriority
import java.util.*

class RedstoneListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onRedstoneEvent(event: BlockRedstoneEvent) {
        val chunk = event.block.chunk

        Bukkit.getServer().scheduler.runTaskAsynchronously(CMonit.instance, Runnable {
            val chunkEntry = Database.chunkDao.queryForEq("chunk", chunk)[0]

            chunkEntry.redstone += ChunkRedstoneEntry(
                UUID.randomUUID(),
                chunkEntry,
                event.oldCurrent,
                event.newCurrent
            )

            Database.chunkDao.update(chunkEntry)
        })
    }
}