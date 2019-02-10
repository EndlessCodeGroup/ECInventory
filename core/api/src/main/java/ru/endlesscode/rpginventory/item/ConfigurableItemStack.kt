package ru.endlesscode.rpginventory.item

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.Objects

@ConfigSerializable
open class ConfigurableItemStack {

    @Setting var material: String = "AIR"
        private set
    @Setting var damage: Int = 0
        private set

    //Meta
    @Setting var displayName: String? = null
        private set
    @Setting var unbreakable: Boolean = false
        private set
    @Setting var lore: List<String> = emptyList()
        private set

    @Setting var enchantments: Map<String, Int> = emptyMap() // Enchantment:level
        private set
    @Setting var itemFlags: List<String> = emptyList()
        private set

    private constructor()

    protected constructor(cis: ConfigurableItemStack) {
        this.material = cis.material
        this.damage = cis.damage
        this.displayName = cis.displayName
        this.unbreakable = cis.unbreakable
        this.lore = cis.lore
        this.enchantments = cis.enchantments
        this.itemFlags = cis.itemFlags
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConfigurableItemStack) return false

        return material == other.material &&
            damage == other.damage &&
            displayName == other.displayName &&
            unbreakable == other.unbreakable &&
            lore == other.lore &&
            enchantments == other.enchantments &&
            itemFlags == other.itemFlags
    }

    override fun hashCode(): Int {
        return Objects.hash(material, damage, displayName, unbreakable, lore, enchantments, itemFlags)
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
            this.cis.material = material
        }

        fun withDamage(damage: Int): Builder {
            this.cis.damage = damage
            return this
        }

        fun withDisplayName(displayName: String): Builder {
            this.cis.displayName = displayName
            return this
        }

        fun withLore(lore: List<String>): Builder {
            this.cis.lore = ArrayList(lore)
            return this
        }

        fun withItemFlags(vararg flags: String): Builder {
            this.cis.itemFlags = flags.toList()
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

        fun build(): ConfigurableItemStack = this.cis
    }
}
