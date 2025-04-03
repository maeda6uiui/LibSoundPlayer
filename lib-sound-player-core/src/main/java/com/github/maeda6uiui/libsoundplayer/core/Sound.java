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
    private String playerId;

    public Sound(String filepath) throws FileNotFoundException {
        if (!Files.exists(Paths.get(filepath))) {
            throw new FileNotFoundException(filepath);
        }

        playerId = ISoundPlayer.INSTANCE.spawn_sound_player_thread(filepath);
    }

    private void throwExceptionOnError(String resp) {
        if (resp.startsWith("error_")) {
            throw new RuntimeException(resp);
        }
    }

    public void play() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "play");
        this.throwExceptionOnError(resp);
    }

    public void stop() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "stop");
        this.throwExceptionOnError(resp);
    }

    public void pause() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "pause");
        this.throwExceptionOnError(resp);
    }

    public boolean isFinished() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "is_finished");
        this.throwExceptionOnError(resp);

        return resp.equals("true");
    }

    public float getSpeed() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "get_speed");
        this.throwExceptionOnError(resp);

        return Float.parseFloat(resp);
    }

    public float getVolume() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "get_volume");
        this.throwExceptionOnError(resp);

        return Float.parseFloat(resp);
    }

    public void setSpeed(float speed) {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, String.format("set_speed %f", speed));
        this.throwExceptionOnError(resp);
    }

    public void setVolume(float volume) {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, String.format("set_volume %f", volume));
        this.throwExceptionOnError(resp);
    }
}
