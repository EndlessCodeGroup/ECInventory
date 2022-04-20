---
sidebar_position: 3
---

# Data: Slots

Slots configurations are stored in `.conf` files inside `data` directory.
All slots should be declared inside `slots` block.
Each slot has an ID used as a key on slot declaration.

```yaml
slots {
  [slot-id] {
    // Slot declaration goes here
  }
  // ...
}
```

:::note

`slot-id` should be a unique string.
It is recommended to use kebab-case for all config keys for consistency.

:::

## Slot {#slot}

All slot properties are optional and takes default value if value is not specified. 

### `display-name`

**Type:** [String] supporting colors and placeholders  
**Default value:** `""` (empty string)

The slot display name.

Will be shown only if [`texture`](#texture) specified.

```yaml
display-name: "&6Totem"
```

### `description`

**Type:** list of [Strings][string] supporting colors and placeholders  
**Default value:** `[]` (empty list)

The slot description.
Each line will be displayed on the new line.

Will be shown only if [`texture`](#texture) specified.

```yaml
description: [
  "&aHere you can place the &6Totem of Undying",
  "",
  "&e\"- It saved my life many times.\" (c) Dad"
]
```

### `texture`

**Type:** [Item], nullable  
**Default value:** `null`

The texture will be shown to the player when the slot is empty.
Uses [`display-name`](#display-name) and [`description`](#description).

If `texture` is `null`, none texture will be shown when the slot is empty.

```yaml
texture: minecraft:orange_stained_glass_pane
```

### `type`

**Type:** one of: `generic`, `equipment`, `gui`  
**Default value:** `generic`

The slot type.
Defines slot destination and capabilities.
Some slot properties applicable only to particular slot types.

- `generic` is a basic slot used just to store items.
  Default [`max-stack-size`](#max-stack-size) is `64`.
- `equipment` is a slot that can store equipment.
  Items stored in equipment slots should be counted for player's attributes.
  Default [`max-stack-size`](#max-stack-size) is `1`.
- `gui` is a GUI slot that can not store items.

```yaml
type: generic
```

### `actions`

**Type:** list of [Slot action bindings](#slot-action-binding)  
**Default value:** `[]` (empty list)

Bindings for player's interactions with the slot.

```yaml
actions: [
  {on: [click], do: ["say It will help you when you are unlucky"]}
]
```

### `allowed-items`

**Type:** list of [Item wildcards](#item-wildcards)  
**Default value:** `["*"]` (all items allowed)  
**Slot types:** `generic`, `equipment`

Defines items allowed to be placed to the slot.

```yaml
allowed-items: [totem_of_undying]
```

### `denied-items`

**Type:** list of [Item wildcards](#item-wildcards)  
**Default value:** `[]` (empty list, none items denied)  
**Slot types:** `generic`, `equipment`

Defines items denied to be placed to the slot.

```yaml
denied-items: [custom:totem_of_undying]
```

### `max-stack-size`

**Type:** integer [Number] from range `1..64`   
**Default value:** depends on [slot type](#type)  
**Slot types:** `generic`, `equipment`

The maximal item stack size to be placed to the slot.

```yaml
max-stack-size: 1
```

## Slot action binding {#slot-action-binding}

Binds player's interactions listed in `on` to commands listed in `do`.

### `on` {#action-on}

**Type:** set of values (`click`, `left_click`, `shift_left_click`, `right_click`, `shift_right_click`)

The list of player's interactions that should be initiate actions listed in `do`.
This list should not be empty.

Actions matching rules:

- `click` matches to any click
- `left_click` and `right_click` matches to both clicks with and without <kbd>Shift</kbd>

```yaml
on: [left_click, shift_right_click]
```

### `do` {#action-do}

**Type:** list of [Strings][string] supporting placeholders

The list of commands will be executed when action described in `on` was performed.
Command should not start with slash.
All commands will be executed from the player who interacted with the slot.

```yaml
do: ["gamemode creative"]
```

## Item wildcards {#item-wildcards}

Item wildcards allows you to easier configure allowed and denied items.
Instead of writing each allowed item, you can use wildcard matching to all of them:

```yaml
# Without wildcard
allowed-items: [
  "wooden_sword",
  "stone_sword",
  "iron_sword",
  "golden_sword",
  "diamond_sword",
  "netherite_sword"
]

# With wildcard
allowed-items: ["*_sword"]
```

:::tip

Syntax:

- `*` matches zero or more characters
- `?` matches exactly one character 

:::

[string]: basics.md#string
[number]: basics.md#number
[item]: basics.md#item
