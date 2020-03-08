package me.xnike.jooq.codegen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToolsTest {

    @Test
    public void filePatternBasedir() {
        assertEquals("", Tools.getFilePatternBasedir("a.xml"));
        assertEquals("", Tools.getFilePatternBasedir("*.xml"));
        assertEquals("/", Tools.getFilePatternBasedir("/*.xml"));
        assertEquals("src", Tools.getFilePatternBasedir("src/*.xml"));
        assertEquals("src/a", Tools.getFilePatternBasedir("src/a/*.xml"));
    }

    @Test
    public void filePatternPattern() {
        assertEquals("a.xml", Tools.getFilePatternPattern("a.xml"));
        assertEquals("*.xml", Tools.getFilePatternPattern("*.xml"));
        assertEquals("*.xml", Tools.getFilePatternPattern("/*.xml"));
        assertEquals("*.xml", Tools.getFilePatternPattern("src/*.xml"));
        assertEquals("*.xml", Tools.getFilePatternPattern("src/a/*.xml"));
    }
}
