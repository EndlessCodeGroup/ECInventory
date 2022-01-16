---
sidebar_position: 1
---

# Configuration Basics

ECInventory uses [HOCON] format for configuration files.
Configuration files use the file extension `.conf`.

## HOCON basics

HOCON (Human-Optimized Config Object Notation) is a human-friendly configuration format, and a superset of JSON.

:::tip

This is a brief HOCON format description targeted on users who already familiar with YAML.
If you want to know all about HOCON, [read the specification][hocon].

:::

Properties in HOCON has `key` and `value` separated by `key-value separator`:

- a `key` is a string describing `value` destination
- a `value` may be string, number, object, boolean, enumeration type or `null`
- a `key-value separator` separates key and value, should be either `:` (YAML-like), or `=` (JSON-like)

If line in config starts with `//` or `#`, it is considered a comment.

```yaml
# This is a comment. It can contain additional information about property.
# Here "display-name" is a key, ":" is a key-value separator and "My beautiful inventory" is a value.
display-name: "My beautiful inventory"
```

HOCON config may be very similar to YAML, but it has significant differences in objects and lists declaration.

### Objects

YAML uses indentation for object declaration, but HOCON uses curly braces `{}`:

```yaml
## YAML
my-slot:
  display-name: "Custom slot"
  type: storage

## HOCON
my-slot {
  display-name: "Custom slot"
  type: storage
}
# HOCON also supports one-line object declaration
my-slot { display-name: "Custom slot", type: storage }
```

### Arrays and lists

YAML has two notations to declare list elements — using square braces `[]` or using hyphen `-` at the line start.
HOCON supports only "square braces" style:

```yaml
## YAML
description:
  - "First line"  
  - "Second line"
# One-line list declaration also supported
description: ["First line", "Second line"]

## HOCON
description: [
  "First line",
  "Second line"
]
# One-line list declaration looks exactly like in YAML
description: ["First line", "Second line"]
```

### List of objects

YAML allows declaring list of objects using hyphen list notation, but in HOCON we should use curly braces to declare objects in list:

```yaml
## YAML
actions:
  - on: [right_click]
    do: ["weather clear"]

## HOCON
actions: [
  {
      on: [right_click]
      do: ["weather clear"]
  }
]
# Or more compact variant using one-line object declaration
actions: [
  {on: [right_click], do: ["weather_clear"]}
]
```

## Types

Here are listed common types used in configs.
Other types are described in the place of usage.

### String

Strings may be quoted and unquoted.
It is recommended to always use quoted strings because unquoted strings has limited set of characters they can contain.

```yaml
quoted-string: "This is a string value"
unquoted-string: This also allowed but not recommended
```

### Number

Numbers may be either integer, or with floating point.
Allowed range usually specified in field specification.

```yaml
integer-value: 42
float-value: 0.5
```

### Boolean

Primitive logical type that can have only the values `true` (aliases: `yes`, `on`) or `false` (aliases: `no`, `off`).

```yaml
boolean-field: true
```

### Item

Item ID that can be used to obtain item via [Mimic].
You can add namespace if you want to get item from the defined source.

```yaml
minecraft-item: minecraft:iron_sword
custom-item: mmoitems:iron_sword
item-without-namespace: iron_sword
```

:::tip

You can check list of available items using command:

```
/mimic item give <player> <item_id>
```

:::

## Advanced HOCON

HOCON provides features good to know because it may be useful when you configure the plugin.

### Path as key

You can use paths as a keys for values to configure nested objects:

```yaml
my-slot {
  display-name: "Change my type"
}

# HOCON will go to the "my-slot" and change its property called "type"
my-slot.type: generic

# This is also a valid notation to declare "other-slot" object
other-slot.display-name: "Slot created by path keys"
other-slot.type: generic
```

### Substitutions

HOCON allows referring from value to other paths in configuration.
Referent path should be in format `${absolute.path.to.field}`.

For example, you can create a variable and reuse it in several values:

```yaml
server-name: "Best Server"

server-info-slot {
  display-name: ${server-name} info
  description: ["Server name is "${server-name}]
  type: gui
}
```

:::tip

Substitutions are not allowed inside quotes `"`.
So you should move substitution out of the quotes the following way:

```yaml
# Bad
"Server name is ${server-name}!"

# Good
"Server name is "${server-name}"!"
```

:::

### Concatenation and inheritance

HOCON allows concatenating values including objects and lists.
It is a powerful feature in combination with [substitutions](#substitutions).

Lists concatenation can be used to share common configurations:

```yaml
offhand {
  allowed-items: [dagger, shield]
}

# We can hold in main hand everything we can hold in offhand and sword or axe additionally
mainhand {
  allowed-items: ${offhand.allowed-items} [sword, axe]
}
```

Objects concatenation can be used for inheritance:

```yaml
ammo-base {
  type: generic
  max-stack-size: 16
}

# Arrows slot will inherit all fields from "ammo-base"
arrows: ${ammo-base} {
  allowed-items: [arrow]
}
```

:::tip

Remember you should use absolute path to objects for substitution.
In real configs paths will look like `slots.ammo-base` instead of `ammo-base`.

:::

[hocon]: https://github.com/lightbend/config/blob/main/HOCON.md
[mimic]: https://www.spigotmc.org/resources/82515/
