/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.models

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import ng.baconi.cmonit.NoArg
import ng.baconi.cmonit.serializers.ChunkSerializer
import org.bukkit.Chunk
import org.bukkit.World
import java.sql.Timestamp
import java.util.*

@DatabaseTable(tableName = "cmonit_chunk")
@NoArg
data class ChunkTileEntityEntry(
    @DatabaseField(id = true)
    val id: UUID,

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    val chunk: ChunkEntry,

    @DatabaseField
    val tileEntites: Int,

    @DatabaseField(dataType = DataType.TIME_STAMP_STRING)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis())
)