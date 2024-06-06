/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit

import com.djrapitops.plan.capability.CapabilityService
import com.djrapitops.plan.extension.ExtensionService
import ng.baconi.cmonit.commands.CheckCommand
import ng.baconi.cmonit.commands.WorldStatisticsCommand
import ng.baconi.cmonit.database.Database
import ng.baconi.cmonit.models.ChunkEntityEntry
import ng.baconi.cmonit.models.ChunkEntry
import ng.baconi.cmonit.models.ChunkTileEntityEntry
import ng.baconi.cmonit.plan.CMonitPlanExtension
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.bukkit.BukkitCommandHandler
import java.lang.IllegalStateException
import java.util.*

class CMonit : JavaPlugin() {
    private lateinit var handler: BukkitCommandHandler

    override fun onEnable() {
        this.logger.info("Enabling CMonit...")

        saveDefaultConfig()

        instance = this

        this.logger.info("Registering commands...")
        handler = BukkitCommandHandler.create(this)

        handler.register(CheckCommand())
        handler.register(WorldStatisticsCommand())
        handler.registerBrigadier()

        this.logger.info("Initializing database...")
        Database.init()

        this.logger.info("Registering extensions: ")
        registerExtensions()

        this.logger.info("Registering entity counters...")

        val ONE_SECOND = 20
        this.server.scheduler.runTaskTimerAsynchronously(this, Runnable {
            this.server.worlds.forEach { world ->
                if (config.getStringList("exluded_worlds").contains(world.name)) return@forEach
                world.loadedChunks.forEach { chunk ->
                    val chunkEntity = Database.chunkDao.queryForEq("chunk", chunk).firstOrNull()
                        ?: ChunkEntry(UUID.randomUUID(), chunk, mutableListOf(), mutableListOf(), mutableListOf())

                    chunkEntity.entities += ChunkEntityEntry(
                        UUID.randomUUID(),
                        chunkEntity,
                        chunk.entities.size
                    )

                    chunkEntity.tileEntities += ChunkTileEntityEntry(
                        UUID.randomUUID(),
                        chunkEntity,
                        chunk.tileEntities.size
                    )

                    Database.chunkDao.createOrUpdate(chunkEntity)
                }
            }
        }, 0, ONE_SECOND * 5L)

        this.logger.info("CMonit enabled.")
    }

    private fun registerExtensions() {
        try {
            if (!CapabilityService.getInstance().hasCapability("DATA_EXTENSION_VALUES")) {
                this.logger.warning("Plan [⭕] (Missing DATA_EXTENSION_VALUES capability)")
            } else {
                ExtensionService.getInstance().register(CMonitPlanExtension())

                CapabilityService.getInstance().registerEnableListener {
                    if (it) {
                        ExtensionService.getInstance().register(CMonitPlanExtension())
                        this.logger.info("Reloaded Plan extension.")
                    }
                }

                this.logger.info("Plan [✅]")
            }
        } catch (e: NoClassDefFoundError) {
            this.logger.warning("Plan [❌])")
        } catch (e: IllegalStateException) {
            this.logger.warning("Plan [⭕] (Plan is not enabled)")
        }
    }

    companion object {
        lateinit var instance: CMonit
            private set
    }
}