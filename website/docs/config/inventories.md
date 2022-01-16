---
sidebar_position: 4
---

# Data: Inventories

Inventories configurations are stored in `.conf` files inside `data` directory.
All inventories should be declared inside `inventories` block.
Each inventory has an ID used as a key on inventory declaration.

```yaml
inventories {
  [inventory-id] {
    // Inventory declaration goes here
  }
  // ...
}
```

:::note

`inventory-id` should be a unique string.
It is recommended to use kebab-case for all config keys for consistency.

:::

## Inventory

### `display-name`

**Type:** [String], supporting colors and placeholders

The inventory name displayed to the player when inventory is open.

```yaml
display-name: "&6Equipment"
```

### `default-slot`

**Type:** ID of the [slot](slots.md)

The slot that will be used for all not bound inventory slots.

```yaml
# Slot with ID "empty" should be declared in "slots" block
default-slot: empty
```

:::tip

Use `gui` slot to deny all interactions with not bound slots.
If you want to allow store items in unbound slots use `generic` slot as a default.

:::

### `slots`

**Type:** object where keys are slot positions and values are slot IDs should be bound to that position

- `key` is a slot position.
  You can also specify positions range if you want to bind similar slot to multiple positions.
  Range format is `[start]-[end]`, for example [0-9].
- `value` is a slot ID.

```yaml
slots {
  0-8: free-slot
  9-17: premium-slot
  18: previous-page
  26: next-page
}
```

:::info

Slot position is an integer number in range `0..53`.
`0` is the left top position and `53` is the bottom right position.
 
![Slots positions in large chest](/img/large-chest.png)

:::

### `rows`

**Type:** integer [Number] in range `1..6`  
**Default value:** minimal number of lines enough to hold specified `slots`

The number of lines in inventory.
Each line contains 9 slots, maximal possible inventory size is `54` (6 lines).

[string]: basics.md#string
[number]: basics.md#number
