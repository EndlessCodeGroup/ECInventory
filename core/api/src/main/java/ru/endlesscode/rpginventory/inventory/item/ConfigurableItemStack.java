package ru.endlesscode.rpginventory.inventory.item;

import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

public class ConfigurableItemStack {

    //Default values
    private String material = "AIR";
    //Woah. Do you ever seen that?
    private Short damage = null;
    private String inherit = null;
    //Meta
    private String displayName = null;
    private Boolean isUnbreakable = null;
    private LinkedList<String> lore = null;
    // Enchantment:level
    private HashMap<String, Integer> enchantments = null;
    private ArrayList<String> itemFlags = null;

    //Default constructor must be public for (de)serialization
    public ConfigurableItemStack() {
    }

    protected ConfigurableItemStack(ConfigurableItemStack cis) {
        this.material = cis.material;
        this.damage = cis.damage;
        this.inherit = cis.inherit;
        this.displayName = cis.displayName;
        this.isUnbreakable = cis.isUnbreakable;
        this.lore = cis.lore;
        this.enchantments = cis.enchantments;
        this.itemFlags = cis.itemFlags;
    }

    public ConfigurableItemStack resolveInheritance(ConfigurableItemStack inheritance) {
        this.material = ObjectUtils.defaultIfNull(material, inheritance.material);
        this.damage = ObjectUtils.defaultIfNull(damage, inheritance.damage);
        //TODO: assign inheritance of the inherited ConfigurableItemStack?
        this.displayName = ObjectUtils.defaultIfNull(displayName, inheritance.displayName);
        this.isUnbreakable = ObjectUtils.defaultIfNull(isUnbreakable, inheritance.isUnbreakable);
        this.lore = ObjectUtils.defaultIfNull(lore, inheritance.lore);
        this.enchantments = ObjectUtils.defaultIfNull(enchantments, inheritance.enchantments);
        this.itemFlags = ObjectUtils.defaultIfNull(itemFlags, inheritance.itemFlags);
        return this;
    }

    public String getMaterial() {
        return material;
    }

    public short getDamage() {
        return ObjectUtils.defaultIfNull(damage, (short) 0);
    }

    public String getInherit() {
        return inherit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isUnbreakable() {
        return ObjectUtils.defaultIfNull(isUnbreakable, false);
    }

    public List<String> getLore() {
        return ObjectUtils.defaultIfNull(lore, Collections.emptyList());
    }

    public Map<String, Integer> getEnchantments() {
        return ObjectUtils.defaultIfNull(enchantments, Collections.emptyMap());
    }

    public List<String> getItemFlags() {
        return ObjectUtils.defaultIfNull(itemFlags, Collections.emptyList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurableItemStack that = (ConfigurableItemStack) o;
        return Objects.equals(material, that.material) &&
                Objects.equals(damage, that.damage) &&
                Objects.equals(inherit, that.inherit) &&
                Objects.equals(displayName, that.displayName) &&
                Objects.equals(isUnbreakable, that.isUnbreakable) &&
                Objects.equals(lore, that.lore) &&
                Objects.equals(enchantments, that.enchantments) &&
                Objects.equals(itemFlags, that.itemFlags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, damage, inherit, displayName, isUnbreakable, lore, enchantments, itemFlags);
    }

    public static class Builder {
        private final ConfigurableItemStack cis;

        private Builder(String material) {
            this.cis = new ConfigurableItemStack();
            this.cis.material = material;
        }

        public static Builder fromMaterial(String material) {
            return new Builder(material);
        }

        public Builder withDamage(short damage) {
            this.cis.damage = damage;
            return this;
        }

        public Builder withInheritance(String of) {
            this.cis.inherit = of;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.cis.displayName = displayName;
            return this;
        }

        public Builder withLore(List<String> lore) {
            this.cis.lore = new LinkedList<>();
            this.cis.lore.addAll(lore);
            return this;
        }

        public Builder withItemFlags(String... flags) {
            this.cis.itemFlags = new ArrayList<>();
            Collections.addAll(this.cis.itemFlags, flags);
            return this;
        }

        public Builder withEnchantments(Map<String, Integer> enchantments) {
            this.cis.enchantments = new HashMap<>();
            this.cis.enchantments.putAll(enchantments);
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            this.cis.isUnbreakable = unbreakable;
            return this;
        }

        public ConfigurableItemStack build() {
            if (this.cis.damage == null) {
                this.cis.damage = 0;
            }

            if (this.cis.isUnbreakable == null) {
                this.cis.isUnbreakable = false;
            }

            return this.cis;
        }
    }
}
