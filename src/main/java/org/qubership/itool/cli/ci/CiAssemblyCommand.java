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

package org.qubership.itool.cli.ci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.Properties;

import static org.qubership.itool.cli.ci.CiConstants.*;
import static org.qubership.itool.utils.ConfigProperties.*;

import org.qubership.itool.cli.AbstractCommand;
import org.qubership.itool.utils.ConfigUtils;

/**
 * A command for executing CI assembly.
 * Works in "ci" profile, loads domains from it before merging graphs.
 */
@Command(
    name = "ci-assembly",
    description = "CI flow: assembly",
    mixinStandardHelpOptions = true
)
public class CiAssemblyCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CiAssemblyCommand.class);

    public static final String DEFAULT_OUTPUT_DIRECTORY_DESKTOP = "progress";
    public static final String DEFAULT_INPUT_DIRECTORY_DOCKER = "/var/input";
    public static final String DEFAULT_OUTPUT_DIRECTORY_DOCKER = "/var/output";

    public CiAssemblyCommand() {
        super();
        // Set CI assembly-specific defaults
        // "ci" to include internal domains only
        properties.put(PROFILE_POINTER, "ci");
        properties.put(OFFLINE_MODE, "true");
        properties.put(SAVE_PROGRESS, "false");
        properties.put(P_DEFAULT_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY_DESKTOP);
        properties.put(P_OUTPUT_FILE, "assembly.result.json");
    }

    // CI Assembly specific options
    @Option(names = {"--inputDirectory", "--inputDir"}, description = "Input directory. All files within it will be loaded and merged.")
    public void setInputDirectory(String inputDirectory) {
        properties.put(P_INPUT_DIRECTORY, inputDirectory);
    }

    @Option(names = {"--outputDirectory"}, description = "Output directory for resulting graph")
    public void setOutputDirectory(String outputDirectory) {
        properties.put(P_OUTPUT_DIRECTORY, outputDirectory);
    }

    @Option(names = {"--outputFile"}, description = "Output file name for resulting graph")
    public void setOutputFile(String outputFile) {
        properties.put(P_OUTPUT_FILE, outputFile);
    }

    @Option(names = {"--appName", "--appname"}, description = "Application name, e.g.: \"Inventory-Tool\"", required = true)
    public void setAppName(String appName) {
        properties.put(P_APP_NAME, appName);
    }

    @Option(names = {"--appVersion"}, description = "Application version from builder, e.g.: \"main-SNAPSHOT\"")
    public void setAppVersion(String appVersion) {
        properties.put(P_APP_VERSION, appVersion);
    }

    @Option(names = {"--docker", "--dockerMode"}, description = "Docker mode: true/false")
    public void setDockerMode(boolean dockerMode) {
        properties.put(P_DEFAULT_OUTPUT_DIRECTORY, dockerMode ? DEFAULT_OUTPUT_DIRECTORY_DOCKER : DEFAULT_OUTPUT_DIRECTORY_DESKTOP);
        properties.put("dockerMode", String.valueOf(dockerMode));
        if (dockerMode) {
            properties.put("saveProgress", "false");
        }
    }

    @Override
    public Integer call() throws Exception {
        LOGGER.info("Inventory tool assembly flow execution for CI (Picocli version)");

        // Log configuration
        logConfiguration();

        // Execute the flow using the existing AbstractCommand infrastructure
        runFlow(new CiAssemblyVerticle(), null);

        return 0;
    }

    /**
     * Logs the current configuration for debugging purposes.
     */
    private void logConfiguration() {
        LOGGER.info("----- Configuration -----");
        Properties buildProperties = ConfigUtils.getInventoryToolBuildProperties();
        LOGGER.info("cli version: {}", buildProperties.get("inventory-tool-cli.version"));
        LOGGER.info("profile: {}", properties.get(PROFILE_POINTER));
        logAndFillDirs();
        LOGGER.info("outputFile: {}", properties.get(P_OUTPUT_FILE));
        LOGGER.info("appName: {}", properties.get(P_APP_NAME));
        LOGGER.info("appVersion: {}", properties.get(P_APP_VERSION));
    }

    /**
     * Processing for default directories.
     * When flow runs, "ciInputDirectory" and "ciOutputDirectory" may be taken from "ci.properties" file.
     * This method helps the command to fall back to default directories before the flow starts.
     */
    protected void logAndFillDirs() {
        boolean isDocker = Boolean.parseBoolean(properties.getOrDefault("dockerMode", "false"));
        LOGGER.info("dockerMode: {}", isDocker);

        String defInputDirectory = getDefaultInputDir(isDocker);
        String defOutputDirectory = getDefaultOutputDir(isDocker);

        LOGGER.info("explicit inputDirectory: {}", properties.get(P_INPUT_DIRECTORY));
        LOGGER.info("default inputDirectory: {}", defInputDirectory);
        LOGGER.info("explicit outputDirectory: {}", properties.get(P_OUTPUT_DIRECTORY));
        LOGGER.info("default outputDirectory: {}", defOutputDirectory);

        if (!properties.containsKey(P_INPUT_DIRECTORY)) {
            if (defInputDirectory != null) {
                properties.put(P_INPUT_DIRECTORY, DEFAULT_INPUT_DIRECTORY_DOCKER);
            } else {
                LOGGER.error("Either --docker=true or --inputDirectory must be specified! EXITTING!");
                System.exit(1);
            }
        }
        if (!properties.containsKey(P_OUTPUT_DIRECTORY)) {
            if (defOutputDirectory != null) {
                properties.put(P_OUTPUT_DIRECTORY, defOutputDirectory);
            }
        }
    }

    protected String getDefaultInputDir(boolean isDocker) {
        return isDocker ? DEFAULT_INPUT_DIRECTORY_DOCKER : null;
    }

    protected String getDefaultOutputDir(boolean isDocker) {
        return properties.get(P_DEFAULT_OUTPUT_DIRECTORY);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
