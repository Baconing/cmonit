/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.field.DataPersisterManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.table.TableUtils
import ng.baconi.cmonit.CMonit
import ng.baconi.cmonit.models.ChunkEntityEntry
import ng.baconi.cmonit.models.ChunkEntry
import ng.baconi.cmonit.models.ChunkRedstoneEntry
import ng.baconi.cmonit.models.ChunkTileEntityEntry
import ng.baconi.cmonit.serializers.ChunkSerializer
import java.util.*

class Database {
    companion object {
        /**
         * The connection source for the database.
         */
        @JvmStatic
        lateinit var connectionSource: JdbcConnectionSource
            private set

        /**
         * The DAO for the ChunkEntry table.
         */
        @JvmStatic
        lateinit var chunkDao: Dao<ChunkEntry, UUID>
            private set

        /**
         * The DAO for the ChunkEntityEntry table.
         */
        @JvmStatic
        lateinit var chunkEntityDao: Dao<ChunkEntityEntry, UUID>
            private set

        /**
         * The DAO for the ChunkTileEntityEntry table.
         */
        @JvmStatic
        lateinit var chunkTileEntityDao: Dao<ChunkTileEntityEntry, UUID>
            private set

        /**
         * The DAO for the ChunkRedstoneEntry table.
         */
        @JvmStatic
        lateinit var chunkRedstoneDao: Dao<ChunkRedstoneEntry, UUID>
            private set

        /**
         * Initializes the database with values from the config.
         * This should be called once.
         * Call before any other database operations, and after the config has been loaded.
         */
        @JvmStatic
        fun init() {
            connectionSource = JdbcPooledConnectionSource(
                CMonit.instance.config.getString("database.url"),
                CMonit.instance.config.getString("database.user"),
                CMonit.instance.config.getString("database.password")
            )

            registerDao()
            createTables()

            DataPersisterManager.registerDataPersisters(ChunkSerializer.singleton)
        }

        @JvmStatic
        private fun registerDao() {
            chunkDao = DaoManager.createDao(connectionSource, ChunkEntry::class.java) as Dao<ChunkEntry, UUID>
            chunkEntityDao = DaoManager.createDao(connectionSource, ChunkEntityEntry::class.java) as Dao<ChunkEntityEntry, UUID>
            chunkTileEntityDao = DaoManager.createDao(connectionSource, ChunkTileEntityEntry::class.java) as Dao<ChunkTileEntityEntry, UUID>
            chunkRedstoneDao = DaoManager.createDao(connectionSource, ChunkRedstoneEntry::class.java) as Dao<ChunkRedstoneEntry, UUID>
        }

        @JvmStatic
        private fun createTables() {
            TableUtils.createTableIfNotExists(connectionSource, ChunkEntry::class.java)
            TableUtils.createTableIfNotExists(connectionSource, ChunkEntityEntry::class.java)
            TableUtils.createTableIfNotExists(connectionSource, ChunkTileEntityEntry::class.java)
            TableUtils.createTableIfNotExists(connectionSource, ChunkRedstoneEntry::class.java)
        }
    }
}