package io.github.anycollect.extensions.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class TestConfigUtils {
    public static List<String> splitFileBySeparator(String filename, String separator) throws IOException {
        File file = FileUtils.getFile("src", "test", "resources", filename);
        String content = FileUtils.readFileToString(file, Charset.defaultCharset());
        return Arrays.asList(content.split(separator));
    }
}
