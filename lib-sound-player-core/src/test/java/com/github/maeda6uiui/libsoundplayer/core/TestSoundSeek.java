package com.github.maeda6uiui.libsoundplayer.core;

import java.io.FileNotFoundException;

public class TestSoundSeek {
    public static void main(String[] args) {
        Sound sound;
        try {
            sound = new Sound("./Data/Op23.mp3");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        sound.seek(10000);
        System.out.printf("Current position: %f s\n", sound.getPos() / 1000.0);

        sound.play();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
