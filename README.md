# PaperMC Multiverse
A plugin/library that allows easy creation of new "universes" each with their own world, nether and end.

## Usage
### Installation
To include the library in your plugin add "https://jitpack.io" as a maven repository and include this repo as a dependency.

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
...

dependencies {
    implementation 'com.github.UnrealCryptoCore:Multiverse:Tag'
}
```

### API
```kotlin
MultiverseAPI.init(this) // call once in onEnable()

// create a new universe or load an existing one
val universe = MultiverseAPI.createOrLoadUniverse("universe1")

// spawn a player in the new universe
universe.spawn(player)

```
