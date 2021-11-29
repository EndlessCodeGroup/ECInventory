/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
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
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.endlesscode.inventory.internal.TaskScheduler
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.listener.*
import ru.endlesscode.inventory.internal.util.*
import ru.endlesscode.inventory.slot.*
import ru.endlesscode.inventory.slot.SlotInteractionResult.Change
import java.util.*

/**
 * Provides utilities for working with RPG inventory, as with Bukkit inventory.
 *
 * @property id Unique ID of the inventory.
 * @param holder The Inventory's holder.
 * @param layout Layout of the inventory.
 */
public class CustomInventory internal constructor(
    public val id: UUID,
    private val layout: InventoryLayout,
    private val scheduler: TaskScheduler = DI.scheduler,
) : Inventory, InventoryHolder {

    /** Returns inventory layout name. */
    public val name: String get() = layout.name

    /** Returns inventory layout ID. It can be considered as an inventory type. */
    public val type: String get() = layout.id

    /** Temporary [Inventory], used to show [CustomInventory] to player. */
    private var view: Inventory? = null

    private val internalSlotsMap: IndexedMap<Int, Slot> = layout.slotsMap.asIndexedMap()
    private val slots: MutableMap<String, InventorySlot>

    /** View size is maximal slot position rounded to nine. */
    internal val viewSize: Int
        get() = internalSlotsMap.lastKey().roundToPowerOf(9)

    private var maxStack = DEFAULT_MAX_STACK

    public constructor(layout: InventoryLayout) : this(
        id = UUID.randomUUID(),
        layout = layout,
        scheduler = DI.scheduler,
    )

    init {
        val slots = mutableMapOf<String, InventorySlot>()
        for ((position, slot) in internalSlotsMap) {
            slots[slot.id] = InventorySlot(slot, this, position)
        }
        this.slots = slots
    }

    /** Returns the ItemStack found in the slot with the given [id][slotId], or `null` if there no such slot. */
    public fun getItem(slotId: String): ItemStack? = slots[slotId]?.content

    /**
     * Stores the ItemStack at the slot with given id.
     *
     * @param slotId The id of the slot where to put the ItemStack.
     * @param item The ItemStack to set.
     */
    public fun setItem(slotId: String, item: ItemStack?) {
        slots[slotId]?.let { it.content = item.orEmpty() }
    }

    /** Returns the slot with the given [id][slotId], or `null` if there no such slot. */
    public fun getSlot(slotId: String): InventorySlot? = slots[slotId]

    /**
     * Returns slot by [index], or throws an exception if there no such slot.
     *
     * @throws IndexOutOfBoundsException when the inventory doesn't contain a slot for the specified index.
     */
    public fun getSlot(index: Int): InventorySlot {
        val slotId = internalSlotsMap.getByIndex(index).id
        return slots.getValue(slotId)
    }

    /** Returns slot by theirs [position] or `null` if there no slot on given position. */
    public fun getSlotAt(position: Int): InventorySlot? {
        return internalSlotsMap[position]?.let {
            slots[it.id]
        }
    }

    /** Returns index of slot with given [id][slotId] or -1 if there no such slot. */
    public fun getIndexOfSlot(slotId: String): Int {
        return slots[slotId]?.let {
            internalSlotsMap.getIndexOf(it.position)
        } ?: -1
    }

    /** Returns index of slot with given [slot] or -1 if given slot isn't in the inventory. */
    public fun getIndexOfSlot(slot: InventorySlot): Int {
        return if (slot.holder == this) internalSlotsMap.getIndexOf(slot.position) else -1
    }

    /** Returns the inventory's slots with the given [type] or all slots if type is `null`. */
    @JvmOverloads
    public fun getSlots(type: Slot.Type? = null): List<InventorySlot> {
        return if (type == null) {
            slots.values.toList()
        } else {
            slots.values.filter { it.type == type }
        }
    }

    /** Clears out a particular slot with given [slotId]. */
    public fun clear(slotId: String) {
        setItem(slotId, null)
    }

    /** Returns the inventory's passive slots. */
    public fun getPassiveSlots(): List<InventorySlot> = getSlots(Slot.Type.PASSIVE)

    /** Returns the inventory's storage slots. */
    public fun getStorageSlots(): List<InventorySlot> = getSlots(Slot.Type.STORAGE)

    /** Returns the inventory's active slots. */
    public fun getActiveSlots(): List<InventorySlot> = getSlots(Slot.Type.ACTIVE)

    /** Constructs and returns [Inventory] that can be shown to a player. */
    override fun getInventory(): Inventory {
        return view ?: Bukkit.createInventory(holder, viewSize, name).also { view ->
            view.maxStackSize = maxStackSize
            view.contents = buildViewContents()
            this.view = view
        }
    }

    /** Opens this inventory for the given [player]. */
    public fun open(player: Player) {
        player.openInventory(holder.inventory)
    }

    /** This method should be called when inventory close. */
    internal fun onClose() {
        this.view = null
    }

    /** Assigns given [slot] to the given [position], with replace of existing slot. */
    public fun assignSlot(position: Int, slot: Slot) {
        val inventorySlot = InventorySlot(slot, this, position)
        val existingSlotId = internalSlotsMap[position]?.id

        internalSlotsMap[position] = inventorySlot
        existingSlotId?.let { slots.remove(it) }
        slots[inventorySlot.id] = inventorySlot
    }

    /**
     * Removes slot with the specified [id][slotId] from the inventory.
     *
     * @return removed slot, or `null` if there no slot with the given id.
     */
    public fun removeSlot(slotId: String): InventorySlot? {
        val removedSlot = slots.remove(slotId)
        if (removedSlot != null) {
            internalSlotsMap.remove(removedSlot.position)
        }

        return removedSlot
    }

    /** Returns the first empty Slot or `null` if there are no empty slots. */
    public fun findEmptySlot(): InventorySlot? = getStorageSlots().find { it.isEmpty() }

    override fun getSize(): Int = slots.size

    @Deprecated("Use slot's maxStackSize instead")
    override fun getMaxStackSize(): Int = maxStack

    override fun setMaxStackSize(size: Int) {
        maxStack = size
    }

    override fun getItem(index: Int): ItemStack = getSlot(index).content

    override fun setItem(index: Int, item: ItemStack?) {
        getSlot(index).content = item.orEmpty()
    }

    override fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        val leftover = hashMapOf<Int, ItemStack>()
        val nonFullSlots = mutableMapOf<Material, InventorySlot>()

        var freeSlot = findEmptySlot()
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
                freeSlot = findEmptySlot()

                // Remember that there are the non-full slot
                if (!slot.isFull()) nonFullSlots[item.type] = slot
            }
        }

        return leftover
    }

    override fun removeItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        val leftover = hashMapOf<Int, ItemStack>()
        val itemsSlots = mutableMapOf<Material, InventorySlot>()

        for (i in items.indices) {
            val item = items[i]
            var toDelete = item.amount

            while (toDelete > 0) {
                val itemSlot = itemsSlots.remove(item.type) ?: find(item)

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

    override fun getContents(): Array<ItemStack> {
        return slots.values
            .map { it.content }
            .toTypedArray()
    }

    override fun setContents(items: Array<out ItemStack>) {
        setSlots(getSlots(), items)
    }

    override fun getStorageContents(): Array<ItemStack> {
        return getStorageSlots()
            .map { it.content }
            .toTypedArray()
    }

    override fun setStorageContents(items: Array<out ItemStack>) {
        setSlots(getStorageSlots(), items)
    }

    override fun contains(material: Material): Boolean {
        for (slot in getStorageSlots()) {
            if (slot.content.type == material) return true
        }

        return false
    }

    override fun contains(item: ItemStack?): Boolean {
        if (item == null) return false

        for (slot in getStorageSlots()) {
            if (slot.content == item) return true
        }

        return false
    }

    override fun contains(material: Material, amount: Int): Boolean {
        if (amount <= 0) return true

        var remainingAmount = amount
        for (slot in getStorageSlots()) {
            val item = slot.content
            if (item.type == material) {
                remainingAmount -= item.amount
                if (remainingAmount <= 0) return true
            }
        }

        return false
    }

    override fun contains(item: ItemStack?, amount: Int): Boolean {
        if (item == null) return false
        if (amount <= 0) return true

        var remainingAmount = amount
        for (slot in getStorageSlots()) {
            if (slot.content == item && --remainingAmount <= 0) return true
        }

        return false
    }

    override fun containsAtLeast(item: ItemStack?, amount: Int): Boolean {
        if (item == null) return false
        if (amount <= 0) return true

        var remainingAmount = amount
        for (slot in getStorageSlots()) {
            val currentItem = slot.content
            if (currentItem.isSimilar(item)) {
                remainingAmount -= currentItem.amount
                if (remainingAmount <= 0) return true
            }
        }

        return false
    }

    override fun all(material: Material): HashMap<Int, out ItemStack> {
        val slots = hashMapOf<Int, ItemStack>()

        for (slot in getStorageSlots()) {
            if (slot.content.type == material) {
                slots[getIndexOfSlot(slot)] = slot.content
            }
        }

        return slots
    }

    override fun all(item: ItemStack?): HashMap<Int, out ItemStack> {
        if (item == null) return hashMapOf()

        val slots = hashMapOf<Int, ItemStack>()
        for (slot in getStorageSlots()) {
            if (slot.content == item) {
                slots[getIndexOfSlot(slot)] = slot.content
            }
        }

        return slots
    }

    override fun first(material: Material): Int {
        val slot = getStorageSlots().firstOrNull { it.content.type == material } ?: return -1
        return getIndexOfSlot(slot)
    }

    override fun first(item: ItemStack): Int {
        val slot = find(item, withAmount = true) ?: return -1
        return getIndexOfSlot(slot)
    }

    override fun firstEmpty(): Int {
        val slot = findEmptySlot() ?: return -1
        return getIndexOfSlot(slot)
    }

    override fun isEmpty(): Boolean = slots.values.all(InventorySlot::isEmpty)

    override fun remove(material: Material) {
        for (slot in getStorageSlots()) {
            if (slot.content.type == material) {
                clear(slot.id)
            }
        }
    }

    override fun remove(item: ItemStack) {
        for (slot in getStorageSlots()) {
            if (slot.content == item) {
                clear(slot.id)
            }
        }
    }

    override fun clear(index: Int) {
        setItem(index, null)
    }

    override fun clear() {
        slots.keys.forEach(::clear)
    }

    override fun getViewers(): List<HumanEntity> = view?.viewers.orEmpty()

    override fun getType(): InventoryType = InventoryType.CHEST

    override fun getHolder(): InventoryHolder = this

    override fun iterator(): MutableListIterator<ItemStack> = InventoryIterator(this)

    override fun iterator(index: Int): MutableListIterator<ItemStack> {
        // ie, with -1, previous() will return the last element
        val validIndex = if (index < 0) index + size + 1 else index
        return InventoryIterator(this, validIndex)
    }

    override fun getLocation(): Location? {
        return when (val holder = this.holder) {
            is Container -> holder.location
            is Entity -> holder.location
            else -> null
        }
    }

    internal fun syncSlotWithView(slot: InventorySlot) {
        // Do sync on the next tick for the case if it was called from click event
        scheduler.runOnMain {
            view?.setItem(slot.position, slot.getContentOrTexture())
        }
    }

    private fun setSlots(slots: List<InventorySlot>, items: Array<out ItemStack>) {
        if (slots.size < items.size) error("items.length should be ${slots.size} or less")

        slots.forEachIndexed { index, slot ->
            setItem(slot.id, items.getOrNull(index))
        }
    }

    private fun buildViewContents(): Array<ItemStack> {
        val contents = Array(viewSize) { layout.emptySlotTexture }
        for (slot in getSlots()) {
            contents[slot.position] = slot.getContentOrTexture()
        }
        return contents
    }

    private fun find(item: ItemStack, withAmount: Boolean = false): InventorySlot? {
        return getStorageSlots().find { slot ->
            if (withAmount) item == slot.content
            else item.isSimilar(slot.content)
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
        item.isEmpty() && this.isEmpty() || item.amount > maxStackSize -> SlotInteractionResult.Deny
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
