## [Unreleased]

### Dependency updates

| Dependency                  | Old version | New version |
|-----------------------------|-------------|-------------|
| **Java**                    | `11`        | `17`        |
| **Spigot**                  | `1.6.5`     | `1.8`       |
| **CommandAPI**              | `8.5.1`     | `9.2.0`     |
| Kotlin                      | `1.7.10`    | `1.9.20`    |
| kotlinx-serialization-hocon | `1.4.0`     | `1.6.1`     |
| HikariCP                    | `5.0.1`     | `5.1.0`     |

### Housekeeping

- Update Gradle to 8.4

## [v0.1.3] (2022-08-27)

### Added

- [Documentation](https://endlesscodegroup.github.io/ECInventory/docs/intro)
- Support of Mimic PlayerInventory API to integrate with other plugins
- Check minimal required version of Mimic and CommandAPI on plugin enable

### Changed

- Update required CommandAPI to 8.0.0+
- Update required Mimic to 0.8.0+
- Update Kotlin to 1.7.10
- Minimal Java version set to 11

## [v0.1.2] (2022-01-07)

### Hotfix

- Fixed `SQLiteException` on plugin initialization

## [v0.1.1] (2022-01-06)

### Hotfix

- Fix the error `Can not parse version string` on plugin loading

## [v0.1] (2022-01-06)

Happy New Year and Merry Christmas!

This is the first preview release, that includes core mechanics of inventories.

### What's Implemented

- Flexible configs for inventories and slots #1

Inventories:
* Inventory clicks mapping https://github.com/EndlessCodeGroup/ECInventory/pull/11
* Default inventory slot https://github.com/EndlessCodeGroup/ECInventory/pull/34
* Support multiple positions for similar slots https://github.com/EndlessCodeGroup/ECInventory/pull/33
* SQL storage for inventories https://github.com/EndlessCodeGroup/ECInventory/pull/23

Slots:
* Slot content validation https://github.com/EndlessCodeGroup/ECInventory/pull/29
* Slot name and description with placeholders support https://github.com/EndlessCodeGroup/ECInventory/pull/31
* Slot clicks bindings https://github.com/EndlessCodeGroup/ECInventory/pull/35

Commands:
* `/inventories open <type>` - Open inventory
* `/inventories open <type> <target>` - Open other player's inventory https://github.com/EndlessCodeGroup/ECInventory/pull/41
* `/inventories reload` - Reload configs https://github.com/EndlessCodeGroup/ECInventory/issues/40

[Unreleased]: https://github.com/EndlessCodeGroup/ECInventory/compare/v0.1.3...HEAD
[v0.1.3]: https://github.com/EndlessCodeGroup/ECInventory/compare/v0.1.2...v0.1.3
[v0.1.2]: https://github.com/EndlessCodeGroup/ECInventory/compare/v0.1.1...v0.1.2
[v0.1.1]: https://github.com/EndlessCodeGroup/ECInventory/compare/v0.1...v0.1.1
[v0.1]: https://github.com/EndlessCodeGroup/ECInventory/commits/v0.1
