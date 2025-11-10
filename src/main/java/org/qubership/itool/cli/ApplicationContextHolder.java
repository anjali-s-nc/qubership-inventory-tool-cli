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

import com.google.inject.Module;
import com.google.inject.util.Modules;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VerticleFactory;
import org.qubership.itool.cli.spi.ModuleProvider;
import org.qubership.itool.context.FlowContext;
import org.qubership.itool.di.ApplicationContext;
import org.qubership.itool.di.QubershipModule;
import org.qubership.itool.factories.JavaAppContextVerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Service locator and manager for ApplicationContext to avoid circular dependencies. This class
 * handles the complete lifecycle of the application context including initialization, management,
 * and cleanup.
 */
public class ApplicationContextHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextHolder.class);

    private static volatile ApplicationContext instance;
    private static volatile FlowContext flowContext;

    /**
     * Initialize application context with specific configuration. Uses SPI-based module discovery
     * for centralized extension management.
     *
     * @param vertx the Vertx instance
     * @param config the configuration to use
     */
    public static void initializeWithConfig(Vertx vertx, JsonObject config) {
        if (instance != null) {
            // Clean up existing context if any
            clear();
        }

        instance = new ApplicationContext(vertx, config, createModules(vertx));
        flowContext = instance.getInstance(FlowContext.class);

        // Initialize verticle factories
        initializeVerticleFactories(vertx, flowContext, config);

        LOGGER.debug("Application context initialized with command-specific config");
    }

    /**
     * Get the shared application context.
     *
     * @return the shared application context, or null if not set
     */
    public static ApplicationContext getInstance() {
        return instance;
    }

    /**
     * Get the shared flow context.
     *
     * @return the shared flow context, or null if not set
     */
    public static FlowContext getFlowContext() {
        return flowContext;
    }

    /**
     * Clear the shared application context. Should be called during cleanup.
     */
    public static void clear() {
        instance = null;
        flowContext = null;
    }

    /**
     * Create modules for dependency injection. Uses SPI to discover a single extension module for
     * centralized extension management.
     *
     * @param vertx The Vertx instance
     * @return Array of modules to use for dependency injection
     */
    private static Module[] createModules(Vertx vertx) {
        Module baseModule = new QubershipModule(vertx);

        // Discover extension module via SPI (expect only one)
        Module extensionModule = discoverExtensionModule(vertx);

        if (extensionModule != null) {
            // Base module + extension override
            return new Module[] {Modules.override(baseModule).with(extensionModule)};
        } else {
            // Only base module
            return new Module[] {baseModule};
        }
    }

    /**
     * Discover a single extension module using Java's ServiceLoader mechanism. If multiple
     * providers are found, only the first one is used and others are logged as warnings.
     *
     * @param vertx The Vertx instance
     * @return the extension module, or null if no provider found
     */
    private static Module discoverExtensionModule(Vertx vertx) {
        try {
            ServiceLoader<ModuleProvider> serviceLoader = ServiceLoader.load(ModuleProvider.class);
            ModuleProvider selectedProvider = null;

            for (ModuleProvider provider : serviceLoader) {
                if (selectedProvider == null) {
                    selectedProvider = provider;
                    LOGGER.info("Using extension module provider: {}", provider.getName());
                } else {
                    LOGGER.warn(
                            "Ignoring additional module provider: {}. Only one extension module is supported.",
                            provider.getName());
                }
            }

            if (selectedProvider != null) {
                Module module = selectedProvider.createModule(vertx);
                if (module != null) {
                    LOGGER.debug("Created extension module from provider: {}",
                            selectedProvider.getName());
                    return module;
                } else {
                    LOGGER.debug("Provider {} returned null module", selectedProvider.getName());
                }
            } else {
                LOGGER.debug("No extension module providers found");
            }

        } catch (Exception e) {
            LOGGER.warn("Failed to discover extension module", e);
        }

        return null;
    }

    /**
     * Initialize verticle factories.
     *
     * @param vertx the Vertx instance
     * @param flowContext the flow context
     * @param config the configuration
     */
    private static void initializeVerticleFactories(Vertx vertx, FlowContext flowContext,
            JsonObject config) {
        Optional<VerticleFactory> factory = vertx.verticleFactories().stream()
                .filter(f -> f instanceof JavaAppContextVerticleFactory).findAny();

        JavaAppContextVerticleFactory javaTaskFactory;
        if (factory.isEmpty()) {
            javaTaskFactory = new JavaAppContextVerticleFactory(flowContext, config);
            vertx.registerVerticleFactory(javaTaskFactory);
        } else {
            javaTaskFactory = (JavaAppContextVerticleFactory) factory.get();
        }

        flowContext.initialize(vertx, config);
        flowContext.setTaskClassLoader(javaTaskFactory.getTaskClassLoader());
    }
}
