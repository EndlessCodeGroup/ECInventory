/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021-2022 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.slot

import io.kotest.assertions.assertSoftly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.util.AIR
import ru.endlesscode.inventory.test.mockItemFactory

@OptIn(ExperimentalKotest::class)
internal class InventorySlotTest : FeatureSpec({

    val slotContentValidator = TestItemValidator()
    val inventory = mockk<CustomInventory>(relaxed = true)

    // SUT
    val slot = ContainerInventorySlot(
        texture = Material.BLACK_STAINED_GLASS_PANE,
        maxStackSize = 4,
        contentValidator = slotContentValidator,
        holder = inventory,
    )

    fun verifySyncCalled(syncCalled: Boolean) {
        verify(exactly = if (syncCalled) 1 else 0) { inventory.syncSlotWithView(slot) }
    }

    beforeSpec {
        mockItemFactory()
    }

    feature("take item") {

        suspend fun scenario(
            name: String,
            slotContent: ItemStack,
            takeAmount: Int = slotContent.amount,
            afterTake: State = State(content = AIR, cursor = slotContent),
            syncCalled: Boolean = true,
        ) = scenario(name) {
            slot.content = slotContent
            clearMocks(inventory)

            assertSoftly {
                slot.takeItem(takeAmount) shouldBe afterTake.cursor
                slot.content shouldBe afterTake.content
                verifySyncCalled(syncCalled)
            }
        }

        scenario(
            "take item from empty slot",
            slotContent = AIR,
            syncCalled = false,
        )

        scenario(
            "take all items from slot",
            slotContent = item(amount = 2),
        )

        scenario(
            "take part of items from slot",
            slotContent = item(amount = 3),
            takeAmount = 2,
            afterTake = State(
                cursor = item(amount = 2),
                content = item(),
            ),
        )
    }

    feature("place item") {

        suspend fun scenario(
            name: String,
            beforePlace: State,
            afterPlace: State = State(content = beforePlace.cursor, cursor = beforePlace.content),
            placeAmount: Int = beforePlace.cursor.amount,
            syncCalled: Boolean = true,
        ) = scenario(name) {
            val (currentContent, currentCursor) = beforePlace
            val (resultContent, resultCursor) = afterPlace
            slot.content = currentContent
            clearMocks(inventory)

            assertSoftly {
                slot.placeItem(currentCursor, placeAmount) shouldBe resultCursor
                slot.content shouldBe resultContent
                verifySyncCalled(syncCalled)
            }
        }

        scenario(
            "place item to empty slot",
            beforePlace = State(
                cursor = item(),
                content = AIR,
            ),
        )

        scenario(
            "place single item",
            beforePlace = State(
                content = AIR,
                cursor = item(amount = slot.maxStackSize + 1),
            ),
            placeAmount = 1,
            afterPlace = State(
                content = item(amount = 1),
                cursor = item(amount = slot.maxStackSize),
            ),
        )

        scenario(
            "place more than max stack size to empty slot",
            beforePlace = State(
                content = AIR,
                cursor = item(amount = slot.maxStackSize + 1)
            ),
            afterPlace = State(
                content = item(amount = slot.maxStackSize),
                cursor = item(amount = 1)
            ),
        )

        scenario(
            "place similar item to full slot",
            beforePlace = State(
                content = item(amount = slot.maxStackSize),
                cursor = item(),
            ),
            afterPlace = State(
                content = item(amount = slot.maxStackSize),
                cursor = item(),
            ),
            syncCalled = false,
        )

        scenario(
            "place single similar item",
            placeAmount = 1,
            beforePlace = State(
                content = item(amount = 1),
                cursor = item(amount = 3),
            ),
            afterPlace = State(
                content = item(amount = 2),
                cursor = item(amount = 2),
            ),
        )

        scenario(
            "place similar item to slot",
            beforePlace = State(
                content = item(amount = slot.maxStackSize - 1),
                cursor = item(),
            ),
            afterPlace = State(
                content = item(amount = slot.maxStackSize),
                cursor = AIR,
            ),
        )

        scenario(
            "place similar item to slot with overflow",
            beforePlace = State(
                content = item(amount = slot.maxStackSize - 1),
                cursor = item(amount = 2)
            ),
            afterPlace = State(
                content = item(amount = slot.maxStackSize),
                cursor = item(amount = 1)
            ),
        )
    }

    feature("swap item") {

        suspend fun scenario(
            name: String,
            beforeSwap: State,
            afterSwap: State = State(content = beforeSwap.cursor, cursor = beforeSwap.content),
            syncCalled: Boolean = true,
        ) = scenario(name) {
            val (currentContent, currentCursor) = beforeSwap
            val (resultContent, resultCursor) = afterSwap
            slot.content = currentContent
            clearMocks(inventory)

            assertSoftly {
                slot.swapItem(currentCursor) shouldBe resultCursor
                slot.content shouldBe resultContent
                verifySyncCalled(syncCalled)
            }
        }

        scenario(
            "swap empty slot with empty item",
            beforeSwap = State(content = AIR, cursor = AIR),
            syncCalled = false,
        )

        scenario(
            "swap slot with empty item",
            beforeSwap = State(content = item(), cursor = AIR),
        )

        scenario(
            "swap empty slot with item",
            beforeSwap = State(content = AIR, cursor = item(amount = 2)),
        )

        scenario(
            "swap slot with item not fitting to slot",
            beforeSwap = State(
                content = AIR,
                cursor = item(amount = slot.maxStackSize + 1),
            ),
            afterSwap = State(
                content = AIR,
                cursor = item(amount = slot.maxStackSize + 1),
            ),
            syncCalled = false,
        )

        scenario(
            "swap slot content with cursor",
            beforeSwap = State(
                content = item(amount = 3),
                cursor = item(Material.BLAZE_ROD, amount = 2),
            ),
        )

        scenario(
            "swap slot content with item not fitting to slot",
            beforeSwap = State(
                content = item(Material.BLAZE_ROD),
                cursor = item(amount = slot.maxStackSize + 1),
            ),
            afterSwap = State(
                content = item(Material.BLAZE_ROD),
                cursor = item(amount = slot.maxStackSize + 1),
            ),
            syncCalled = false,
        )
    }

    feature("content validator") {

        // Consider blaze rod as a not valid item
        slotContentValidator.predicate = { it.type != Material.BLAZE_ROD }
        val notValidItem = item(Material.BLAZE_ROD)

        scenario("swap content with not valid item") {
            slot.content = item()
            slot.swapItem(notValidItem) shouldBe notValidItem
        }

        scenario("place not valid item") {
            slot.placeItem(notValidItem) shouldBe notValidItem
        }
    }
})

private fun item(material: Material = Material.STICK, amount: Int = 1): ItemStack = ItemStack(material, amount)

private data class State(
    val content: ItemStack,
    val cursor: ItemStack,
)
