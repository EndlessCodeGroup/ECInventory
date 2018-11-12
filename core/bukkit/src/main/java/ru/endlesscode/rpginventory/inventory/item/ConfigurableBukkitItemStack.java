package ru.endlesscode.rpginventory.inventory.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConfigurableBukkitItemStack extends ConfigurableItemStack {

    private ConfigurableBukkitItemStack() {
        super();
    }

    private ConfigurableBukkitItemStack(ConfigurableItemStack cis) {
        super(cis);
    }

    public static ConfigurableBukkitItemStack from(ConfigurableItemStack cis) {
        return new ConfigurableBukkitItemStack(cis);
    }

    public ItemStack toItemStack() {
        final Material material = Material.getMaterial(this.getMaterial());
        if (material == null) {
            throw new IllegalArgumentException("Unknown material name \"" + this.getMaterial() + "\"");
        }
        final ItemStack itemStack = new ItemStack(material, 1, this.getDamage());
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(this.isUnbreakable());
        if (this.getDisplayName() != null && !this.getDisplayName().isEmpty()) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getDisplayName()));
        }

        //Lore colorizing
        if (this.getLore() != null && !this.getLore().isEmpty()) {
            itemMeta.setLore(this.getLore().stream().map(
                    line -> ChatColor.translateAlternateColorCodes('&', line)
            ).collect(Collectors.toList()));
        }

        //Enchantments processing
        if (this.getEnchantments() != null && !this.getEnchantments().isEmpty()) {
            this.getEnchantments().forEach((name, level) -> {
                final Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                if (e != null) {
                    itemMeta.addEnchant(e, level, true);
                }
            });
        }

        //ItemFlags processing
        if (this.getItemFlags() != null && !this.getItemFlags().isEmpty()) {
            final ArrayList<ItemFlag> itemFlags = new ArrayList<>();
            for (String flag : this.getItemFlags()) {
                try {
                    itemFlags.add(ItemFlag.valueOf(flag));
                } catch (IllegalArgumentException e) {
                    //TODO: Print exception to the logger
                }
            }
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
