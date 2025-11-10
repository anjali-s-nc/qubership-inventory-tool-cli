/*
 * Copyright 2024-2025 NetCracker Technology Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qubership.itool.cli;

import org.junit.jupiter.api.Test;
import org.qubership.itool.cli.spi.CommandProvider;

import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for the ExtensionCommandProvider SPI mechanism.
 */
public class CommandProviderTest {

    @Test
    public void testExtensionCommandProviderInterface() {
        // Test that the interface can be implemented
        CommandProvider provider = new TestExtensionCommandProvider();

        assertNotNull(provider);
        assertEquals("test-command", provider.getCommandName());

        Callable<Integer> command = provider.createCommand();
        assertNotNull(command);
    }

    @Test
    public void testServiceLoaderDiscovery() {
        // Test that ServiceLoader can discover command providers
        ServiceLoader<CommandProvider> providers =
                ServiceLoader.load(CommandProvider.class);

        assertNotNull(providers);

        // Count discovered providers (should be 6 core providers in test environment)
        int providerCount = 0;
        for (CommandProvider provider : providers) {
            providerCount++;
            assertNotNull(provider);
            assertNotNull(provider.getCommandName());
            assertNotNull(provider.createCommand());
        }

        // In test environment, we should find 6 core command providers
        assertEquals(6, providerCount,
                "Should find 6 core command providers in test environment, found: " + providerCount);
    }

    @Test
    public void testCoreCommandsArePresent() {
        // Test that all core commands are discovered
        ServiceLoader<CommandProvider> providers =
                ServiceLoader.load(CommandProvider.class);

        assertNotNull(providers);

        // Collect all command names
        java.util.Set<String> commandNames = new java.util.HashSet<>();
        for (CommandProvider provider : providers) {
            commandNames.add(provider.getCommandName());
        }

        // Verify all core commands are present
        assertTrue(commandNames.contains("exec"), "Should find exec command");
        assertTrue(commandNames.contains("query"), "Should find query command");
        assertTrue(commandNames.contains("extract"), "Should find extract command");
        assertTrue(commandNames.contains("obfuscate"), "Should find obfuscate command");
        assertTrue(commandNames.contains("ci-exec"), "Should find ci-exec command");
        assertTrue(commandNames.contains("ci-assembly"), "Should find ci-assembly command");

        assertEquals(6, commandNames.size(),
                "Should find exactly 6 core commands, found: " + commandNames.size());
    }

    /**
     * Test implementation of ExtensionCommandProvider for testing purposes.
     */
    private static class TestExtensionCommandProvider implements CommandProvider {

        @Override
        public Callable<Integer> createCommand() {
            return new TestCommand();
        }

        @Override
        public String getCommandName() {
            return "test-command";
        }
    }

    /**
     * Test command implementation for testing purposes.
     */
    private static class TestCommand implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            return 0;
        }
    }
}
