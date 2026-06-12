<img width="1536" height="355" alt="image" src="https://github.com/user-attachments/assets/8a7e449b-1991-4b13-9891-5bf3ff9097fd" />


# LuaCraft
LuaCraft embeds LuaJ into Paper/Bukkit servers, providing developers with a lightweight, fast scripting layer without the complexity of full plugin development.
<br>
<br>
This is the original maintained repository of LuaCraft
# Requirements
LuaCraft requires Paper to be installed on the server, it does not support Spigot
<br>
<br>
LuaCraft currently supports 1.21.4+ and will intend to support this version and forward. The current projects scope does not intend to support lower versions than this.
<br>
<br>
LuaCraft soft depends on `Vault`
# Download
You can download the latest release of LuaCraft at the [Releases](https://github.com/BorpBorp/LuaCraft/releases)<br>
You can find our Discord at [Discord](https://discord.gg/Nvmajwdfmz)<br>
You can find our Modrinth at [Modrinth](https://modrinth.com/plugin/officialluacraft)
# Example
```lua
function ServerEvent.OnPlayerJoin(event)
   local player = event.Player
   local white = "&#FFFFFF"
   local component = Component(white, "This is my first event!")

   Chat.Broadcast(component)
end
```

# Testing
LuaCraft's simple testing framework implements [edubart/lester](https://github.com/edubart/lester).

## Writing tests
Tests are written in `resources/tests/`.

The methods and configuration provided by *lester* are available under the `Test` global.
Additional globals are provided to the testing environment, such as `debug` and `TestReflect` (described later)

The core functions needed to test are `Test.describe`, `Test.it`, and `Test.expect`; a simple test may look like this:
```lua
local describe, it, expect = lester.describe, lester.it, lester.expect

describe("module", function()
  it("feature", function()
    expect.equal(2 + 2, 4)
  end)
end)
```

### Reflection

Reflection may be useful for writing tests that verify using APIs not directly exposed by LuaCraft.

The global `TestReflect` is only exposed during testing, and allows lookup of java properties via `TestReflect.GetField(obj, fieldName)` and `TestReflect.CallMethod(obj, methodName, ...args)`.

For example:
```lua
//-- Returns an instance of ItemStackLib from LuaCraft
local item = Item.New("stone", 44)
//-- We can get ItemStackLib#stack to get the Bukkit ItemStack
local stack = TestReflect.GetField(item, "stack")
//-- All methods and functions on this ItemStack are available now!
local amount = TestReflect.CallMethod(itemStack, "getAmount")
```

Calling getter methods is recommended over fields, as Bukkit fields may be inconsistent.

## Running the test environment

Running the test environment is as simple as running `./gradlew luaTest`.

A debugger is opened on port 8000, which IntelliJ can attach to via the 'Attach debugger' button in the debug console's output.
