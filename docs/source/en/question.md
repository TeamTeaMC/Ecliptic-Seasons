## From Serene Seasons to Ecliptic Seasons

### Code Compatibility

This is a rather complicated topic. At one point, I considered implementing a hook-based middleware layer. However, I
later realized that some existing mods might already be compatible with Ecliptic Seasons, and introducing such a layer
could actually create new issues.

In practice, occasional differences at the code level are not a major problem. Therefore, I ultimately chose to provide
a separate compatibility mod:
Ecliptic Seasons: MultiMod Patch  
https://legacy.curseforge.com/minecraft/mc-mods/ecliptic-seasons-multimod-patch

This mod proactively supplies compatibility fixes to address the most common and fundamental issues.

### Tag Compatibility

This part is much more straightforward. You can refer to the section on Seasonal Crops and Humidity Conditions in the
Datapack Basics chapter.

If a mod already provides Serene Seasons tags, seasonal behavior will work automatically. However, if you are a modpack
author, it is still recommended to use Ecliptic Seasons’ own season tags for better compatibility. This is because, in
some cases, only Ecliptic Seasons tags are queried for condition checks.

Regardless of the approach, humidity conditions must always be defined manually. Fortunately, this is usually very
simple to do.

## Detect Problem

### Detect Server Freeze Issues

1. **Open Command Prompt (CMD)**

2. **List Java processes**
   Run the command:

   ```
   jps -l
   ```

   Find the line showing Minecraft; the number on the left is the PID (Process ID).

3. **Generate thread dump**
   Run the command:

   ```
   jstack <PID> > a.log
   ```

   Example:

   ```
   jstack 42928 > a.log
   ```

   This creates a file named `a.log` in the current directory.

4. **(Optional) Specify output file path or name**
   Example:

   ```
   jstack 42928 > D:\logs\mc_thread_dump.txt
   ```

5. **If you get the error `'jstack' is not recognized as an internal or external command`**

    * Locate your Java installation path, e.g.:

      ```
      C:\Program Files\Java\jdk-17\bin
      ```

      ```
      cd "C:\Program Files\Java\jdk-17\bin"
      ```
    * Run the `jstack` command again.

## Compatibility

### Does a certain crop grow based on seasons and humidity? / Does it support seasonal crops from specific mods?

Actually, it’s better to ask the other way around. Ecliptic Seasons doesn’t natively support specific crops
or blocks, because when all mods are combined, there can be tens of thousands of blocks. However, you can manually add
compatibility using tags.

If the mods you're using already support Serene Seasons and you haven’t disabled the compatibility settings, then
seasonal crop behavior will be enabled.

As for humidity, that needs to be configured separately.

### Does this mod work with Serene Seasons？

As a season mod, Ecliptic Seasons shares many similarities with Serene Seasons — both represent the Minecraft year using
four seasons, change grass and foliage colors, modify rain and snow effects, and control seasonal crop growth. However,
Minecraft has been around for 15 years, and we aim to push things further.

Also, you might try running both mods together and notice crashes during rain, then assume it’s caused by the new mod.
In reality, the problem comes from Serene Seasons’ use of a compatibility-unfriendly @Redirect mixin to cache data,
instead of capturing it with local variables (which was also possible back in the older Mixin days). This design makes
their code quite fragile.

Sometimes crashes don’t happen because you have mods like Pretty Rain installed. Those mods add separate rendering logic
that bypasses the risky Serene Seasons code, preventing crashes.

## Commands

### Season

You can set the current day count and solar term in the seasonal system using a command like:

```
/ecliptic solar set 99
```

Alternatively, if you remember the solar term names, you can use:

```
/ecliptic solar setTerm beginning_of_autumn
```

### Weather

You can use commands like:

```
/weather rain
```

However, this is an Ecliptic Seasons compatibility behavior that forces all biomes to change their weather state.

It is better to use:

```
/ecliptic weather <biome> rain
```

where `<biome>` can be a biome tag or biome ID.

### Time

Ecliptic Seasons overrides the vanilla command, so commands like:

```
/time set night
```

will change dynamically according to the season.
