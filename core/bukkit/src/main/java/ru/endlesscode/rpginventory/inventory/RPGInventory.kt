package ru.endlesscode.rpginventory.inventory

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.endlesscode.rpginventory.extensions.isEmpty
import ru.endlesscode.rpginventory.extensions.orAir
import java.util.*


/**
 * Provides utilities for working with RPG inventory, as with Bukkit inventory.
 *
 * @param owner The Inventory's owner.
 * @param layout Layout of the inventory.
 *
 * @property view Temporary [Inventory], used to show RPGInventory to player.
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

    var view: Inventory? = null

    private val slots = linkedMapOf<String, InventorySlot>()
    private val viewSize: Int

    private var maxStack = DEFAULT_MAX_STACK

    init {
        for ((position, slot) in layout.slotsMap) {
            slots[slot.id] = InventorySlot(slot, this, position)
        }

        val maxSlotPosition = layout.slotsMap.lastKey()
        // Round view size to nine
        this.viewSize = maxSlotPosition / 9 * 9
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
        return contents[index]
    }

    override fun setItem(index: Int, item: ItemStack?) {
        val slot = getSlots()[index]
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

    override fun all(material: Material?): HashMap<Int, out ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun all(item: ItemStack?): HashMap<Int, out ItemStack> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun first(material: Material?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun first(item: ItemStack?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun remove(item: ItemStack?) {
        if (item.isEmpty()) return

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
     * Returns the ItemStack found in the slot with the given id.
     *
     * @param slotId The id of the Slot's ItemStack to return
     * @return The ItemStack in the slot or null if there no slot with the given id.
     */
    fun getItem(slotId: String): ItemStack? {
        return slots[slotId]?.content
    }

    /**
     * Stores the ItemStack at the given slot of the RPG inventory.
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
     * Returns the slot with the given id.
     *
     * @param slotId The id of the slot to return.
     * @return The slot or null if there no slot with the given id.
     */
    fun getSlot(slotId: String): InventorySlot? {
        return slots[slotId]
    }

    /**
     * Returns the inventory's slots.
     *
     * @return The slots.
     */
    fun getSlots(): List<InventorySlot> {
        return slots.values.toList()
    }

    /**
     * Clears out a particular slot with given id.
     *
     * @param slotId The slot's id to empty.
     */
    fun clear(slotId: String) {
        setItem(slotId, null)
    }

    /**
     * Returns the inventory's passive slots.
     *
     * @return The passive slots.
     */
    fun getPassiveSlots(): List<InventorySlot> {
        return slots.values.filter { it.type == Slot.Type.PASSIVE }
    }

    /**
     * Returns the inventory's storage slots.
     *
     * @return The storage slots.
     */
    fun getStorageSlots(): List<InventorySlot> {
        return slots.values.filter { it.type == Slot.Type.STORAGE }
    }

    /**
     * Returns the inventory's active slots.
     *
     * @return The active slots.
     */
    fun getActiveSlots(): List<InventorySlot> {
        return slots.values.filter { it.type == Slot.Type.ACTIVE }
    }

    /**
     * Constructs and returns [Inventory] that can be shown to a player.
     *
     * @return The constructed inventory
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


    private fun setSlots(slots: List<InventorySlot>, items: Array<out ItemStack>) {
        if (slots.size < items.size) error("items.length should be ${slots.size} or less")

        for (i in slots.indices) {
            val item = if (i >= items.size) null else items[i]
            setItem(i, item)
        }
    }

    private fun buildViewContents(): Array<ItemStack> {
        val contents = Array(viewSize) { layout.filler }
        for (slot in getSlots()) {
            contents[slot.position] = slot.getContentOrHolder()
        }
        return contents
    }

    private fun syncSlotWithView(slot: InventorySlot) {
        view?.setItem(slot.position, slot.getContentOrHolder())
    }
}
