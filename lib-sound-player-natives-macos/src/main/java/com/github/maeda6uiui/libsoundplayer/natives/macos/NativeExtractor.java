package com.github.maeda6uiui.libsoundplayer.natives.macos;

import com.github.maeda6uiui.libsoundplayer.natives.INativeExtractor;
import com.github.maeda6uiui.libsoundplayer.natives.NativeExtractorUtils;

import java.io.File;
import java.io.IOException;

/**
 * Extracts native libraries for macOS
 *
 * @author maeda6uiui
 */
public class NativeExtractor implements INativeExtractor {
    @Override
    public File extractLibSoundPlayer() throws IOException {
        return NativeExtractorUtils.extractNativeLibFromJar(this.getClass(), "/Bin/libsoundplayer.dylib");
    }
}
