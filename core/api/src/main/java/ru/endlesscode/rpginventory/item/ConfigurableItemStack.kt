package ru.endlesscode.rpginventory.item

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.Objects

@ConfigSerializable
open class ConfigurableItemStack {

    @Setting("material")
    private var _material: String? = null
    @Setting("damage")
    private var _damage: Int? = null

    @Setting("inherit")
    private var _inherit: String? = null

    //Meta
    @Setting("displayName")
    private var _displayName: String? = null
    @Setting("unbreakable")
    private var _unbreakable: Boolean? = null
    @Setting("lore")
    private var _lore: List<String>? = null

    @Setting("enchantments")
    private var _enchantments: Map<String, Int>? = null // Enchantment:level
    @Setting("itemFlags")
    private var _itemFlags: List<String>? = null

    val material: String get() = _material ?: "AIR"
    val damage: Int get() = _damage ?: 0
    val inheritance: String? get() = _inherit
    val displayName: String? get() = _displayName
    val isUnbreakable: Boolean get() = _unbreakable ?: false
    val lore: List<String> get() = _lore.orEmpty()
    val enchantments: Map<String, Int> get() = _enchantments.orEmpty()
    val itemFlags: List<String> get() = _itemFlags.orEmpty()

    private constructor()

    protected constructor(cis: ConfigurableItemStack) {
        this._material = cis._material
        this._damage = cis._damage
        this._inherit = cis._inherit
        this._displayName = cis._displayName
        this._unbreakable = cis._unbreakable
        this._lore = cis._lore
        this._enchantments = cis._enchantments
        this._itemFlags = cis._itemFlags
    }

    fun resolveInheritance(inheritance: ConfigurableItemStack): ConfigurableItemStack {
        this._material = _material ?: inheritance._material
        this._damage = _damage ?: inheritance._damage
        //TODO: assign inheritance of the inherited ConfigurableItemStack?
        this._displayName = _displayName ?: inheritance._displayName
        this._unbreakable = _unbreakable ?: inheritance._unbreakable
        this._lore = _lore ?: inheritance._lore
        this._enchantments = _enchantments ?: inheritance._enchantments
        this._itemFlags = _itemFlags ?: inheritance._itemFlags
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConfigurableItemStack) return false

        return material == other.material &&
            damage == other.damage &&
            inheritance == other.inheritance &&
            displayName == other.displayName &&
            isUnbreakable == other.isUnbreakable &&
            lore == other.lore &&
            enchantments == other.enchantments &&
            itemFlags == other.itemFlags
    }

    override fun hashCode(): Int {
        return Objects.hash(material, damage, inheritance, displayName, isUnbreakable, lore, enchantments, itemFlags)
    }

    override fun toString(): String {
        return "ConfigurableItemStack(material=$material, damage=$damage, displayName=$displayName)"
    }


    class Builder private constructor(material: String) {

        companion object {
            @JvmStatic
            fun fromMaterial(material: String): Builder {
                return Builder(material)
            }
        }

        private val cis: ConfigurableItemStack = ConfigurableItemStack()

        init {
            this.cis._material = material
        }

        fun withDamage(damage: Int): Builder {
            this.cis._damage = damage
            return this
        }

        fun withInheritance(of: String): Builder {
            this.cis._inherit = of
            return this
        }

        fun withDisplayName(displayName: String): Builder {
            this.cis._displayName = displayName
            return this
        }

        fun withLore(lore: List<String>): Builder {
            this.cis._lore = ArrayList(lore)
            return this
        }

        fun withItemFlags(vararg flags: String): Builder {
            this.cis._itemFlags = flags.toList()
            return this
        }

        fun withEnchantments(enchantments: Map<String, Int>): Builder {
            this.cis._enchantments = HashMap(enchantments)
            return this
        }

        fun unbreakable(unbreakable: Boolean): Builder {
            this.cis._unbreakable = unbreakable
            return this
        }

        fun build(): ConfigurableItemStack = this.cis
    }
}
