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

import org.qubership.itool.cli.spi.CommandProvider;

import java.util.concurrent.Callable;

/**
 * Example extension command provider implementation. This demonstrates how extensions register
 * their commands using the SPI mechanism.
 *
 * <p>
 * In a real extension, this would be registered in:
 * {@code META-INF/services/org.qubership.itool.cli.spi.CommandProvider}
 * </p>
 *
 * <p>
 * This is a test/demo implementation. Real extensions would provide actual functionality.
 * </p>
 */
public class ExampleCommandProvider implements CommandProvider {

    @Override
    public Callable<Integer> createCommand() {
        return new ExampleCommand();
    }

    @Override
    public String getCommandName() {
        return "example-extension";
    }
}
