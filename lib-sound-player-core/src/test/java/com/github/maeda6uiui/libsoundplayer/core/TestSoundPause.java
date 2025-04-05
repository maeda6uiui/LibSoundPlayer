package com.github.maeda6uiui.libsoundplayer.core;

import java.io.FileNotFoundException;

public class TestSoundPause {
    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Sound sound;
        try {
            sound = new Sound("./Data/Op23.mp3");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        sound.play();
        System.out.printf("isPaused: %b\n", sound.isPaused());
        sleep(10000);

        sound.pause();
        System.out.printf("isPaused: %b\n", sound.isPaused());
        sleep(3000);

        sound.play();
        System.out.printf("isPaused: %b\n", sound.isPaused());
        sleep(10000);

        sound.stop();
    }
}
