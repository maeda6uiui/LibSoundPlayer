package com.github.maeda6uiui.libsoundplayer.core;

import com.github.maeda6uiui.libsoundplayer.natives.INativeExtractor;
import com.github.maeda6uiui.libsoundplayer.natives.NativeExtractorFactory;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Loads native library
 *
 * @author maeda6uiui
 */
public class NativeLoader {
    public static ISoundPlayer load() {
        String platform;
        if (Platform.isWindows()) {
            platform = "windows";
        } else if (Platform.isLinux()) {
            platform = "linux";
        } else {
            throw new RuntimeException("Unsupported platform");
        }

        File libFile;
        try {
            INativeExtractor extractor = NativeExtractorFactory.createNativeExtractor(platform);
            libFile = extractor.extractLibSoundPlayer();
        } catch (
                ClassNotFoundException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | IOException e) {
            throw new RuntimeException(e);
        }

        return Native.load(libFile.getPath(), ISoundPlayer.class);
    }
}
