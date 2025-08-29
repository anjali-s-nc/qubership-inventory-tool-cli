package org.qubership.itool.cli.ci.extension;

import com.google.inject.Module;
import io.vertx.core.Vertx;
import org.qubership.itool.cli.spi.ModuleProvider;

/**
 * ModuleProvider implementation for the CI extension project. This replaces the old command-based
 * override approach with centralized SPI-based module management.
 */
public class ExtensionModuleProvider implements ModuleProvider {

    @Override
    public Module createModule(Vertx vertx) {
        return new ExtensionModule();
    }

    @Override
    public String getName() {
        return "CiExtensionModule";
    }
}
