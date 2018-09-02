package ru.endlesscode.rpginventory.inventory

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.extensions.orAir
import ru.endlesscode.rpginventory.extensions.roundToPowerOf
import ru.endlesscode.rpginventory.util.IndexedMap
import ru.endlesscode.rpginventory.util.toIndexedMap
import java.util.*
import kotlin.math.min


/**
 * Provides utilities for working with RPG inventory, as with Bukkit inventory.
 *
 * @param owner The Inventory's owner.
 * @param layout Layout of the inventory.
 */
class RPGInventory(
        private val owner: InventoryHolder,
        private val layout: InventoryLayout
) : Inventory {

    companion object {
        /**
         * By default will be used stack size 1 and it will be increased when
         * will be added new slots with greater max stack size.
         * @see InventorySlot
         */
        const val DEFAULT_MAX_STACK = 1
    }

    /**
     * Temporary [Inventory], used to show RPGInventory to player.
     */
    var view: Inventory? = null

    private val internalSlotsMap: IndexedMap<Int, Slot> = layout.slotsMap.toIndexedMap()
    private val slots: MutableMap<String, InventorySlot>

    /**
     * View size is maximal slot position rounded to nine
     */
    private val viewSize: Int
        get() = internalSlotsMap.lastKey().roundToPowerOf(9)

    private var maxStack = DEFAULT_MAX_STACK

    init {
        val slots = mutableMapOf<String, InventorySlot>()
        for ((position, slot) in internalSlotsMap) {
            slots[slot.id] = InventorySlot(slot, this, position)
        }
        this.slots = slots
    }

    /**
     * Returns the ItemStack found in the slot with the given [id][slotId], or `null` if there no such slot.
     */
    fun getItem(slotId: String): ItemStack? {
        return slots[slotId]?.content
    }

    /**
     * Stores the ItemStack at the slot with given id.
     *
     * @param slotId The id of the slot where to put the ItemStack.
     * @param item The ItemStack to set.
     */
    fun setItem(slotId: String, item: ItemStack?) {
        slots[slotId]?.let { it.content = item.orAir() }
    }

    /**
     * Returns the slot with the given [id][slotId], or `null` if there no such slot.
     */
    fun getSlot(slotId: String): InventorySlot? {
        return slots[slotId]
    }

    /**
     * Returns slot by [index], or throws an exception if there no such slot.
     *
     * @throws IndexOutOfBoundsException when the inventory doesn't contain a slot for the specified index.
     */
    fun getSlot(index: Int): InventorySlot {
        val slotId = internalSlotsMap.getByIndex(index).id
        return slots.getValue(slotId)
    }

    /**
     * Returns slot by theirs [position] or `null` if there no slot on given position.
     */
    fun getSlotAt(position: Int): InventorySlot? {
        return internalSlotsMap[position]?.let {
            slots[it.id]
        }
    }

    /**
     * Returns index of slot with given [id][slotId] or -1 if there no such slot.
     */
    fun getIndexOfSlot(slotId: String): Int {
        return slots[slotId]?.let {
            internalSlotsMap.getIndexOf(it.position)
        } ?: -1
    }

    /**
     * Returns index of slot with given [slot] or -1 if given slot isn't in the inventory.
     */
    fun getIndexOfSlot(slot: InventorySlot): Int {
        return if (slot.holder != this) -1
        else internalSlotsMap.getIndexOf(slot.position)
    }

    /**
     * Returns the inventory's slots with the given [type] or all slots if type is `null`.
     */
    @JvmOverloads
    fun getSlots(type: Slot.Type? = null): List<InventorySlot> {
        return if (type == null) {
            slots.values.toList()
        } else {
            slots.values.filter { it.type == type }
        }
    }

    /**
     * Clears out a particular slot with given [id][slotId].
     */
    fun clear(slotId: String) {
        setItem(slotId, null)
    }

    /**
     * Returns the inventory's passive slots.
     */
    fun getPassiveSlots(): List<InventorySlot> {
        return getSlots(Slot.Type.PASSIVE)
    }

    /**
     * Returns the inventory's storage slots.
     */
    fun getStorageSlots(): List<InventorySlot> {
        return getSlots(Slot.Type.STORAGE)
    }

    /**
     * Returns the inventory's active slots.
     */
    fun getActiveSlots(): List<InventorySlot> {
        return getSlots(Slot.Type.ACTIVE)
    }

    /**
     * Constructs and returns [Inventory] that can be shown to a player.
     */
    fun constructView(): Inventory {
        return view ?: Bukkit.createInventory(holder, viewSize, title).also { view ->
            view.maxStackSize = maxStackSize
            view.contents = buildViewContents()
            this.view = view
        }
    }

    /**
     * This method should be called when inventory close.
     */
    fun onClose() {
        this.view = null
    }

    /**
     * Assigns given [slot] to the given [position], with replace of existing slot.
     */
    fun assignSlot(position: Int, slot: Slot) {
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
    fun removeSlot(slotId: String): InventorySlot? {
        val removedSlot = slots.remove(slotId)
        if (removedSlot != null) {
            internalSlotsMap.remove(removedSlot.position)
        }

        return removedSlot
    }

    /**
     * Returns the first empty Slot or `null` if there no empty slots.
     */
    fun firstEmptySlot(): InventorySlot? {
        return getStorageSlots().firstOrNull { it.isEmpty() }
    }

    override fun getSize(): Int {
        return slots.size
    }

    @Deprecated("Use slot's maxStackSize instead")
    override fun getMaxStackSize(): Int {
        return maxStack
    }

    override fun setMaxStackSize(size: Int) {
        maxStack = size
    }

    override fun getName(): String {
        return layout.name
    }

    override fun getItem(index: Int): ItemStack {
        return getSlot(index).content
    }

    override fun setItem(index: Int, item: ItemStack?) {
        getSlot(index).content = item.orAir()
    }

    /**
     * It was copied from CraftBukkit implementation.
     *
     * Also was made some optimizations:
     *  - Record the `firstPartial` per [Material]
     *  - Cache `firstEmptySlot` result
     */
    override fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        val leftover = hashMapOf<Int, ItemStack>()
        val nonFullSlots = mutableMapOf<Material, InventorySlot>()

        /*
         * TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         */

        var freeSlot = firstEmptySlot()
        for (i in items.indices) {
            val item = items[i]

            while (true) {
                // Do we already have a stack of it?
                val nonFullSlot = nonFullSlots.getOrElse(item.type) { firstPartial(item) }

                // Drat! no partial stack
                if (nonFullSlot == null) {
                    // Find a free spot!

                    if (freeSlot == null) {
                        // No space at all!
                        leftover[i] = item
                        break
                    }

                    // More than a single stack!
                    if (item.amount > freeSlot.maxStackSize) {
                        val stack = item.clone()
                        stack.amount = freeSlot.maxStackSize
                        freeSlot.content = stack
                        item.amount -= freeSlot.maxStackSize

                        // Look for new free slot
                        freeSlot = firstEmptySlot()
                    } else {
                        // Just store it and look for new free slot
                        freeSlot.content = item
                        // Remember that there are the non-full slot
                        nonFullSlots[item.type] = freeSlot
                        freeSlot = firstEmptySlot()
                        break
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    val partialItem = nonFullSlot.content

                    val amount = item.amount
                    val partialAmount = partialItem.amount
                    val maxAmount = min(partialItem.maxStackSize, nonFullSlot.maxStackSize)

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.amount = amount + partialAmount
                        nonFullSlot.content = partialItem
                        // Remember that there are the non-full slot
                        nonFullSlots[item.type] = nonFullSlot
                        break
                    }

                    // It fits partially
                    partialItem.amount = maxAmount
                    // To make sure the packet is sent to the client
                    nonFullSlot.content = partialItem
                    nonFullSlots.remove(item.type)
                    item.amount = amount + partialAmount - maxAmount
                }
            }
        }

        return leftover
    }

    /**
     * It was copied from CraftBukkit implementation.
     *
     * Also was made some optimizations:
     *  - Cache `first` result per [Material]
     */
    override fun removeItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        val leftover = hashMapOf<Int, ItemStack>()
        val itemsSlots = mutableMapOf<Material, InventorySlot>()

        // TODO: optimization

        for (i in items.indices) {
            val item = items[i]
            var toDelete = item.amount

            while (true) {
                val itemSlot = itemsSlots.getOrElse(item.type) { first(item, false) }

                // Drat! we don't have this type in the inventory
                if (itemSlot == null) {
                    item.amount = toDelete
                    leftover[i] = item
                    break
                } else {
                    val itemStack = itemSlot.content
                    val amount = itemStack.amount

                    if (amount <= toDelete) {
                        toDelete -= amount
                        // Clear the slot, all used up
                        clear(itemSlot.id)
                        itemsSlots.remove(item.type)
                    } else {
                        // Split the stack and store
                        itemStack.amount = amount - toDelete
                        itemSlot.content = itemStack
                        toDelete = 0
                        // Remember that there are non-fully empty slot
                        itemsSlots[item.type] = itemSlot
                    }
                }

                // Bail when done
                if (toDelete <= 0) break
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

    override fun first(item: ItemStack?): Int {
        val slot = first(item, true) ?: return -1
        return getIndexOfSlot(slot)
    }

    override fun firstEmpty(): Int {
        val slot = firstEmptySlot() ?: return -1
        return getIndexOfSlot(slot)
    }

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

    override fun getViewers(): List<HumanEntity> {
        return view?.viewers ?: emptyList()
    }

    override fun getTitle(): String {
        return name
    }

    override fun getType(): InventoryType {
        return InventoryType.CHEST
    }

    override fun getHolder(): InventoryHolder {
        return owner
    }

    override fun iterator(): MutableListIterator<ItemStack> {
        return InventoryIterator(this)
    }

    override fun iterator(index: Int): MutableListIterator<ItemStack> {
        // ie, with -1, previous() will return the last element
        val validIndex = if (index < 0) index + size + 1 else index
        return InventoryIterator(this, validIndex)
    }

    override fun getLocation(): Location {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal fun syncSlotWithView(slot: InventorySlot) {
        view?.setItem(slot.position, slot.getContentOrHolder())
    }

    private fun setSlots(slots: List<InventorySlot>, items: Array<out ItemStack>) {
        if (slots.size < items.size) error("items.length should be ${slots.size} or less")

        slots.forEachIndexed { index, slot ->
            setItem(slot.id, items.getOrNull(index))
        }
    }

    private fun buildViewContents(): Array<ItemStack> {
        val contents = Array(viewSize) { layout.filler }
        for (slot in getSlots()) {
            contents[slot.position] = slot.getContentOrHolder()
        }
        return contents
    }

    private fun first(item: ItemStack?, withAmount: Boolean): InventorySlot? {
        if (item == null) return null

        return getStorageSlots().firstOrNull { slot ->
            if (withAmount) item == slot.content
            else item.isSimilar(slot.content)
        }
    }

    private fun firstPartial(item: ItemStack?): InventorySlot? {
        if (item == null) return null

        return getStorageSlots().firstOrNull { slot ->
            val slotItem = slot.content
            slotItem.amount < slotItem.maxStackSize  && slotItem.amount < slot.maxStackSize && slotItem.isSimilar(item)
        }
    }
}
