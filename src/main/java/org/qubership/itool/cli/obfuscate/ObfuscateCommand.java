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

package org.qubership.itool.cli.obfuscate;

import org.qubership.itool.cli.AbstractCommand;
import org.qubership.itool.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Properties;

import static org.qubership.itool.cli.ci.CiConstants.*;
import static org.qubership.itool.utils.ConfigProperties.*;

/**
 * Picocli-based implementation of the obfuscate command.
 * A command for obfuscation of CI run or assembly.
 *
 * <p>Run example:
 *<pre>
 * java -jar &lt;JAR&gt; obfuscate \
 *  --inputFile=/path/to/assembly.result.json \
 *  --outputFile=/path/to/obfuscate.result.json
 *</pre>
 */
@Command(
    name = "obfuscate",
    description = "Obfuscate Graph",
    mixinStandardHelpOptions = true
)
public class ObfuscateCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObfuscateCommand.class);

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    public ObfuscateCommand() {
        super();
        properties.put(PROFILE_POINTER, "ci");
        properties.put(OFFLINE_MODE, "true");
        properties.put(SAVE_PROGRESS, "false");
    }

    @Option(names = {"--inputDirectory"}, description = "Input directory")
    public void setInputDirectory(String inputDirectory) {
        this.properties.put(P_INPUT_DIRECTORY, inputDirectory);
    }

    @Option(names = {"--inputFile"}, description = "Input file name", required = true)
    public void setInputFile(String inputFile) {
        this.properties.put(P_INPUT_FILE, inputFile);
    }

    @Option(names = {"--outputDirectory"}, description = "Output directory")
    public void setOutputDirectory(String outputDirectory) {
        this.properties.put(P_OUTPUT_DIRECTORY, outputDirectory);
    }

    @Option(names = {"--outputFile"}, description = "Output file name", required = true)
    public void setOutputFile(String outputFile) {
        this.properties.put(P_OUTPUT_FILE, outputFile);
    }

    @Option(names = {"-r", "--obfuscationRules"}, description = "Path to the obfuscated graph dump")
    public void setObfuscationRules(String obfuscationRules) {
        this.properties.put(OBFUSCATION_RULES, obfuscationRules);
    }

    @Override
    public Integer call() throws Exception {
        LOGGER.info("Obfuscation main flow execution");
        LOGGER.info("----- Configuration -----");
        Properties buildProperties = ConfigUtils.getInventoryToolBuildProperties();
        LOGGER.info("cli version: {}", buildProperties.get("inventory-tool-cli.version"));
        LOGGER.info("profile: {}", properties.get(PROFILE_POINTER));
        LOGGER.info("inputDirectory: {}", properties.get(P_INPUT_DIRECTORY));
        LOGGER.info("inputFile: {}", properties.get(P_INPUT_FILE));
        LOGGER.info("explicit outputDirectory: {}", properties.get(P_OUTPUT_DIRECTORY));
        LOGGER.info("outputFile: {}", properties.get(P_OUTPUT_FILE));

        // Execute the flow using the existing AbstractCommand infrastructure
        runFlow(new ObfuscationMainVerticle(), null);

        return 0;
    }
}
