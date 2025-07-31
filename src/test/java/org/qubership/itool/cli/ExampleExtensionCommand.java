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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Example extension command implementation. This demonstrates how extensions can provide custom
 * commands using the SPI mechanism.
 *
 * <p>
 * This is a test/demo implementation. Real extensions would implement actual functionality.
 * </p>
 */
public class ExampleExtensionCommand implements Callable<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleExtensionCommand.class);

    private String message = "Hello from extension!";

    @Override
    public Integer call() throws Exception {
        LOGGER.info("=== Example Extension Command ===");
        LOGGER.info("Message: {}", message);
        LOGGER.info("This demonstrates the SPI extension mechanism");
        LOGGER.info("Extension commands can implement any functionality");
        return 0;
    }

    /**
     * Sets the message to display. In a real Picocli implementation, this would be an @Option
     * parameter.
     *
     * @param message the message to display
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the current message.
     *
     * @return the current message
     */
    public String getMessage() {
        return message;
    }
}
