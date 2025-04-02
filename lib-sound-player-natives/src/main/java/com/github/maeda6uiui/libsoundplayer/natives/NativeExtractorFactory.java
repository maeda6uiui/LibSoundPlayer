package com.github.maeda6uiui.libsoundplayer.natives;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory for native extractor
 *
 * @author maeda6uiui
 */
public class NativeExtractorFactory {
    private static final String NATIVE_EXTRACTOR_CLASS_NAME = "NativeExtractor";
    private static final String WINDOWS_PACKAGE_PATH = "com.github.maeda6uiui.libsoundplayer.natives.windows";
    private static final String LINUX_PACKAGE_PATH = "com.github.maeda6uiui.libsoundplayer.natives.linux";

    public static INativeExtractor createNativeExtractor(String platform)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        String className = switch (platform) {
            case "windows" -> WINDOWS_PACKAGE_PATH + "." + NATIVE_EXTRACTOR_CLASS_NAME;
            case "linux" -> LINUX_PACKAGE_PATH + "." + NATIVE_EXTRACTOR_CLASS_NAME;
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        };

        Class<?> clazz = Class.forName(className);
        return (INativeExtractor) clazz.getDeclaredConstructor().newInstance();
    }
}
