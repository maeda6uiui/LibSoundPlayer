package com.github.maeda6uiui.libsoundplayer.core;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Sound
 *
 * @author maeda6uiui
 */
public class Sound {
    public static final int SUCCESS = 0;
    public static final int ALREADY_FINISHED = 1;
    public static final int NOT_FOUND = -1;

    private String playerId;

    public Sound(String filepath) throws FileNotFoundException {
        if (!Files.exists(Paths.get(filepath))) {
            throw new FileNotFoundException();
        }

        playerId = ISoundPlayer.INSTANCE.spawn_sound_player_thread(filepath);
    }

    public int play() {
        return ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "play");
    }

    public int stop() {
        return ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "stop");
    }

    public int pause() {
        return ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "pause");
    }
}
