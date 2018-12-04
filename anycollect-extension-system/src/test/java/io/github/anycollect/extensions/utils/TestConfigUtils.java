package io.github.anycollect.extensions.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class TestConfigUtils {
    public static List<String> splitFileBySeparator(String filename, String separator) throws IOException {
        File file = getConfig(filename);
        String content = FileUtils.readFileToString(file, Charset.defaultCharset());
        return Arrays.asList(content.split(separator));
    }

    public static String read(String filename) throws IOException {
        return FileUtils.readFileToString(getConfig(filename), Charset.defaultCharset());
    }

    private static File getConfig(String filename) {
        return FileUtils.getFile("src", "test", "resources", filename);
    }
}
