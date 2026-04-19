<p align="center"> <a href="https://youtu.be/XmVLSE-e-2Q"> <img src="https://img.youtube.com/vi/XmVLSE-e-2Q/maxresdefault.jpg" alt="Watch the video" width="600"> </a> </p>

<p align="center">
  Automate Animal breeding via dispensers.
</p>

<p align="center">
  <!-- Build -->
  <a href="https://github.com/mattcoulter7/dispenser-breeding/actions/workflows/build-and-publish.yml">
    <img src="https://github.com/mattcoulter7/dispenser-breeding/actions/workflows/build-and-publish.yml/badge.svg" alt="Build Status">
  </a>

  <!-- Modrinth -->
  <a href="https://modrinth.com/mod/dispenser-breeding">
    <img src="https://img.shields.io/badge/Modrinth-Download-1bd96a?logo=modrinth&logoColor=white" alt="Modrinth">
  </a>

  <!-- GitHub Releases -->
  <a href="https://github.com/mattcoulter7/dispenser-breeding/releases">
    <img src="https://img.shields.io/github/v/release/mattcoulter7/dispenser-breeding?label=release&color=blue" alt="GitHub Release">
  </a>

  <!-- Fabric -->
  <a href="https://fabricmc.net/use/server/">
    <img src="https://img.shields.io/badge/Loader-Fabric-blueviolet" alt="Fabric">
  </a>

  <!-- Minecraft -->
  <a href="https://www.minecraft.net/en-us">
    <img src="https://img.shields.io/badge/Minecraft-26.1-62B47A?logo=minecraft&logoColor=white" alt="Minecraft Version">
  </a>
</p>

---

# 🐄 Dispenser Breeding

Dispenser Breeding adds simple, server-side breeding automation to Minecraft.

Dispensers can instantly feed nearby animals using their normal breeding food, and animals can also detect valid breeding items dropped on the ground, walk to them, and consume them automatically.

The result is a breeding system that feels vanilla, works naturally in farms, and requires no client-side installation.

---

## 🧠 Example use cases

- 🏭 Fully automated animal breeding pens  
- ⚡ Redstone-powered breeding systems  
- 🌾 Semi-automatic farms where food is dropped into enclosures  
- 🐑 Passive breeding setups without manual feeding  

---

## ✨ Features

- 🐑 **Works with vanilla breeding rules**  
  Animals only respond to their normal breeding food.

- ⚡ **Dispenser-based breeding**  
  If a dispenser fires a valid breeding item at a nearby animal, the animal is fed instantly.

- 🌾 **Ground item breeding**  
  Animals detect dropped breeding food, walk to it, consume one item, and enter love mode.

- 🐄 **Supports all standard animals**  
  Cows, sheep, pigs, chickens, and more — no setup required.

- 🖥️ **Server-side only**  
  No client install needed.

- ⚙️ **Configurable**  
  Behaviour can be tuned via a JSON5 config file.

---

## 🔧 How it works

### Dispenser feeding

When a dispenser fires:

- If a nearby animal can be bred → it is fed instantly  
- Otherwise → the item is dispensed normally  

---

### Ground item feeding

Animals will:

- Detect nearby breeding items  
- Walk towards valid food  
- Consume **one item** from the stack  
- Enter love mode ❤️  

Works with:
- Dispenser-dropped items  
- Player-dropped items  

---

## 📦 Installation

### Requirements

- **Minecraft:** 26.1  
- **Loader:** Fabric  
- **Dependency:** Fabric API  

### Standard installation

1. Install Fabric  
2. Download the mod → https://modrinth.com/mod/dispenser-breeding  
3. Drop the `.jar` into your `mods` folder  
4. Ensure Fabric API is installed  
5. Start your game/server  

### 🐳 Docker (itzg/minecraft-server)

```yaml
- name: VERSION
  value: "26.1.2"
- name: TYPE
  value: "FABRIC"
- name: MODRINTH_PROJECTS
  value: |
    fabric-api
    dispenserbreeding
```

---

## ⚙️ Configuration

A config file is generated automatically:

```
config/dispenserbreeding.json5
```

You can tweak:

* 🔭 Dispenser range
* 🐄 Enable/disable breeding features
* 🌾 Ground item detection radius
* ⚡ Movement speed & scan frequency

---

## 📥 Download

* Modrinth → [https://modrinth.com/mod/dispenser-breeding](https://modrinth.com/mod/dispenser-breeding)
* GitHub → [https://github.com/mattcoulter7/dispenser-breeding](https://github.com/mattcoulter7/dispenser-breeding)

---

## 🚧 Development status

Supported versions:

* 26.1
* 26.1.1
* 26.1.2

Built and published automatically via GitHub Actions.

Release branches follow:

`release/<minecraft-version>`

Examples:
- `release/26.1.0`
- `release/26.1.1`
- `release/26.1.2`

Release tags follow:

`<mc-version>-<mod-version>-<loaders>`

Examples:
- `26.1.0-1.0.8-fabric`
- `26.1.0-1.0.8-fabric+forge+neoforge+quilt`

---

## ❤️ Credits

Built with Fabric and designed to feel as close to vanilla as possible.
