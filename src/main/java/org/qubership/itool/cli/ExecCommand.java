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
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Picocli-based implementation of the exec command.
 *
 * <p>Usage examples:</p>
 * <pre>
 * java -jar exec -l your_login -p -sp true -df confluenceGenerate,confluenceUpload,excelExport,mavenDependency,repositoryUpdate
 * java -jar exec -l your_login -p -sp true -df confluenceUpload,mavenDependency,repositoryUpdate -ss excelExport -ls releaseDiff
 * java -jar exec -l your_login -p -sp confluenceGenerateComponentPages,parseComponentConfFiles -df .....
 * </pre>
 */
@Command(
    name = "exec",
    description = "Execute inventory-tool",
    mixinStandardHelpOptions = true
)
public class ExecCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecCommand.class);

    public static final String DEFAULT_INPUT_DIRECTORY_DOCKER = "/var/input";
    public static final String DEFAULT_OUTPUT_DIRECTORY_DOCKER = "/var/output";

    // ExecCommand specific options
    @Option(names = {"-l", "--login"}, description = "Login to access services requiring authentication")
    public void setLogin(String login) {
        properties.put("login", login);
    }

    @Option(names = {"-e", "--excelExport"}, description = "Path pattern for excel report export")
    public void setExcelExport(String excelExport) {
        properties.put("excelExport", excelExport);
    }

    @Option(names = {"-u", "--uploadConfluencePages"}, description = "List of the page titles to be uploaded to Confluence. Delimiter is ','. Examples: all; none; type:report; \"Tech of DOMAIN1, Cloud Libraries list\"")
    public void setUploadConfluencePages(String uploadConfluencePages) {
        properties.put("uploadConfluencePages", uploadConfluencePages);
    }

    @Option(names = {"-sp", "--saveProgress"}, description = "Save execution progress. That allow restart progress from the specified step.")
    public void setSaveProgress(String saveProgress) {
        properties.put("saveProgress", saveProgress);
    }

    @Option(names = {"-ss", "--startStep"}, description = "Start execution from the specified step if progress was saved before. See 'saveProgress' property.")
    public void setStartStep(String startStep) {
        properties.put("startStep", startStep);
    }

    @Option(names = {"-ls", "--lastStep"}, description = "Last execution step (if progress was saved before). See 'saveProgress' property.")
    public void setLastStep(String lastStep) {
        properties.put("lastStep", lastStep);
    }

    @Option(names = {"-id", "--includeDomains"}, description = "List of Domains that must be processed. Delimiter is ','")
    public void setIncludeDomains(String includeDomains) {
        properties.put("includeDomains", includeDomains);
    }

    @Option(names = {"-df", "--disabledFeatures"}, description = "List of the disabled features. Delimiter is ','. Possible: confluenceGenerate,confluenceUpload,excelExport,mavenDependency,repositoryUpdate")
    public void setDisabledFeatures(String disabledFeatures) {
        properties.put("disabledFeatures", disabledFeatures);
    }

    @Option(names = {"-r", "--release"}, description = "Release version to be used as a suffix during the export to Confluence")
    public void setRelease(String release) {
        properties.put("release", release);
    }

    @Option(names = {"-rb", "--releaseBranch"}, description = "Release version to be used as a suffix during the export to Confluence")
    public void setReleaseBranch(String releaseBranch) {
        properties.put("releaseBranch", releaseBranch);
    }

    @Option(names = {"-pr", "--priorRelease"}, description = "Release version prior to selected release to compare to")
    public void setPriorRelease(String priorRelease) {
        properties.put("priorRelease", priorRelease);
    }

    @Option(names = {"-pws", "--passwordSource"}, description = "Password source, e.g.: \"file:password.txt\"")
    public void setPasswordSource(String passwordSource) {
        properties.put("passwordSource", passwordSource);
    }

    @Option(names = {"--offline"}, description = "Offline mode: true/false")
    public void setOfflineMode(String offlineMode) {
        properties.put("offlineMode", offlineMode);
    }

    @Option(names = {"--docker", "--dockerMode"}, description = "Docker mode: true/false")
    public void setDockerMode(boolean dockerMode) {
        properties.put("dockerMode", String.valueOf(dockerMode));
        if (dockerMode) {
            properties.put("saveProgress", "false");
        }
    }

    @Option(names = {"--inputDirectory"}, description = "Input directory path")
    public void setInputDirectory(String inputDirectory) {
        properties.put("inputDirectory", inputDirectory);
    }

    @Option(names = {"--outputDirectory"}, description = "Output directory path")
    public void setOutputDirectory(String outputDirectory) {
        properties.put("outputDirectory", outputDirectory);
    }

    @Option(names = {"--defaultOutputDirectory"}, description = "Default output directory path")
    public void setDefaultOutputDirectory(String defaultOutputDirectory) {
        properties.put("defaultOutputDirectory", defaultOutputDirectory);
    }

        @Override
    public Integer call() throws Exception {
        LOGGER.info("Inventory tool main flow execution (Picocli version)");

        // Execute the flow using the existing AbstractCommand infrastructure
        runFlow(new ExecVerticle(), null);

        return 0;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

}
