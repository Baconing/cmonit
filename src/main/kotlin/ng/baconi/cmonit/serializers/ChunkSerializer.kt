/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.serializers

import com.google.gson.Gson
import com.j256.ormlite.field.FieldType
import com.j256.ormlite.field.SqlType
import com.j256.ormlite.field.types.BaseDataType
import com.j256.ormlite.support.DatabaseResults
import org.bukkit.Bukkit
import org.bukkit.Chunk
import java.util.*

class ChunkSerializer : BaseDataType(SqlType.STRING, arrayOf<Class<*>>(Chunk::class.java)) {
    data class ChunkData(
        var x: Int,
        var z: Int,
        var world: UUID
    )

    override fun parseDefaultString(fieldType: FieldType?, defaultStr: String?): Chunk? {
        return try {
            val data = Gson().fromJson(defaultStr, ChunkData::class.java)
            Bukkit.getWorld(data.world)?.getChunkAt(data.x, data.z)
        } catch (e: Exception) {
            null
        }
    }

    override fun resultToSqlArg(fieldType: FieldType?, results: DatabaseResults?, columnPos: Int): Any {
        return results?.getString(columnPos) ?: ""
    }

    override fun sqlArgToJava(fieldType: FieldType?, sqlArg: Any?, columnPos: Int): Chunk? {
        return try {
            val data = Gson().fromJson(sqlArg as String, ChunkData::class.java)
            Bukkit.getWorld(data.world)?.getChunkAt(data.x, data.z)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        @JvmStatic
        val singleton = ChunkSerializer()
    }
}