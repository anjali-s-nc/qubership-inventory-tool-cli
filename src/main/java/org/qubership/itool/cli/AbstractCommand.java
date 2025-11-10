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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.qubership.itool.context.FlowContext;
import org.qubership.itool.di.ApplicationContext;
import org.qubership.itool.modules.graph.GraphService;
import org.qubership.itool.utils.FutureUtils;
import org.slf4j.Logger;
import picocli.CommandLine.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.qubership.itool.utils.ConfigProperties.CONFIG_PATH_POINTER;
import static org.qubership.itool.utils.ConfigProperties.PROFILE_POINTER;

/**
 * Abstract base class for all CLI commands in the inventory tool. Provides common functionality for
 * command execution, configuration handling, and flow management.
 */
public abstract class AbstractCommand implements Callable<Integer> {

    /**
     * Configuration properties map containing default values for the command.
     */
    protected Map<String, String> properties =
            new HashMap<>(Map.of(PROFILE_POINTER, "dev", CONFIG_PATH_POINTER, "inventory-tool"));

    /**
     * Returns the logger instance for this command.
     *
     * @return the logger instance
     */
    protected abstract Logger getLogger();

    /**
     * Create VertX, start a flow using {@link #properties} and config, let the flow complete,
     * terminate JVM
     *
     * @param main Main verticle
     * @param graphService Graph Service to work with. May be omitted (set to null)
     */
    public void runFlow(FlowMainVerticle main, GraphService graphService) {
        Vertx vertx = Vertx.vertx();
        vertx.exceptionHandler(err -> {
            getLogger().error("Critical error, application is stopping", err);
            System.exit(1);
        });

        Future<?> flowFuture = runFlow(vertx, main, graphService).onComplete(ar -> {
            if (ar.failed()) {
                getLogger().error("Flow execution failed", ar.cause());
                System.exit(1);
            }
            System.exit(0);
        });
        FutureUtils.blockForResultOrException(flowFuture, 10, TimeUnit.HOURS);
    }

    /**
     * Runs the flow with the given Vertx instance, main verticle, and graph service. Configuration
     * is loaded by InventoryToolMain's execution strategy before this method is called.
     *
     * @param vertx the Vertx instance
     * @param main the main verticle
     * @param graphService the graph service (may be null)
     * @return a future that completes when the flow finishes
     */
    protected Future<?> runFlow(Vertx vertx, FlowMainVerticle main, GraphService graphService) {
        // Configuration is already loaded by the execution strategy and shared context is ready
        return Future.future(promise -> {
            try {
                // Get the shared context that was prepared by InventoryToolMain
                ApplicationContext context = getSharedApplicationContext();
                if (context == null) {
                    throw new IllegalStateException(
                            "Shared application context is not available. "
                            + "This indicates a problem with the execution strategy.");
                }

                FlowContext flowContext = context.getInstance(FlowContext.class);

                // Execute the flow
                main.deployAndRunFlow(flowContext).onComplete(flowResult -> {
                    promise.handle((AsyncResult<Object>) flowResult);
                    flowFinished(main, flowResult);
                });
            } catch (Throwable ex) {
                promise.fail(ex);
            }
        });
    }

    /**
     * Get the shared application context through a service locator pattern. This completely avoids
     * any dependency on InventoryToolMain.
     *
     * @return the shared application context
     */
    private ApplicationContext getSharedApplicationContext() {
        ApplicationContext context = ApplicationContextHolder.getInstance();
        if (context == null) {
            throw new IllegalStateException(
                    "No application context available. "
                    + "Ensure InventoryToolMain properly initialized the shared context.");
        }
        return context;
    }

    /**
     * Called when the flow has finished execution.
     *
     * @param main the main verticle
     * @param flowResult the result of the flow execution
     */
    protected void flowFinished(FlowMainVerticle main, AsyncResult<?> flowResult) {
        main.undeploy(); // Fire-and-forget
        if (flowResult.succeeded()) {
            getLogger().info("Flow succeeded. Undeploying and terminating.");
        } else {
            getLogger().error("Flow failed. Undeploying and terminating.", flowResult.cause());
        }
    }


    // ------------------------------------------------------
    // Common command-line parameters

    /**
     * Sets a property with the given parameters.
     *
     * @param params array of property parameters in format "name=value"
     */
    @Option(names = {"--set"}, description = "Universal setter: name=value (multiple)",
            required = false)
    public void setProperty(String[] params) {
        for (String param : params) {
            String[] pair = param.split("=", 2);
            properties.put(pair[0], pair.length > 1 ? pair[1] : "");
        }
    }

    /**
     * Sets the configuration path.
     *
     * @param configPath the path to the configuration folder
     */
    @Option(names = {"-c", "--configPath"},
            description = "Path to folder containing the configuration files", required = false)
    public void setConfigPath(String configPath) {
        properties.put(CONFIG_PATH_POINTER, configPath);
    }

    /**
     * Sets the profile to be used.
     *
     * @param profile the profile name or file
     */
    @Option(names = {"-p", "--profile"},
            description = "Custom profile to be used. "
                    + "By default uses properties format, in case of json, use file name with extension "
                    + "(Examples: \"custom\", \"custom.properties\", \"custom_example.json\")",
            required = false)
    public void setProfile(String profile) {
        properties.put(PROFILE_POINTER, profile);
    }

}
