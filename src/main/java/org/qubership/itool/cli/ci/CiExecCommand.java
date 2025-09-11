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
import java.io.File;
import java.util.Properties;

import static org.qubership.itool.cli.ci.CiConstants.*;
import static org.qubership.itool.utils.ConfigProperties.*;

import org.qubership.itool.cli.AbstractCommand;
import org.qubership.itool.cli.FlowMainVerticle;
import org.qubership.itool.modules.graph.GraphDataConstants;
import org.qubership.itool.utils.ConfigUtils;

/**
 * A command for single-component run on CI.
 * Works in "ci" profile.
 */
@Command(
    name = "ci-exec",
    description = "CI flow: parse a single component",
    mixinStandardHelpOptions = true
)
public class CiExecCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CiExecCommand.class);

    public static final String DEFAULT_OUTPUT_DIRECTORY_DESKTOP = "progress-ci";
    public static final String DEFAULT_INPUT_DIRECTORY_DOCKER = "/var/input";
    public static final String DEFAULT_OUTPUT_DIRECTORY_DOCKER = "/var/output";

    public CiExecCommand() {
        super();
        // Set CI-specific defaults
        properties.put(P_MOCK_DOMAIN, GraphDataConstants.UNKNOWN_DOMAIN_NAME);
        properties.put(PROFILE_POINTER, "ci");
        properties.put(OFFLINE_MODE, "true");
        properties.put(SAVE_PROGRESS, "false");
        properties.put(P_DEFAULT_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY_DESKTOP);
    }

    // CI Exec specific options
    @Option(names = {"--componentName"}, description = "Component name in builder", required = true)
    public void setComponentName(String componentName) {
        properties.put(P_COMP_NAME, componentName);
    }

    @Option(names = {"--componentVersion"}, description = "Component version in builder")
    public void setComponentVersion(String componentVersion) {
        properties.put(P_COMP_VERSION, componentVersion);
    }

    @Option(names = {"--repository"}, description = "Repository of the target component",
            required = true)
    public void setRepository(String repository) {
        if ("null".equals(repository)) {
            LOGGER.error("'null' repository name passed");
            System.exit(1);
        }
        properties.put(P_REPOSITORY, repository);
        String runName = repository.replaceFirst("^.*/(.*?)(\\.git)?$", "$1");
        properties.put(P_RUN_NAME, runName);
    }

    @Option(names = {"--inputDirectory"},
            description = "Input directory with sources of the target component")
    public void setInputDirectory(String inputDirectory) {
        properties.put(P_INPUT_DIRECTORY, inputDirectory);
    }

    @Option(names = {"--outputDirectory"}, description = "Output directory")
    public void setOutputDirectory(String outputDirectory) {
        properties.put(P_OUTPUT_DIRECTORY, outputDirectory);
    }

    @Option(names = {"--outputFile"}, description = "Output file name")
    public void setOutputFile(String outputFile) {
        properties.put(P_OUTPUT_FILE, outputFile);
    }

    @Option(names = {"--dumpResultsBy"},
            description = "Strategy for automatic generation of output file name when it is not provided. " +
                    "Choices: hash, id, repo")
    public void setDumpResultsBy(String dumpResults) {
        properties.put(P_DUMP_BY, dumpResults);
    }

    @Option(names = {"--docker", "--dockerMode"}, description = "Docker mode: true/false")
    public void setDockerMode(boolean dockerMode) {
        properties.put(P_DEFAULT_OUTPUT_DIRECTORY,
                dockerMode ? DEFAULT_OUTPUT_DIRECTORY_DOCKER : DEFAULT_OUTPUT_DIRECTORY_DESKTOP);
        properties.put("dockerMode", String.valueOf(dockerMode));
        if (dockerMode) {
            properties.put("saveProgress", "false");
        }
    }

    @Override
    public Integer call() throws Exception {
        LOGGER.info("Inventory tool main flow execution for CI (Picocli version)");

        // Log configuration
        logConfiguration();

        // Execute the flow using the existing AbstractCommand infrastructure
        runFlow(createMainVerticle(), null);

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
        LOGGER.info("repository: {}", properties.get(P_REPOSITORY));
        LOGGER.info("releaseBranch: {}", properties.get(RELEASE_POINTER));
        LOGGER.info("componentName: {}", properties.get(P_COMP_NAME));
        LOGGER.info("componentVersion: {}", properties.get(P_COMP_VERSION));
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

    protected FlowMainVerticle createMainVerticle() {
        String inputDir = properties.get(P_INPUT_DIRECTORY);

        File appInvJson = new File(inputDir, "application_inventory.json");
        boolean appExec = false;
        if (appInvJson.isFile()) {
            LOGGER.info("File {} exists!", appInvJson);
            appExec = true;
        } else {
            File appInvJson2 = new File(inputDir, "application-inventory.json");
            if (appInvJson2.isFile()) { // Fallback
                LOGGER.warn("File {} exists! Considering it as {}", appInvJson2, "application_inventory.json");
                appExec = true;
            }
        }

        if (appExec) {
            LOGGER.info("==> switching to Application flow");
            return new CiExecApplicationVerticle();
        } else {
            LOGGER.info("File {} does NOT exist", appInvJson);
            LOGGER.info("==> Normal flow");
            return new CiExecVerticle();
        }
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
