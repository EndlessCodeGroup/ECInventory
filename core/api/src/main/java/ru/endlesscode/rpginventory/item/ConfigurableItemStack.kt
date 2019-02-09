package ru.endlesscode.rpginventory.item

import java.util.LinkedList
import java.util.Objects

open class ConfigurableItemStack {

    private var material: String? = null
    private var damage: Short? = null

    private var inherit: String? = null

    //Meta
    private var displayName: String? = null
    private var unbreakable: Boolean? = null
    private var lore: LinkedList<String>? = null

    private var enchantments: HashMap<String, Int>? = null // Enchantment:level
    private var itemFlags: ArrayList<String>? = null


    /**
     * Public default constructor for (de)serialization
     */
    constructor()

    protected constructor(cis: ConfigurableItemStack) {
        this.material = cis.material
        this.damage = cis.damage
        this.inherit = cis.inherit
        this.displayName = cis.displayName
        this.unbreakable = cis.unbreakable
        this.lore = cis.lore
        this.enchantments = cis.enchantments
        this.itemFlags = cis.itemFlags
    }

    fun resolveInheritance(inheritance: ConfigurableItemStack): ConfigurableItemStack {
        this.material = material ?: inheritance.material
        this.damage = damage ?: inheritance.damage
        //TODO: assign inheritance of the inherited ConfigurableItemStack?
        this.displayName = displayName ?: inheritance.displayName
        this.unbreakable = unbreakable ?: inheritance.unbreakable
        this.lore = lore ?: inheritance.lore
        this.enchantments = enchantments ?: inheritance.enchantments
        this.itemFlags = itemFlags ?: inheritance.itemFlags
        return this
    }

    fun getMaterial(): String = material ?: "AIR"

    fun getDamage(): Short = damage ?: 0

    fun getInherit(): String? = inherit

    fun getDisplayName(): String? = displayName

    fun isUnbreakable(): Boolean = unbreakable ?: false

    fun getLore(): List<String> = lore.orEmpty()

    fun getEnchantments(): Map<String, Int> = enchantments.orEmpty()

    fun getItemFlags(): List<String> = itemFlags.orEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConfigurableItemStack) return false

        return material == other.material &&
            damage == other.damage &&
            inherit == other.inherit &&
            displayName == other.displayName &&
            unbreakable == other.unbreakable &&
            lore == other.lore &&
            enchantments == other.enchantments &&
            itemFlags == other.itemFlags
    }

    override fun hashCode(): Int {
        return Objects.hash(material, damage, inherit, displayName, unbreakable, lore, enchantments, itemFlags)
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
            this.cis.material = material
        }

        fun withDamage(damage: Short): Builder {
            this.cis.damage = damage
            return this
        }

        fun withInheritance(of: String): Builder {
            this.cis.inherit = of
            return this
        }

        fun withDisplayName(displayName: String): Builder {
            this.cis.displayName = displayName
            return this
        }

        fun withLore(lore: List<String>): Builder {
            this.cis.lore = LinkedList(lore)
            return this
        }

        fun withItemFlags(vararg flags: String): Builder {
            this.cis.itemFlags = arrayListOf(*flags)
            return this
        }

        fun withEnchantments(enchantments: Map<String, Int>): Builder {
            this.cis.enchantments = HashMap(enchantments)
            return this
        }

        fun unbreakable(unbreakable: Boolean): Builder {
            this.cis.unbreakable = unbreakable
            return this
        }

        fun build(): ConfigurableItemStack {
            if (this.cis.damage == null) {
                this.cis.damage = 0
            }

            if (this.cis.unbreakable == null) {
                this.cis.unbreakable = false
            }

            return this.cis
        }
    }
}
