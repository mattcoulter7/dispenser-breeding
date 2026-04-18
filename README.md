# Dispenser Breeding

<p align="center">
  Automated animal breeding for Minecraft Fabric servers.
</p>

<p align="center">
  <a href="https://github.com/mattcoulter7/dispenser-breeding/actions/workflows/build-and-publish.yml">
    <img src="https://github.com/mattcoulter7/dispenser-breeding/actions/workflows/build-and-publish.yml/badge.svg" alt="Build Status">
  </a>
  <a href="https://modrinth.com/mod/dispenser-breeding">
    <img src="https://img.shields.io/modrinth/dt/dispenser-breeding?logo=modrinth&label=Modrinth%20downloads" alt="Modrinth Downloads">
  </a>
  <a href="https://modrinth.com/mod/dispenser-breeding/versions">
    <img src="https://img.shields.io/modrinth/game-versions/dispenser-breeding?label=Minecraft" alt="Minecraft Versions">
  </a>
  <a href="https://modrinth.com/mod/dispenser-breeding/versions">
    <img src="https://img.shields.io/modrinth/loaders/dispenser-breeding?label=Loader" alt="Supported Loaders">
  </a>
</p>

Dispenser Breeding adds simple, server-side breeding automation to Minecraft.

Dispensers can instantly feed nearby animals using their normal breeding food, and animals can also detect valid breeding items dropped on the ground, walk to them, and consume them automatically.

The result is a breeding system that feels vanilla, works naturally in farms, and requires no client-side installation.

---

## Features

- **Works with vanilla breeding rules**  
  Animals only respond to their normal breeding food.

- **Dispenser-based breeding**  
  If a dispenser fires a valid breeding item at a nearby animal, the animal is fed instantly.

- **Ground item breeding**  
  Animals can detect dropped breeding food nearby, walk to it, consume one item, and enter love mode.

- **Supports normal breeding animals**  
  Cows, sheep, pigs, chickens, and other animals that already use vanilla breeding mechanics are supported automatically.

- **Server-side only**  
  The mod only needs to be installed on the server.

- **Configurable**  
  Behaviour such as dispenser range and ground pickup settings can be adjusted through a JSON5 config file.

--

## Example use cases

* fully automated animal breeding pens
* redstone-powered breeding systems
* semi-automatic farms where food is dropped into enclosures
* passive breeding setups without manual player feeding

---

## How it works

### Dispenser feeding

When a dispenser fires:

- if a nearby animal can be bred with that item, it is fed instantly
- otherwise, the item is dispensed normally

### Ground item feeding

Animals can also:

- detect nearby dropped breeding items
- walk towards valid food
- consume a single item from the stack
- enter love mode

This works whether the item was dropped by a dispenser or by a player.

---

## Installation

### Requirements

- **Minecraft:** 26.1.2
- **Loader:** Fabric
- **Dependency:** Fabric API

### Standard installation

1. Install Fabric for your Minecraft version.
2. Download the latest version of **Dispenser Breeding** from Modrinth.  
   https://modrinth.com/mod/dispenser-breeding
3. Place the mod `.jar` into your `mods` folder.
4. Make sure **Fabric API** is also installed.
5. Start the game or server.

---

## Docker installation

If you are running a Fabric server using `itzg/minecraft-server`, you can install the mod through Modrinth like this:

```yaml
- name: VERSION
  value: "26.1"
- name: TYPE
  value: "FABRIC"
- name: MODRINTH_PROJECTS
  value: |
    fabric-api
    dispenserbreeding
````

---

## Configuration

A config file is created automatically on first launch:

```text
config/dispenserbreeding.json5
```

This file can be used to control things such as:

* dispenser breeding range
* enabling or disabling dispenser breeding
* enabling or disabling ground item breeding
* search radius for dropped breeding food
* consume distance
* movement speed and scan interval
---

## Download

* **Modrinth:** [https://modrinth.com/mod/dispenser-breeding](https://modrinth.com/mod/dispenser-breeding)
* **GitHub:** [https://github.com/mattcoulter7/dispenser-breeding](https://github.com/mattcoulter7/dispenser-breeding)

---

## Development status

Current supported releases include:

* 26.1
* 26.1.1
* 26.1.2

The project is built and published through GitHub Actions, with release publishing handled automatically for Modrinth.
