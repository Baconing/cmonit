/*
 * Copyright 2024. Brenden "Baconing" Freier
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED “AS IS” AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package ng.baconi.cmonit.plan

import com.djrapitops.plan.extension.CallEvents
import com.djrapitops.plan.extension.DataExtension
import com.djrapitops.plan.extension.annotation.PluginInfo
import com.djrapitops.plan.extension.annotation.TableProvider
import com.djrapitops.plan.extension.icon.Color
import com.djrapitops.plan.extension.icon.Family
import com.djrapitops.plan.extension.icon.Icon
import com.djrapitops.plan.extension.table.Table
import com.djrapitops.plan.extension.table.Table.Factory
import ng.baconi.cmonit.database.Database

//todo
@PluginInfo(name = "CMonit", iconName = "chart-simple", iconFamily = Family.SOLID, color = Color.NONE)
class CMonitPlanExtension : DataExtension {
    override fun callExtensionMethodsOn(): Array<CallEvents> {
        return arrayOf(CallEvents.SERVER_PERIODICAL)
    }

    @TableProvider
    fun provideChunkTable(): Table {
        var table: Table.Factory = Table.builder()
            .columnOne("Chunk", null)
            .columnTwo("World", null)
            .columnThree("Average Entities", null)
            .columnFour("Average Tile Entities", null)
            .columnFive("Average Redstone", null);

        return table.build()
    }
}