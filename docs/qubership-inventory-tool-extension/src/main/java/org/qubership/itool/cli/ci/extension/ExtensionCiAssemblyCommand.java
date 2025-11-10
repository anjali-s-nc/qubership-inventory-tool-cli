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

package org.qubership.itool.cli.ci.extension;

import org.qubership.itool.cli.ci.CiAssemblyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;

/**
 * Extended implementation of CI assembly command for the extension project. This provides
 * extension-specific functionality while maintaining compatibility with the core CI assembly
 * command.
 */
@Command(name = "ci-assembly", description = "Extended CI flow: assembly with custom processing",
        mixinStandardHelpOptions = true)
public class ExtensionCiAssemblyCommand extends CiAssemblyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionCiAssemblyCommand.class);

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Integer call() throws Exception {
        LOGGER.info("Extension CI assembly command execution");
        return super.call();
    }
}
