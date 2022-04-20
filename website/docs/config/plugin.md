---
sidebar_position: 2
---

# Plugin configuration

Plugin configurations are specified in `main.conf`.

## Base configurations {#base-configurations}

### `enabled`

**Type:** [Boolean]

Allows to disable the plugin.

### `locale`

:::caution

This option is not implemented yet.

:::

## Database configuration {#database-configuration}

ECInventory uses database to store inventories content.
Here are described how to configure database.

All plugin-related tables are prefixed with `ecinv_`.

:::tip

Database configs are used only if you have set `type: mysql`.
`sqlite` database does not require any configuration.

:::

### `type`

The driver that will be used for database:

- `sqlite` is a file-based database.
  Used by default.
  It does not require any configuration but can't be shared between servers.
- `mysql` is a standalone database, so it can be shared between servers.
  Requires additional configuration.

### `host`

The host where the database running.
If it is running on the same machine, use `localhost`.

### `port`

The database port.
Default port for MySQL is `3306`.

### `name`

The name of the database.
You can create database using [CREATE DATABASE statement][create-db].

### `username`

The username to connect to the database.

### `password`

The password to connect to the database.

[boolean]: basics.md#boolean
[create-db]: https://dev.mysql.com/doc/refman/5.7/en/create-database.html
