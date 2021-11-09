/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.inventory.util

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class MathKtTest : FunSpec({

    context("round to power") {
        withData(
            nameFn = { (number, power, expected) -> "$number round to power $power should be $expected" },
            Triple(0, 2, 0),
            Triple(7, 5, 10),
            Triple(7, 7, 7),
            Triple(-7, 6, -12),
        ) { (number, power, expected) -> number roundToPowerOf power shouldBe expected }
    }
})
