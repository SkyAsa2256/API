# API 
[![Discord](https://img.shields.io/discord/831966641586831431)](https://discord.gg/7vqgtrjDGw) 
[![Developer Releases](https://jitpack.io/v/EnvyWare/API.svg)](https://jitpack.io/#Pixelmon-Development/API) 
[![GitHub](https://img.shields.io/github/license/EnvyWare/API)](https://www.gnu.org/licenses/lgpl-3.0.html) 

This repository will contain a multi-project Gradle setup for Minecraft APIs. (Spigot, Forge, Sponge, Velocity, Bungee etc.)

Join the discord for further assitance.

# How to use

You will need to add the following to your build.gradle in the repositories section:

```groovy
  maven {
       url "https://maven.envyware.co.uk/releases"
  }
```

Then in the dependencies section you'll need to add

```groovy
  shadow "com.envyful.api:commons:4.0.1"
```

My API is designed to be shaded into the JAR file so if you need more information on how to do that please find it [here](https://imperceptiblethoughts.com/shadow/)

The latest version of my API can be found in my [Discord server](https://discord.envyware.co.uk/) in the #api-changelog channel. This channel is where I post detailed information about what changed between each version
