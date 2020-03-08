package me.xnike.jooq.codegen;

import static java.lang.Math.max;

public class Tools {
    public static String getFilePatternBasedir(String configurationFileName) {
        configurationFileName = configurationFileName.replaceAll("\\\\", "/");
        int firstPatternIndex = max(configurationFileName.indexOf('*'), configurationFileName.indexOf('?'));

        return firstPatternIndex <= 0
                ? ""
                : configurationFileName.lastIndexOf('/', firstPatternIndex) == 0
                ? "/"
                : configurationFileName.substring(0, configurationFileName.lastIndexOf('/', firstPatternIndex));
    }

    public static String getFilePatternPattern(String configurationFileName) {
        configurationFileName = configurationFileName.replaceAll("\\\\", "/");
        int firstPatternIndex = max(configurationFileName.indexOf('*'), configurationFileName.indexOf('?'));

        return firstPatternIndex <= 0
                ? configurationFileName
                : configurationFileName.substring(configurationFileName.lastIndexOf('/', firstPatternIndex) + 1);
    }
}
