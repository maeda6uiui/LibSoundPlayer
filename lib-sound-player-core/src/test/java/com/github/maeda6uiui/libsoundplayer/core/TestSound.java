package com.github.maeda6uiui.libsoundplayer.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class TestSound {
    private Sound sound;

    @BeforeEach
    public void loadSound() {
        assertDoesNotThrow(() -> {
            sound = new Sound("../Data/Op23.mp3");
        });
    }

    @Test
    public void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            sound = new Sound("example.mp3");
        });
    }

    @Test
    public void testGetSpeed() {
        assertEquals(1.0f, sound.getSpeed());
        sound.setSpeed(1.5f);
        assertEquals(1.5f, sound.getSpeed());
    }

    @Test
    public void testGetVolume() {
        assertEquals(1.0f, sound.getVolume());
        sound.setVolume(1.5f);
        assertEquals(1.5f, sound.getVolume());
    }

    @Test
    public void testGetPos() {
        assertEquals(0, sound.getPos());
    }
}
