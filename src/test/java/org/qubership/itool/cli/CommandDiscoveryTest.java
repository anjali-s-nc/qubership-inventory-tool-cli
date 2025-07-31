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
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for command discovery using the SPI mechanism.
 */
public class CommandDiscoveryTest {

    @Test
    public void testCommandDiscovery() {
        // Test that ServiceLoader can discover command providers
        ServiceLoader<ExtensionCommandProvider> providers =
                ServiceLoader.load(ExtensionCommandProvider.class);

        assertNotNull(providers);

        // Convert to list for easier testing
        var providerList = StreamSupport.stream(providers.spliterator(), false)
                .collect(Collectors.toList());

        // Should find our core command providers
        assertTrue(providerList.size() >= 6,
                "Should find at least 6 core command providers, found: " + providerList.size());

        // Verify specific commands are present
        var commandNames = providerList.stream()
                .map(ExtensionCommandProvider::getCommandName)
                .collect(Collectors.toSet());

        assertTrue(commandNames.contains("exec"), "Should find exec command");
        assertTrue(commandNames.contains("query"), "Should find query command");
        assertTrue(commandNames.contains("extract"), "Should find extract command");
        assertTrue(commandNames.contains("obfuscate"), "Should find obfuscate command");
        assertTrue(commandNames.contains("ci-exec"), "Should find ci-exec command");
        assertTrue(commandNames.contains("ci-assembly"), "Should find ci-assembly command");

        // Verify all providers can create commands
        for (ExtensionCommandProvider provider : providerList) {
            assertNotNull(provider.getCommandName(), "Command name should not be null");

            Callable<Integer> command = provider.createCommand();
            assertNotNull(command, "Command should not be null");
            assertTrue(command instanceof Callable, "Command should implement Callable<Integer>");
        }
    }
}
