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

import java.util.concurrent.Callable;

/**
 * Interface for extension command providers. Extensions implement this interface to provide custom
 * commands that will be automatically discovered and registered at runtime.
 *
 * <p>
 * To register an extension command:
 * </p>
 * <ol>
 * <li>Implement this interface</li>
 * <li>Create a file in your extension's resources:
 * {@code META-INF/services/org.qubership.itool.cli.ExtensionCommandProvider}</li>
 * <li>Add the fully qualified class name of your implementation to that file</li>
 * </ol>
 *
 * <p>
 * Example implementation:
 * </p>
 *
 * <pre>{@code
 * public class MyExtensionCommandProvider implements ExtensionCommandProvider {
 *     @Override
 *     public Callable<Integer> createCommand() {
 *         return new MyExtensionCommand();
 *     }
 *
 *     @Override
 *     public String getCommandName() {
 *         return "my-extension";
 *     }
 * }
 * }</pre>
 *
 * <p>
 * Registration file: {@code META-INF/services/org.qubership.itool.cli.ExtensionCommandProvider}
 * </p>
 *
 * <pre>{@code
 * org.qubership.itool.extension.MyExtensionCommandProvider
 * }</pre>
 */
public interface ExtensionCommandProvider {

    /**
     * Creates a new command instance. The returned command should implement
     * {@link Callable<Integer>} and optionally use Picocli annotations for CLI parsing.
     *
     * @return the command instance
     */
    Callable<Integer> createCommand();

    /**
     * Gets the command name. This name will be used as the subcommand name in the CLI.
     *
     * @return the command name
     */
    String getCommandName();
}
