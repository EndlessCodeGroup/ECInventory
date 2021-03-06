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
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.TaskScheduler
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.listener.*
import ru.endlesscode.inventory.internal.listener.SlotInteractionResult.*
import ru.endlesscode.inventory.internal.util.*
import ru.endlesscode.inventory.slot.*
import ru.endlesscode.inventory.util.Placeholders
import java.util.*
import java.util.function.Predicate

/**
 * Inventory consisting of [InventorySlot]s.
 *
 * @property id Unique ID of the inventory.
 * @property holder The player holding this inventory
 * @param layout Layout of the inventory.
 */
public class CustomInventory internal constructor(
    public val id: UUID,
    private val layout: InventoryLayout,
    public val holder: Player,
    private val scheduler: TaskScheduler = DI.scheduler,
    private val placeholders: Placeholders = DI.placeholders,
) : InventoryHolder {

    /** Returns inventory name displayed to players. */
    public val displayName: String get() = placeholders.apply(layout.displayName.translateColorCodes(), holder)

    /** Returns inventory layout ID. It can be considered as an inventory type. */
    public val type: String get() = layout.id

    /** Temporary [Inventory], used to show [CustomInventory] to player. */
    private var view: Inventory? = null

    private val slotsByName: MutableMap<String, MutableList<InventorySlot>> = mutableMapOf()
    private var slotsIndex: Array<Pair<String, Int>> = emptyArray()

    private val slotsSequence: Sequence<InventorySlot>
        get() = slotsByName.values.asSequence().flatten()
    private val containerSlotsSequence: Sequence<ContainerInventorySlot>
        get() = slotsSequence.filterIsInstance<ContainerInventorySlot>()

    /** Returns number of slots in the inventory. */
    public val size: Int
        get() = layout.rows * InventoryLayout.SLOTS_IN_ROW

    /**
     * Returns the maximum stack size for an ItemStack in this inventory.
     * @see ContainerInventorySlot.maxStackSize
     */
    public var maxStackSize: Int = DEFAULT_MAX_STACK
        internal set

    /** Returns a list of players viewing the inventory. */
    public val viewers: List<HumanEntity>
        get() = view?.viewers.orEmpty()

    public constructor(layout: InventoryLayout, holder: Player) : this(
        id = UUID.randomUUID(),
        layout = layout,
        holder = holder,
    )

    init {
        for (position in 0 until size) {
            val slot = layout.slotsMap[position] ?: layout.defaultSlot
            slotsByName.getOrPut(slot.name, ::mutableListOf)
                .add(createInventorySlot(slot, position))
        }
        updateSlotsIndex()
    }

    /**
     * Returns the ItemStack found in the [n]th slot with the given [slotName],
     * or `null` if there are no such slot.
     *
     * By default [n] is `0`, so it will return item from the first slot for
     * the given [slotName].
     */
    @JvmOverloads
    public fun getItem(slotName: String, n: Int = 0): ItemStack? {
        val slot = getSlot(slotName, n) as? ContainerInventorySlot
        return slot?.content
    }

    /** Stores the given [item] at the first slot with the given [slotName]. */
    public fun setItem(slotName: String, item: ItemStack?) {
        setItem(slotName, n = 0, item)
    }

    /** Stores the given [item] at the [n]th slot with the given [slotName]. */
    public fun setItem(slotName: String, n: Int, item: ItemStack?) {
        val slot = getSlot(slotName, n) as? ContainerInventorySlot
        slot?.content = item.orEmpty()
    }

    /** Stores the given [item] at the slot assigned to the given [position]. */
    public fun setItemAt(position: Int, item: ItemStack?) {
        val slot = getSlotAt(position) as? ContainerInventorySlot
        slot?.content = item.orEmpty()
    }

    /**
     * Returns the list of ItemStacks found in the slots with the given [slotName]
     * or an empty list if there are no such slots or these slots doesn't contain
     * items.
     *
     * Returned list may not contain `null` or `AIR`.
     */
    public fun getItems(slotName: String): List<ItemStack> {
        return slotsByName[slotName].orEmpty()
            .asSequence()
            .filterIsInstance<ContainerInventorySlot>()
            .filterNot { it.isEmpty() }
            .map { it.content }
            .toList()
    }

    /**
     * Returns the slot with the given [slotName], or `null`
     * if there are no such slot.
     *
     * By default [n] is `0`, so it will return the first slot for
     * the given [slotName].
     */
    @JvmOverloads
    public fun getSlot(slotName: String, n: Int = 0): InventorySlot? = slotsByName[slotName]?.getOrNull(n)

    /**
     * Returns slot by theirs [position].
     * @throws IndexOutOfBoundsException if the given position is out of the inventory.
     */
    public fun getSlotAt(position: Int): InventorySlot {
        val (slotName, n) = slotsIndex[position]
        return slotsByName.getValue(slotName)[n]
    }

    /** Returns slots with the given [slotName] or empty list if there are no such slots. */
    public fun getSlots(slotName: String): List<InventorySlot> = slotsByName[slotName]?.toList().orEmpty()

    /** Returns map where key is slot name and value is a list of slots with this slot name. */
    public fun getSlotsMap(): Map<String, List<InventorySlot>> = slotsByName.mapValues { (_, value) -> value.toList() }

    /** Returns the inventory's generic container slots. */
    public fun getGenericSlots(): List<ContainerInventorySlot> = getContainerSlots(SlotContentType.GENERIC)

    /** Returns the inventory's equipment container slots. */
    public fun getEquipmentSlots(): List<ContainerInventorySlot> = getContainerSlots(SlotContentType.EQUIPMENT)

    /**
     * Returns container slots matching to the given [contentType].
     * If the [contentType] is not passed, returns all container slots.
     * @see getEquipmentSlots
     * @see getGenericSlots
     */
    @JvmOverloads
    public fun getContainerSlots(contentType: SlotContentType? = null): List<ContainerInventorySlot> {
        return if (contentType != null) {
            containerSlotsSequence.filter { it.contentType == contentType }.toList()
        } else {
            containerSlotsSequence.toList()
        }
    }

    /** Returns the inventory's GUI slots. */
    public fun getGuiSlots(): List<GuiInventorySlot> = slotsSequence.filterIsInstance<GuiInventorySlot>().toList()

    /** Constructs and returns [Inventory] that can be shown to a player. */
    override fun getInventory(): Inventory {
        return view ?: Bukkit.createInventory(this, size, displayName).also { view ->
            view.maxStackSize = maxStackSize
            view.contents = buildViewContents()
            this.view = view
        }
    }

    private fun buildViewContents(): Array<ItemStack> {
        return Array(size) { position -> getSlotAt(position).getView(placeholders, holder) }
    }

    /** Opens this inventory for the given [player]. */
    public fun open(player: Player) {
        player.openInventory(inventory)
    }

    /** Closes this inventory for all [viewers]. */
    public fun close() {
        viewers.toList().forEach(HumanEntity::closeInventory)
    }

    /** This method should be called when inventory close. */
    internal fun onClose() {
        // Remove view if it was the last viewer
        scheduler.runOnMain { if (viewers.isEmpty()) view = null }
    }

    /**
     * Removes [n]th slot with the specified [slotName] from the inventory.
     *
     * By default [n] is `0`, so it will remove the first slot for
     * the given [slotName].
     *
     * @return removed slot, or `null` if there are no slot with the given [slotName] or [n].
     */
    @JvmOverloads
    public fun removeSlot(slotName: String, n: Int = 0): InventorySlot? {
        val position = slotsByName[slotName]?.getOrNull(n)?.position ?: return null
        return removeSlotAt(position)
    }

    /**
     * Removes slot at the given [position] from the inventory,
     * replacing it with default inventory slot.
     *
     * @return removed slot.
     */
    public fun removeSlotAt(position: Int): InventorySlot = assignSlot(position, layout.defaultSlot)

    /**
     * Assigns given [slot] to the given [position], replacing existing slot.
     *
     * @return replaced slot.
     */
    public fun assignSlot(position: Int, slot: Slot): InventorySlot {
        val newSlot = createInventorySlot(slot, position)
        val (currentSlotName, n) = slotsIndex[position]

        val previousSlots = slotsByName.getValue(currentSlotName)
        val previousSlot = previousSlots.removeAt(n)

        if (previousSlots.isEmpty()) slotsByName.remove(currentSlotName)
        slotsByName.getOrPut(newSlot.name, ::mutableListOf).add(newSlot)
        updateSlotsIndex()

        return previousSlot
    }

    private fun createInventorySlot(slot: Slot, position: Int): InventorySlot {
        return if (slot is ContainerSlot) {
            ContainerInventorySlot(slot, this, position)
        } else {
            GuiInventorySlot(slot, this, position)
        }
    }

    // Should be called after any operation that changes slots.
    private fun updateSlotsIndex() {
        val slotsIndex = arrayOfNulls<Pair<String, Int>?>(size)
        for ((slotName, slots) in slotsByName) {
            slots.forEachIndexed { index, slot ->
                slotsIndex[slot.position] = slotName to index
            }
        }
        this.slotsIndex = slotsIndex.requireNoNulls()
    }

    /**
     * Returns the first empty that can hold the given [item]
     * or `null` if there are no such slots.
     */
    public fun findEmptySlot(item: ItemStack): ContainerInventorySlot? {
        return containerSlotsSequence
            .filter(ContainerInventorySlot::isEmpty)
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
        val nonFullSlots = mutableMapOf<Material, ContainerInventorySlot>()

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
        val itemsSlots = mutableMapOf<Material, ContainerInventorySlot>()

        for (i in items.indices) {
            val item = items[i]
            var toDelete = item.amount

            while (toDelete > 0) {
                val itemSlot = itemsSlots.remove(item.type) ?: findSlotByContentSimilar(item)

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
        return containerSlotsSequence.any { it.content.type == material }
    }

    /**
     * Checks if this inventory contains the given [item].
     *
     * @return `false` if the [item] is `null`.
     */
    public operator fun contains(item: ItemStack?): Boolean {
        if (item == null) return false
        return containerSlotsSequence.any { it.content == item }
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

        return containerSlotsSequence
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

        return containerSlotsSequence
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

        return containerSlotsSequence
            .map { it.content }
            .filter { it.isSimilar(item) }
            .scan(amount) { remainingAmount, currentItem -> remainingAmount - currentItem.amount }
            .any { it <= 0 }
    }

    /**
     * Returns the first slot in the inventory containing an `ItemStack` with
     * the given [material] or `null` if there are no such slot.
     */
    public fun findSlotByContent(material: Material): ContainerInventorySlot? {
        return containerSlotsSequence.find { it.content.type == material }
    }

    /**
     * Returns the first slot in the inventory containing the given [item].
     * This will only match a slot if both the type and the amount
     * of the stack match.
     * @see findSlotByContentSimilar
     */
    public fun findSlotByContent(item: ItemStack): ContainerInventorySlot? {
        return containerSlotsSequence.find { slot -> item == slot.content }
    }

    /**
     * Returns the first slot in the inventory containing the item similar
     * to the given [item].
     * @see findSlotByContent
     */
    private fun findSlotByContentSimilar(item: ItemStack): ContainerInventorySlot? {
        return containerSlotsSequence.find { slot -> item.isSimilar(slot.content) }
    }

    /**
     * Check whether this inventory is empty. An inventory is considered
     * to be empty if all container slots of this inventory are empty.
     */
    public fun isEmpty(): Boolean = containerSlotsSequence.all(ContainerInventorySlot::isEmpty)

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
        containerSlotsSequence
            .filter { predicate.test(it.content) }
            .forEach { it.content = AIR }
    }

    /** Clears out the whole inventory. */
    public fun clear() {
        (0 until size).forEach { position -> clearAt(position) }
    }

    /**
     * Clears out the [n]th slot with the given [slotName].
     *
     * By default [n] is `0`, so it will clear the first slot for
     * the given [slotName].
     */
    @JvmOverloads
    public fun clear(slotName: String, n: Int = 0) {
        setItem(slotName, n, null)
    }

    /** Clears out the slot assigned to the given [position]. */
    public fun clearAt(position: Int) {
        setItemAt(position, null)
    }

    internal fun syncSlotWithView(slot: ContainerInventorySlot) {
        // Do sync on the next tick for the case if it was called from click event
        scheduler.runOnMain {
            view?.setItem(slot.position, slot.getView(placeholders, holder))
        }
    }

    private fun findPartial(item: ItemStack?): ContainerInventorySlot? {
        if (item == null) return null

        return containerSlotsSequence.find { slot ->
            val slotItem = slot.content
            !slot.isFull() && slotItem.isSimilar(item)
        }
    }

    /**
     * Tries to handle the given [interaction].
     * @return `true` if interaction was handled successfully; `false` if interaction was not handled.
     */
    internal fun handleInteraction(interaction: InventoryInteraction): Boolean {
        return when (interaction) {
            is SlotInteraction -> handleSlotInteraction(interaction)

            is AddItemToInventory -> {
                val itemLeft = addItem(interaction.item).values.firstOrNull()
                interaction.setSlotItem(itemLeft.orEmpty())
                true
            }
        }
    }

    private fun handleSlotInteraction(interaction: SlotInteraction): Boolean {
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

        return result != Deny
    }

    /** Swap content with the given [item]. */
    private fun ContainerInventorySlot.swapItemInteraction(item: ItemStack): SlotInteractionResult = when {
        item.isEmpty() && this.isEmpty() -> Deny
        item.isEmpty() -> takeItemInteraction()
        item.amount > maxStackSize || !canHold(item) -> Deny
        this.isEmpty() -> placeItemInteraction(item)

        else -> {
            swapItem(item)
            Allow
        }
    }

    /** Takes item from this slot and returns result of this interaction. */
    private fun ContainerInventorySlot.takeItemInteraction(amount: Int = content.amount): SlotInteractionResult {
        val expectedCursor = getView(placeholders, this@CustomInventory.holder).cloneWithAmount(amount)
        val actualCursor = takeItem(amount)
        return when {
            actualCursor.isEmpty() -> Deny
            expectedCursor == actualCursor -> Allow
            else -> Change(currentItemReplacement = actualCursor)
        }
    }

    /** Places the given [item] to this slot and returns result of this interaction. */
    private fun ContainerInventorySlot.placeItemInteraction(
        item: ItemStack,
        amount: Int = item.amount,
    ): SlotInteractionResult {
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
            Deny
        }
    }

    public companion object {
        /**
         * By default, will be used stack size 64, and it will be increased when
         * will be added new slots with greater max stack size.
         * @see ContainerInventorySlot
         */
        public const val DEFAULT_MAX_STACK: Int = 64
    }
}
