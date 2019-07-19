# IslandFly

Add-on for BentoBox to allow players of Gamemode Addons to fly on their island.

## How to use

1. Place the .jar in the addons folder of the BentoBox plugin
2. Restart the server
3. The addon will create a data folder and inside the folder will be a config.yml
4. Edit the config.yml if required
5. Restart the server if you make a change

## Config.yml

There are only two options in the config:

**fly-timeout**
How many seconds the addon will wait before disabling fly mode when a player exit his island.

**logout-disable-fly**
If the fly mode should be disabled when a player disconnect.

## Commands
**/is fly** - This command toggles flight **On** and **Off** 

## Permissions
**[gamemode].island.fly** - **/is fly** 

Example: 
    **bskyblock.island.fly**

**[gamemode].island.flybypass** - **Enables user to use fly command on other islands too**


Example:
**caveblock.island.flybypass**
  



