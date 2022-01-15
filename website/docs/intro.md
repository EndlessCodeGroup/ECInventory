---
sidebar_position: 1
---

# Introduction

**ECInventory** is a powerful tool for changing your inventory to your liking.
With it, you can make your server even more atmospheric by adding a lot of new highly customizable inventories.
Thanks to the [Mimic] API, ECInventory can use the capabilities of other plugins (MMOItems, MMOCore, QuantumRPG, SkillAPI, PROSkillAPI, Heroes, etc.).

:::caution

**ECInventory** is under development and it **IS NOT PRODUCTION READY**.
It can't be used as a drop-in replacement of RPGInventory.

If you want to test preview version, follow the ["Getting Started"](usage/getting-started.md) guide.

:::

## Features

- Create multiple inventories for players
- Inventories are stored in SQL database
- Inventories support almost all possible clicks including Shift + Click, hotbar swap and offhand swap
- Bind commands to left, right, shift-left and shift-right clicks
- Define allowed and denied items from others plugins (via [Mimic])
- Define allowed and denied items IDs using wildcard patterns, for example pattern `*_sword` allows to put any sword to the slot
- Placeholders (via [PlaceholderAPI]) can be used in inventory names as well as in slot names, descriptions, and commands
- Admins can open other player's inventories and modify its content

[placeholderapi]: https://www.spigotmc.org/resources/6245/
[mimic]: https://www.spigotmc.org/resources/82515/
