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

    private static final Logger logger = LoggerFactory.getLogger(ExtensionCiAssemblyCommand.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Integer call() throws Exception {
        logger.info("Extension CI assembly command execution");
        return super.call();
    }
}
