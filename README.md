# 🐄 Dispenser Breeding

Automate animal breeding in Minecraft using dispensers.

With this mod installed, dispensers can feed nearby animals automatically using their normal breeding food. No more manual feeding — just hook up some redstone and let it run.

---

## ✨ Features

* 🐑 **Works with all animals**
  Any animal that can normally be bred (cows, sheep, pigs, chickens, etc.) will work automatically.

* 🌾 **Uses vanilla breeding food**
  The mod uses Minecraft’s built-in logic, so animals only accept the correct items (e.g. wheat for cows, carrots for pigs).

* ⚡ **Simple automation**
  Place a dispenser facing animals, add food, power it — done.

* 🖥️ **Server-side mod**
  Only needs to be installed on the server. Clients do not need the mod.

---

## 🧠 How It Works

When a dispenser fires:

* If a nearby animal can be bred using that item → it is fed instantly
* If not → the item is dispensed normally

This means it behaves exactly like vanilla, just smarter.

---

## 📦 Installation

### Fabric (Required)

This mod requires **Fabric Loader** and **Fabric API**.

### Install Steps

1. Install Fabric for your Minecraft version
2. Download:
   * Dispenser Breeding mod [https://modrinth.com/mod/dispenser-breeding](https://modrinth.com/mod/dispenser-breeding)
3. Drop `.jar` files into your `mods` folder
4. Launch the game/server

---

### 🐳 Docker (itzg/minecraft-server)

If you're running a Fabric server via Docker, you can install it like this:

```yaml
- name: VERSION
  value: "26.1.1"
- name: TYPE
  value: "FABRIC"
- name: MODRINTH_PROJECTS
  value: |
    fabric-api
    dispenserbreeding
```

---

## 🔗 Download

👉 [https://modrinth.com/mod/dispenser-breeding](https://modrinth.com/mod/dispenser-breeding)

---

## 🚧 What's Coming

* ⚙️ Config file (JSON) to tweak behaviour
* 🐄 Animals can pick up breeding items from the ground
* 🎯 More control over range and targeting

---

## 💡 Tips

* Keep animals close to the dispenser for best results
* Use redstone clocks for continuous breeding
* Works great with farms and mob pens

---

## ❤️ Credits

Built with Fabric for modern Minecraft.
