package com.github.maeda6uiui.libsoundplayer.core;

import java.io.FileNotFoundException;

public class TestSoundPlayback {
    public static void main(String[] args) {
        Sound sound;
        try {
            sound = new Sound("./Data/Op23.mp3");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }

        sound.play();

        while (true) {
            if (sound.isFinished()) {
                break;
            }
            System.out.printf("%f s\n", sound.getPos() / 1000.0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(e);
                break;
            }
        }
    }
}
