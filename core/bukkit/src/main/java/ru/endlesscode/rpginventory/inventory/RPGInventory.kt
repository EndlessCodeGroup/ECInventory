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
        val slot = getSlot(index)
        slot.content = item.orAir()
        syncSlotWithView(slot)
    }

    override fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun contains(material: Material?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contains(item: ItemStack?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contains(material: Material?, amount: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contains(item: ItemStack?, amount: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsAtLeast(item: ItemStack?, amount: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun all(material: Material): HashMap<Int, out ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun all(item: ItemStack?): HashMap<Int, out ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun first(material: Material): Int {
        val slot = getStorageSlots().first { it.content.type == material }
        return getIndexOfSlot(slot)
    }

    override fun first(item: ItemStack?): Int {
        return first(item, true)
    }

    override fun firstEmpty(): Int {
        return getStorageSlots().indexOfFirst { it.isEmpty() }
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
        slots[slotId]?.let { slot ->
            slot.content = item.orAir()
            syncSlotWithView(slot)
        }
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

    private fun first(item: ItemStack?, withAmount: Boolean): Int {
        if (item == null) return -1

        val slot = getStorageSlots().first { slot ->
            if (withAmount) item == slot.content
            else item.isSimilar(slot.content)
        }
        return getIndexOfSlot(slot)
    }

    private fun syncSlotWithView(slot: InventorySlot) {
        view?.setItem(slot.position, slot.getContentOrHolder())
    }
}
