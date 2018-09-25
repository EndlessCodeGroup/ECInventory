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
        //TODO: Merge or overwrite lists and maps? My choice is overwrite
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
}
