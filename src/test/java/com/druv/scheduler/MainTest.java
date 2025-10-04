
package com.druv.scheduler;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class MainTest {
    @Test
    void testMainPrintsHelloScheduler() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            Main.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }
        String output = outContent.toString().trim();
        assertEquals("Hello, Scheduler 🚀", output);
    }
}
