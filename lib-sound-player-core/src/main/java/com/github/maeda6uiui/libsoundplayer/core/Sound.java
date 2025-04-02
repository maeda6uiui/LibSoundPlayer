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
    public enum CommandResponse {
        SUCCESS,
        ERROR_NO_PLAYER_FOUND,
        ERROR_SEND_COMMAND,
        ERROR_RECEIVE_RESPONSE,
        RESP_TRUE,
        RESP_FALSE
    }

    private String playerId;

    public Sound(String filepath) throws FileNotFoundException {
        if (!Files.exists(Paths.get(filepath))) {
            throw new FileNotFoundException();
        }

        playerId = ISoundPlayer.INSTANCE.spawn_sound_player_thread(filepath);
    }

    private CommandResponse parseCommandResponse(String resp) {
        return switch (resp) {
            case "error_no_player_found" -> CommandResponse.ERROR_NO_PLAYER_FOUND;
            case "error_send_command" -> CommandResponse.ERROR_SEND_COMMAND;
            case "error_receive_response" -> CommandResponse.ERROR_RECEIVE_RESPONSE;
            case "true" -> CommandResponse.RESP_TRUE;
            case "false" -> CommandResponse.RESP_FALSE;
            default -> CommandResponse.SUCCESS;
        };
    }

    public CommandResponse play() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "play");
        return this.parseCommandResponse(resp);
    }

    public CommandResponse stop() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "stop");
        return this.parseCommandResponse(resp);
    }

    public CommandResponse pause() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "pause");
        return this.parseCommandResponse(resp);
    }

    public CommandResponse is_finished() {
        String resp = ISoundPlayer.INSTANCE.send_command_to_sound_player(playerId, "is_finished");
        return this.parseCommandResponse(resp);
    }
}
