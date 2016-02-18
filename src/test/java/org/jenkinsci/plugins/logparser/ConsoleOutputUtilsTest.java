package org.jenkinsci.plugins.logparser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import hudson.plugins.logparser.ConsoleOutputUtils;

public class ConsoleOutputUtilsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * test whether section is extracted correctly from Jenkins console output
     * 
     * @throws IOException
     */
    @Test
    public void testExtractSections1() throws IOException {
        File file = folder.newFile();

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("[WARNING] 1");
            pw.println("[WARNING]");
            pw.println("[INFO] 2");
            pw.println("[WARNING] 3");
        }

        Map<String, List<String>> sections = ConsoleOutputUtils.extractSections(file);

        assertEquals(3, sections.size());
        assertEquals(Arrays.asList("[WARNING] 1", "[WARNING]", "[WARNING] 3"),
                sections.get("WARNING"));
        assertEquals(Arrays.asList("[INFO] 2"), sections.get("INFO"));
        assertEquals(0, sections.get("ERROR").size());
    }

    /**
     * test whether section is extracted correctly from Jenkins console output
     * 
     * @throws IOException
     */
    @Test
    public void testExtractSections2() throws IOException {
        File file = folder.newFile();

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("[WARNING] 1");
            pw.println("[WARNING]");
            pw.println("[INFO] 2");
            pw.println("[WARNING] 3");
            pw.println("[ERROR] 4");
        }

        Map<String, List<String>> sections = ConsoleOutputUtils.extractSections(file);

        assertEquals(3, sections.size());
        assertEquals(Arrays.asList("[WARNING] 1", "[WARNING]", "[WARNING] 3"),
                sections.get("WARNING"));
        assertEquals(Arrays.asList("[INFO] 2"), sections.get("INFO"));
        assertEquals(Arrays.asList("[ERROR] 4"), sections.get("ERROR"));
    }

}
