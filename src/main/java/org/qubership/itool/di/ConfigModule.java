package org.qubership.itool.di;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.vertx.core.json.JsonObject;

/**
 * Module that provides application configuration.
 * This module binds the application configuration JsonObject with a named binding
 * to distinguish it from other JsonObject instances that might be needed in the future.
 */
public class ConfigModule extends AbstractModule {

    private final JsonObject config;

    /**
     * Creates a new ConfigModule with the application configuration.
     *
     * @param config The application configuration
     */
    public ConfigModule(JsonObject config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(JsonObject.class)
            .annotatedWith(Names.named("application.config"))
            .toInstance(config);
    }
}
