package com.github.maeda6uiui.libsoundplayer.core;

import com.sun.jna.Library;

/**
 * Interface to native sound player
 *
 * @author maeda6uiui
 */
public interface ISoundPlayer extends Library {
    ISoundPlayer INSTANCE = NativeLoader.load();

    String spawn_sound_player_thread(String input_filepath);

    String send_command_to_sound_player(String id, String command);
}
