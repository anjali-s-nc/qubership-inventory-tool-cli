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

package org.qubership.itool.cli.spi;

import com.google.inject.Module;
import io.vertx.core.Vertx;

/**
 * Service Provider Interface for contributing a Guice module to the application context.
 *
 * Extension projects can implement this interface to provide a single override module
 * that can replace bindings from the base QubershipModule. Only one extension module
 * is supported - if multiple providers are found, only the first one is used.
 *
 * To register the provider, create a file:
 * {@code META-INF/services/org.qubership.itool.cli.spi.ModuleProvider}
 * containing the fully qualified class name of your implementation.
 */
public interface ModuleProvider {

    /**
     * Create a Guice module to be included in the application context.
     *
     * @param vertx the Vertx instance
     * @return the module to be included, or null if no module should be contributed
     */
    Module createModule(Vertx vertx);

    /**
     * Get a descriptive name for this module provider.
     * Used for logging and debugging purposes.
     *
     * @return a human-readable name for this provider
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}
