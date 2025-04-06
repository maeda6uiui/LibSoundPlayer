# LibSoundPlayer

## Overview

This is a sound player library that can be used in Java.
Core part is written in Rust, using [rodio](https://github.com/RustAudio/rodio).

## Supported platforms

- Linux x64
- Windows x64
- macOS x64 (not tested)

## Installation

### Maven

```xml
<dependency>
    <groupId>com.github.dabasan</groupId>
    <artifactId>lib-sound-player-core</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Code example

Below is a code example for audio playback.

```java
package com.github.maeda6uiui.libsoundplayertest;

import com.github.maeda6uiui.libsoundplayer.core.Sound;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        Sound sound;
        try {
            sound = new Sound("./Data/Op24.flac");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        sound.play();
        while (true) {
            if (sound.isFinished()) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(e);
                return;
            }
        }
    }
}
```
