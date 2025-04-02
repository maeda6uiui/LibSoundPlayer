package com.github.maeda6uiui.libsoundplayer.core;

import java.io.FileNotFoundException;

public class TestSound {
    public static void main(String[] args) {
        Sound sound;
        try {
            sound = new Sound("./Data/Op23.mp3");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        sound.play();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
