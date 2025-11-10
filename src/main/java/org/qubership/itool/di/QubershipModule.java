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

package org.qubership.itool.di;

import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;

/**
 * Base module for Qubership inventory tool that can be extended by users.
 * This module combines the core functionality with any user-provided customizations.
 */
public class QubershipModule extends AbstractModule {

    private final Vertx vertx;

    /**
     * Creates a new QubershipModule with default core functionality.
     */
    public QubershipModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        // Install core functionality
        install(new CoreModule(vertx));

        // Install merger functionality
        install(new MergerModule());

        // Install CLI functionality
        install(new CliModule());
    }

}
