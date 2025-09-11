package org.qubership.itool.cli.ci.extension;

import org.qubership.itool.cli.spi.CommandProvider;

import java.util.concurrent.Callable;

/**
 * Extension command provider for the CI assembly command. This registers the extended CI assembly
 * command with the main application.
 */
public class ExtensionCiAssemblyCommandProvider implements CommandProvider {

    @Override
    public Callable<Integer> createCommand() {
        return new ExtensionCiAssemblyCommand();
    }

    @Override
    public String getCommandName() {
        return "ci-assembly";
    }
}
