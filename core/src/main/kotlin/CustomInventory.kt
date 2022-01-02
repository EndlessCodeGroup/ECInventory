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

package ru.endlesscode.inventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.TaskScheduler
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.listener.*
import ru.endlesscode.inventory.internal.listener.SlotInteractionResult.Change
import ru.endlesscode.inventory.internal.util.*
import ru.endlesscode.inventory.slot.InventorySlot
import ru.endlesscode.inventory.slot.Slot
import ru.endlesscode.inventory.util.Placeholders
import java.util.*
import java.util.function.Predicate

/**
 * Inventory consisting of [InventorySlot]s.
 *
 * @property id Unique ID of the inventory.
 * @param layout Layout of the inventory.
 */
public class CustomInventory internal constructor(
    public val id: UUID,
    private val layout: InventoryLayout,
    private val scheduler: TaskScheduler = DI.scheduler,
    private val placeholders: Placeholders = DI.placeholders,
) : InventoryHolder {

    /** Returns inventory layout name. */
    public val name: String get() = layout.name

    /** Returns inventory layout ID. It can be considered as an inventory type. */
    public val type: String get() = layout.id

    /** Temporary [Inventory], used to show [CustomInventory] to player. */
    private var view: Inventory? = null

    private val slotsById: MutableMap<String, MutableList<InventorySlot>> = mutableMapOf()
    private var slotsLayout: Map<Int, Pair<String, Int>> = emptyMap()

    private val slotsSequence: Sequence<InventorySlot>
        get() = slotsById.values.asSequence().flatten()

    /** Returns number of slots in the inventory. */
    public val size: Int
        get() = slotsSequence.count()

    /** View size is maximal slot position rounded to nine. */
    internal val viewSize: Int
        get() = slotsLayout.keys.last().roundToPowerOf(9)

    /**
     * Returns the maximum stack size for an ItemStack in this inventory.
     * @see InventorySlot.maxStackSize
     */
    public var maxStackSize: Int = DEFAULT_MAX_STACK
        internal set

    /** Returns a list of players viewing the inventory. */
    public val viewers: List<HumanEntity>
        get() = view?.viewers.orEmpty()

    public constructor(layout: InventoryLayout) : this(
        id = UUID.randomUUID(),
        layout = layout,
        scheduler = DI.scheduler,
    )

    init {
        for ((position, slot) in layout.slotsMap) {
            slotsById.getOrPut(slot.id, ::mutableListOf)
                .add(InventorySlot(slot, this, position))
        }
        updateLayout()
    }

    /**
     * Returns the ItemStack found in the [n]th slot with the given [slotId],
     * or `null` if there are no such slot.
     *
     * By default [n] is `0`, so it will return item from the first slot for
     * the given [slotId].
     */
    @JvmOverloads
    public fun getItem(slotId: String, n: Int = 0): ItemStack? = getSlot(slotId, n)?.content

    /** Stores the given [item] at the first slot with the given [slotId]. */
    public fun setItem(slotId: String, item: ItemStack?) {
        setItem(slotId, n = 0, item)
    }

    /** Stores the given [item] at the [n]th slot with the given [slotId]. */
    public fun setItem(slotId: String, n: Int, item: ItemStack?) {
        getSlot(slotId, n)?.content = item.orEmpty()
    }

    /**
     * Returns the list of ItemStacks found in the slots with the given [slotId]
     * or an empty list if there are no such slots.
     */
    public fun getItems(slotId: String): List<ItemStack> = slotsById[slotId].orEmpty().map { it.content }

    /**
     * Returns the slot with the given [id][slotId], or `null`
     * if there are no such slot.
     *
     * By default [n] is `0`, so it will return the first slot for
     * the given [slotId].
     */
    @JvmOverloads
    public fun getSlot(slotId: String, n: Int = 0): InventorySlot? = slotsById[slotId]?.getOrNull(n)

    /** Returns slot by theirs [position] or `null` if there are no slot on the given position. */
    public fun getSlotAt(position: Int): InventorySlot? {
        val (slotId, n) = slotsLayout[position] ?: return null
        return getSlot(slotId, n)
    }

    /** Returns slots with the given [slotId] or empty list if there are no such slots. */
    public fun getSlots(slotId: String): List<InventorySlot> = slotsById[slotId]?.toList().orEmpty()

    /** Returns slots that may contain items (all slots except visual). */
    public fun getContainerSlots(): List<InventorySlot> = slotsSequence.filter { it.type != Slot.Type.VISUAL }.toList()

    /** Returns the inventory's slots with the given [type] or all slots if type is `null`. */
    public fun getSlotsByType(type: Slot.Type): List<InventorySlot> = slotsSequence.filter { it.type == type }.toList()

    /** Returns the inventory's passive slots. */
    public fun getPassiveSlots(): List<InventorySlot> = getSlotsByType(Slot.Type.PASSIVE)

    /** Returns the inventory's storage slots. */
    public fun getStorageSlots(): List<InventorySlot> = getSlotsByType(Slot.Type.STORAGE)

    /** Returns the inventory's active slots. */
    public fun getActiveSlots(): List<InventorySlot> = getSlotsByType(Slot.Type.ACTIVE)

    /** Constructs and returns [Inventory] that can be shown to a player. */
    override fun getInventory(): Inventory = getInventory(player = null)

    /** Constructs and returns [Inventory] that can be shown to the given [player]. */
    private fun getInventory(player: OfflinePlayer?): Inventory {
        val currentView = view
        if (currentView != null) return currentView

        val inventoryName = placeholders.apply(name.translateColorCodes(), player)
        return Bukkit.createInventory(this, viewSize, inventoryName).also { view ->
            view.maxStackSize = maxStackSize
            view.contents = buildViewContents(player)
            this.view = view
        }
    }

    private fun buildViewContents(player: OfflinePlayer?): Array<ItemStack> {
        val contents = Array(viewSize) { layout.emptySlotTexture }
        for (slot in slotsSequence) {
            contents[slot.position] = placeholders.apply(slot.getContentOrTexture(), player)
        }
        return contents
    }

    /** Opens this inventory for the given [player]. */
    public fun open(player: Player) {
        player.openInventory(getInventory(player))
    }

    /** This method should be called when inventory close. */
    internal fun onClose() {
        this.view = null
    }

    /** Assigns given [slot] to the given [position], replacing existing slot. */
    public fun assignSlot(position: Int, slot: Slot) {
        val inventorySlot = InventorySlot(slot, this, position)
        val existingSlotLocation = slotsLayout[position]

        if (existingSlotLocation != null) {
            val (slotId, n) = existingSlotLocation
            removeSlot(slotId, n)
        }
        slotsById.getOrPut(inventorySlot.id, ::mutableListOf)
            .add(inventorySlot)
        updateLayout()
    }

    /**
     * Removes [n]th slot with the specified [slotId] from the inventory.
     *
     * By default [n] is `0`, so it will remove the first slot for
     * the given [slotId].
     *
     * @return removed slot, or `null` if there are no slot with the given id.
     */
    @JvmOverloads
    public fun removeSlot(slotId: String, n: Int = 0): InventorySlot? {
        val slots = slotsById[slotId] ?: return null
        if (n !in slots.indices) return null

        val removedSlot = slots.removeAt(n)
        if (slots.isEmpty()) slotsById.remove(slotId)
        updateLayout()

        return removedSlot
    }

    // Should be called after any operation that adds or removes slots.
    private fun updateLayout() {
        slotsLayout = buildMap {
            for ((slotId, slots) in slotsById) {
                slots.forEachIndexed { index, slot ->
                    put(slot.position, slotId to index)
                }
            }
        }
    }

    /**
     * Returns the first empty that can hold the given [item]
     * or `null` if there are no such slots.
     */
    public fun findEmptySlot(item: ItemStack): InventorySlot? {
        return getStorageSlots().asSequence()
            .filter(InventorySlot::isEmpty)
            .find { it.canHold(item) }
    }

    /**
     * Stores the given [items] in the inventory. This will try to fill
     * existing stacks and empty slots as well as it can.
     *
     * The returned `Map` contains what it couldn't store, where the key is
     * the index of the parameter, and the value is the ItemStack at that
     * index of the varargs parameter. If all items are stored, it will return
     * an empty `Map`.
     */
    public fun addItem(vararg items: ItemStack): Map<Int, ItemStack> {
        val leftover = mutableMapOf<Int, ItemStack>()
        val nonFullSlots = mutableMapOf<Material, InventorySlot>()

        var freeSlot = findEmptySlot(items.first())
        for (i in items.indices) {
            var item = items[i]

            while (item.isNotEmpty()) {
                // Do we already have a stack of it? If not, use free slot
                val nonFullSlot = nonFullSlots.remove(item.type) ?: findPartial(item)
                val slot = nonFullSlot ?: freeSlot

                // Drat! no partial stack and no space at all!
                if (slot == null) {
                    leftover[i] = item
                    break
                }

                item = slot.placeItem(item)
                freeSlot = findEmptySlot(item)

                // Remember that there are the non-full slot
                if (!slot.isFull()) nonFullSlots[item.type] = slot
            }
        }

        return leftover
    }

    /**
     * Removes the given [items] from the inventory.
     *
     * It will try to remove 'as much as possible' from the types and amounts
     * you give as arguments.
     *
     * The returned `Map` contains what it couldn't remove, where the key is
     * the index of the parameter, and the value is the ItemStack at that
     * index of the varargs parameter. If all the given ItemStacks are
     * removed, it will return an empty `Map`.
     *
     * It is known that in some implementations this method will also set the
     * inputted argument amount to the number of that item not removed from
     * slots.
     */
    public fun removeItem(vararg items: ItemStack): Map<Int, ItemStack> {
        val leftover = mutableMapOf<Int, ItemStack>()
        val itemsSlots = mutableMapOf<Material, InventorySlot>()

        for (i in items.indices) {
            val item = items[i]
            var toDelete = item.amount

            while (toDelete > 0) {
                val itemSlot = itemsSlots.remove(item.type) ?: findSimilar(item)

                // Drat! we don't have this type in the inventory
                if (itemSlot == null) {
                    item.amount = toDelete
                    leftover[i] = item
                    break
                }

                val removedItem = itemSlot.takeItem(toDelete)
                toDelete -= removedItem.amount

                // Remember that there are non-fully empty slot
                if (!itemSlot.isEmpty()) itemsSlots[item.type] = itemSlot
            }
        }

        return leftover
    }

    /**
     * Checks if this inventory contains any item stacks with the given
     * [material].
     *
     * @return `false` if the [material] is `AIR`.
     */
    public operator fun contains(material: Material): Boolean {
        if (material.isAir) return false
        return getStorageSlots().any { it.content.type == material }
    }

    /**
     * Checks if this inventory contains the given [item].
     *
     * @return `false` if the [item] is `null`.
     */
    public operator fun contains(item: ItemStack?): Boolean {
        if (item == null) return false
        return getStorageSlots().any { it.content == item }
    }

    /**
     * Checks if this inventory contains any item stacks with the given
     * [material], adding to at least the minimum [amount] specified.
     *
     * @return `true` if the [amount] less than `1`. Returns `true` if
     * the [material] is `AIR` and the inventory contains any empty slots.
     */
    public fun contains(material: Material, amount: Int): Boolean {
        if (amount <= 0) return true

        return getStorageSlots().asSequence()
            .map { it.content }
            .filter { it.type == material }
            .scan(amount) { remainingAmount, item -> remainingAmount - item.amount }
            .any { it <= 0 }
    }

    /**
     * Checks if the inventory contains at least the minimum [amount]
     * specified of exactly matching [item].
     *
     * An `ItemStack` only counts if both the type and the amount
     * of the stack match.
     *
     * @return `false` if the [item] is null, `true` if [amount] less
     * than `1` or if amount of exactly matching ItemStacks were found.
     */
    public fun contains(item: ItemStack?, amount: Int): Boolean {
        if (item == null) return false
        if (amount <= 0) return true

        return getStorageSlots().asSequence()
            .filter { it.content == item }
            .scan(amount) { remainingAmount, _ -> remainingAmount - 1 }
            .any { it <= 0 }
    }

    /**
     * Checks if the inventory contains [item] matching the given ItemStack
     * whose amounts sum to at least the minimum [amount] specified.
     *
     * @return `false` if [item] is `null`, `true` if [amount] less than `1`,
     * `true` if enough ItemStacks were found to add to the given [amount].
     */
    public fun containsAtLeast(item: ItemStack?, amount: Int): Boolean {
        if (item == null) return false
        if (amount <= 0) return true

        return getStorageSlots().asSequence()
            .map { it.content }
            .filter { it.isSimilar(item) }
            .scan(amount) { remainingAmount, currentItem -> remainingAmount - currentItem.amount }
            .any { it <= 0 }
    }

    /**
     * Returns the first slot in the inventory containing an `ItemStack` with
     * the given [material] or `null` if there are no such slot.
     */
    public fun find(material: Material): InventorySlot? {
        return getStorageSlots().find { it.content.type == material }
    }

    /**
     * Returns the first slot in the inventory containing the given [item].
     * This will only match a slot if both the type and the amount
     * of the stack match.
     * @see findSimilar
     */
    public fun find(item: ItemStack): InventorySlot? {
        return getStorageSlots().find { slot -> item == slot.content }
    }

    /**
     * Returns the first slot in the inventory containing the item similar
     * to the given [item].
     * @see find
     */
    private fun findSimilar(item: ItemStack): InventorySlot? {
        return getStorageSlots().find { slot -> item.isSimilar(slot.content) }
    }

    /**
     * Check whether this inventory is empty. An inventory is considered
     * to be empty if all slots of this inventory are empty.
     */
    public fun isEmpty(): Boolean = slotsSequence.all(InventorySlot::isEmpty)

    /** Removes all stacks in the inventory matching the given [material]. */
    public fun removeAll(material: Material) {
        removeAll { it.type == material }
    }

    /** Removes all stacks in the inventory matching the given [item]. */
    public fun removeAll(item: ItemStack) {
        removeAll { it == item }
    }

    /** Removes all stacks in the inventory matching the given [predicate]. */
    public fun removeAll(predicate: Predicate<ItemStack>) {
        getStorageSlots().asSequence()
            .filter { predicate.test(it.content) }
            .forEach { clear(it.id) }
    }

    /** Clears out the whole inventory. */
    public fun clear() {
        slotsLayout.values.forEach { (slotId, n) -> clear(slotId, n) }
    }

    /**
     * Clears out the [n]th slot with the given [slotId].
     *
     * By default [n] is `0`, so it will clear the first slot for
     * the given [slotId].
     */
    @JvmOverloads
    public fun clear(slotId: String, n: Int = 0) {
        setItem(slotId, n, null)
    }

    internal fun syncSlotWithView(slot: InventorySlot) {
        // Do sync on the next tick for the case if it was called from click event
        scheduler.runOnMain {
            view?.setItem(slot.position, slot.getContentOrTexture())
        }
    }

    private fun findPartial(item: ItemStack?): InventorySlot? {
        if (item == null) return null

        return getStorageSlots().find { slot ->
            val slotItem = slot.content
            !slot.isFull() && slotItem.isSimilar(item)
        }
    }

    internal fun handleInteraction(interaction: InventoryInteraction) {
        when (interaction) {
            is SlotInteraction -> handleSlotInteraction(interaction)

            is AddItemToInventory -> {
                val itemLeft = addItem(interaction.item).values.firstOrNull()
                interaction.setSlotItem(itemLeft.orEmpty())
            }
        }
    }

    private fun handleSlotInteraction(interaction: SlotInteraction) {
        val slot = interaction.slot
        val result = when (interaction) {
            is TakeSlotContent -> slot.takeItemInteraction(interaction.amount)
            is PlaceSlotContent -> slot.placeItemInteraction(interaction.item, interaction.amount)
            is SwapSlotContent -> slot.swapItemInteraction(interaction.item)
        }

        interaction.apply(result)

        if (result is Change) {
            if (result.cursorReplacement != null) {
                scheduler.runOnMain { interaction.syncCursor(result.cursorReplacement) }
            }
        }
    }

    /** Swap content with the given [item]. */
    private fun InventorySlot.swapItemInteraction(item: ItemStack): SlotInteractionResult = when {
        item.amount > maxStackSize || !canHold(item) -> SlotInteractionResult.Deny
        item.isEmpty() && this.isEmpty() -> SlotInteractionResult.Deny
        item.isEmpty() -> takeItemInteraction()
        this.isEmpty() -> placeItemInteraction(item)

        else -> {
            swapItem(item)
            SlotInteractionResult.Accept
        }
    }

    /** Takes item from this slot and returns result of this interaction. */
    private fun InventorySlot.takeItemInteraction(amount: Int = content.amount): SlotInteractionResult {
        val expectedCursor = getContentOrTexture().cloneWithAmount(amount)
        val actualCursor = takeItem(amount)
        return when {
            actualCursor.isEmpty() -> SlotInteractionResult.Deny
            expectedCursor == actualCursor -> SlotInteractionResult.Accept
            else -> Change(currentItemReplacement = actualCursor)
        }
    }

    /** Places the given [item] to this slot and returns result of this interaction. */
    private fun InventorySlot.placeItemInteraction(item: ItemStack, amount: Int = item.amount): SlotInteractionResult {
        require(amount in 1..item.amount)
        require(this.isEmpty() || content.isSimilar(item))

        val wasEmptyWithTexture = this.isEmpty() && texture.isNotEmpty()
        val newCursor = placeItem(item, amount)

        return if (newCursor != item)
            Change(
                currentItemReplacement = AIR.takeIf { wasEmptyWithTexture },
                cursorReplacement = newCursor,
            )
        else {
            SlotInteractionResult.Deny
        }
    }

    public companion object {
        /**
         * By default, will be used stack size 64, and it will be increased when
         * will be added new slots with greater max stack size.
         * @see InventorySlot
         */
        public const val DEFAULT_MAX_STACK: Int = 64
    }
}
