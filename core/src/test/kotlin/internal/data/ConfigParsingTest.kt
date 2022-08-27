/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2022 EndlessCode Group and contributors
 *
 * ECInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ECInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ECInventory. If not, see <http://www.gnu.org/licenses/>.
 */

package internal.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import ru.endlesscode.inventory.internal.data.parseSlotPositions

@OptIn(ExperimentalKotest::class)
internal class ConfigParsingTest : FeatureSpec({

    feature("parseSlotPositions") {

        // Valid data
        withData(
            nameFn = { (value, range) -> "$value -> $range" },
            "42" to 42..42,
            "0-0" to 0..0,
            "9-17" to 9..17,
            "0-53" to 0..53,
        ) { (value, range) -> parseSlotPositions(value) shouldBe range }

        // Not valid data
        withData(
            nameFn = { "'$it' is not valid range" },
            "",
            "-1",
            "54",
            "4-2",
            "0-54",
            "0..2",
            "word",
        ) { value -> shouldThrow<IllegalArgumentException> { parseSlotPositions(value) } }
    }
})
