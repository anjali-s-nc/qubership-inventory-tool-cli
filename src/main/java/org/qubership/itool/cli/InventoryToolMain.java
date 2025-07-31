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

import org.qubership.itool.cli.ExtensionCommandProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.ServiceLoader;

/**
 * Main entry point for the inventory tool CLI application using Picocli.
 *
 * This class provides the main CLI interface with all available commands.
 * Commands are registered as subcommands and can be discovered dynamically.
 */
@Command(
    name = "inventory-tool",
    description = "Inventory tool CLI application",
    mixinStandardHelpOptions = true,
    version = "4.0.1"
)
public class InventoryToolMain implements java.util.concurrent.Callable<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryToolMain.class);

    public static void main(String[] args) {
        LOGGER.info("=== Inventory Tool CLI (Picocli) ===");

        InventoryToolMain app = new InventoryToolMain();
        CommandLine commandLine = new CommandLine(app);

        // Discover and register all commands (core + extensions)
        discoverAllCommands(commandLine);

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // This is called when no subcommand is specified
        LOGGER.info("Available commands: exec, query, ci-exec, ci-assembly, obfuscate, extract");
        LOGGER.info("Use --help to see available options and commands");
        return 0;
    }

    /**
     * Discovers and registers all commands (core + extensions) using ServiceLoader.
     *
     * @param commandLine the CommandLine instance to add subcommands to
     */
    private static void discoverAllCommands(CommandLine commandLine) {
        LOGGER.debug("Discovering all commands...");

        try {
            ServiceLoader<ExtensionCommandProvider> commandProviders =
                    ServiceLoader.load(ExtensionCommandProvider.class);

            int commandCount = 0;
            for (ExtensionCommandProvider provider : commandProviders) {
                try {
                    String commandName = provider.getCommandName();
                    Object command = provider.createCommand();

                    LOGGER.debug("Found command: {}", commandName);

                    // Add the command to Picocli CommandLine
                    commandLine.addSubcommand(commandName, command);

                    commandCount++;
                } catch (Exception e) {
                    LOGGER.warn("Failed to load command from provider: {}",
                            provider.getClass().getName(), e);
                }
            }

            if (commandCount > 0) {
                LOGGER.info("Loaded {} command(s)", commandCount);
            } else {
                LOGGER.warn("No commands found - check service registration");
            }

        } catch (Exception e) {
            LOGGER.warn("Failed to discover commands", e);
        }
    }

    /**
     * Gets the list of available commands for help display by discovering them dynamically.
     *
     * @return array of available command names
     */
    private static String[] getAvailableCommands() {
        try {
            ServiceLoader<ExtensionCommandProvider> commandProviders =
                    ServiceLoader.load(ExtensionCommandProvider.class);

            return commandProviders.stream()
                    .map(provider -> provider.get().getCommandName())
                    .toArray(String[]::new);
        } catch (Exception e) {
            LOGGER.warn("Failed to discover available commands", e);
            return new String[0];
        }
    }
}
