# Extension Migration Guide: Vert.x 4 to 5

This guide is specifically for **extension developers** who need to migrate their extensions to work with the Qubership Inventory Tool CLI after its migration from Vert.x 4 to Vert.x 5.

## Overview

The core Qubership Inventory Tool CLI has been migrated from Vert.x 4 to Vert.x 5, which includes:
- **CLI Framework Change**: From Vert.x CLI to Picocli
- **Extension SPI Update**: New service provider interfaces
- **API Changes**: Future-based patterns replacing deprecated callback APIs

**Your extensions need to be updated to work with the new system.**

## What Extension Developers Need to Know

### 1. New Extension Architecture

The extension system has been redesigned with two main SPI interfaces:

#### CommandProvider Interface
```java
/**
 * Service Provider Interface for CLI commands.
 * Replaces the old Vert.x CommandFactory pattern.
 */
public interface CommandProvider {
    /**
     * Gets the name of the command (e.g., "my-command").
     * @return the command name
     */
    String getCommandName();
    
    /**
     * Creates the command instance.
     * @return the command object (must implement Callable<Integer>)
     */
    Object createCommand();
}
```

#### ModuleProvider Interface
```java
/**
 * Service Provider Interface for DI modules.
 * Use this if your extension needs custom dependency injection.
 */
public interface ModuleProvider {
    /**
     * Creates a Guice module for dependency injection.
     * @param vertx the Vertx instance
     * @return the configured module
     */
    Module createModule(Vertx vertx);
    
    /**
     * Gets the name of this module provider.
     * @return the module name
     */
    String getName();
}
```

## Migration Steps for Extension Developers

### Step 1: Update Command Implementation

#### Before (Vert.x 4 - Old Pattern)
```java
// Old command extending Vert.x base class
public class MyExtensionCommand extends ClasspathHandler {
    
    @Option(longName = "myOption", description = "My option")
    public void setMyOption(String value) {
        // Handle option
    }
    
    @Override
    public void run() {
        // Command logic
    }
}
```

#### After (Vert.x 5 - New Pattern)
```java
// New command implementing Callable<Integer>
@Command(name = "my-command", description = "My extension command")
public class MyExtensionCommand implements Callable<Integer> {
    
    @Option(names = {"--myOption"}, description = "My option")
    public void setMyOption(String value) {
        // Handle option
    }
    
    @Override
    public Integer call() throws Exception {
        // Command logic
        return 0; // Return exit code
    }
}
```

### Step 2: Create CommandProvider

#### Before (Vert.x 4 - CommandFactory)
```java
// Old CommandFactory pattern
public class MyExtensionCommandFactory extends CommandFactoryBase {
    @Override
    public Command create() {
        return new MyExtensionCommand();
    }
}
```

#### After (Vert.x 5 - CommandProvider)
```java
// New CommandProvider implementation
public class MyExtensionCommandProvider implements CommandProvider {
    
    @Override
    public String getCommandName() {
        return "my-command";
    }
    
    @Override
    public Object createCommand() {
        return new MyExtensionCommand();
    }
}
```

### Step 3: Update Service Registration

#### Before (Vert.x 4)
```properties
# META-INF/services/io.vertx.core.spi.launcher.CommandFactory
org.mycompany.MyExtensionCommandFactory
```

#### After (Vert.x 5)
```properties
# META-INF/services/org.qubership.itool.cli.spi.CommandProvider
org.mycompany.MyExtensionCommandProvider
```

### Step 4: Update Dependencies (if needed)

#### Add Picocli Dependency
```xml
<dependency>
    <groupId>info.picocli</groupId>
    <artifactId>picocli</artifactId>
    <version>4.6.3</version>
</dependency>
```

#### Update Vert.x Version
```xml
<properties>
    <vertx.version>5.0.1</vertx.version>
</properties>
```

### Step 5: Handle DI Integration (if using custom modules)

If your extension provides custom DI modules:

#### Create ModuleProvider
```java
public class MyExtensionModuleProvider implements ModuleProvider {
    
    @Override
    public Module createModule(Vertx vertx) {
        return new MyExtensionModule();
    }
    
    @Override
    public String getName() {
        return "MyExtensionModule";
    }
}
```

#### Register ModuleProvider
```properties
# META-INF/services/org.qubership.itool.cli.spi.ModuleProvider
org.mycompany.MyExtensionModuleProvider
```

## Common Migration Issues and Solutions

### Issue 1: Command Not Recognized
```text
Problem: Extension command not found after migration
Solution: 
- Verify CommandProvider is properly implemented
- Check service registration file exists and is correct
- Ensure command name matches exactly
```

### Issue 2: Option Parsing Errors
```text
Problem: Command line options not working
Solution:
- Update @Option annotations to Picocli format
- Use names = {"--option"} instead of longName = "option"
- Ensure command class has @Command annotation
```

### Issue 3: DI Context Not Available
```text
Problem: Cannot access application context in extension
Solution:
- Use ApplicationContextHolder.getContext() to access DI container
- Implement ModuleProvider if you need custom DI bindings
- Ensure your extension is loaded after core application context is initialized
```

### Issue 4: Vert.x API Compilation Errors
```text
Problem: Compilation errors with Vert.x APIs
Solution:
- Update to Vert.x 5.0.1
- Replace callback-based APIs with Future-based equivalents
- Update executeBlocking calls to use Supplier pattern
```

## Example: Complete Extension Migration

Here's a complete example of migrating an extension:

### Before (Vert.x 4 Extension)
```java
// MyExtensionCommand.java
public class MyExtensionCommand extends ClasspathHandler {
    @Option(longName = "input", description = "Input file")
    public void setInput(String input) {
        this.input = input;
    }
    
    @Override
    public void run() {
        // Process input
        System.out.println("Processing: " + input);
    }
}

// MyExtensionCommandFactory.java
public class MyExtensionCommandFactory extends CommandFactoryBase {
    @Override
    public Command create() {
        return new MyExtensionCommand();
    }
}
```

### After (Vert.x 5 Extension)
```java
// MyExtensionCommand.java
@Command(name = "my-extension", description = "My extension command")
public class MyExtensionCommand implements Callable<Integer> {
    
    @Option(names = {"--input"}, description = "Input file", required = true)
    private String input;
    
    @Override
    public Integer call() throws Exception {
        // Process input
        System.out.println("Processing: " + input);
        return 0;
    }
}

// MyExtensionCommandProvider.java
public class MyExtensionCommandProvider implements CommandProvider {
    
    @Override
    public String getCommandName() {
        return "my-extension";
    }
    
    @Override
    public Object createCommand() {
        return new MyExtensionCommand();
    }
}
```

### Service Registration Files
```properties
# META-INF/services/org.qubership.itool.cli.spi.CommandProvider
org.mycompany.MyExtensionCommandProvider
```

## Testing Your Extension

### 1. Build and Package
```bash
mvn clean package
```

### 2. Test Command Discovery
```bash
java -jar inventory-tool.jar --help
# Should show your extension command in the list
```

### 3. Test Command Execution
```bash
java -jar inventory-tool.jar my-extension --input test.txt
# Should execute your extension command
```

## Migration Checklist for Extension Developers

### Required Changes
- [ ] Update command class to implement `Callable<Integer>`
- [ ] Add `@Command` annotation to command class
- [ ] Update `@Option` annotations to Picocli format
- [ ] Create `CommandProvider` implementation
- [ ] Update service registration file
- [ ] Update Maven dependencies (Picocli, Vert.x 5.0.1)

### Optional Changes
- [ ] Implement `ModuleProvider` if using custom DI
- [ ] Update any Vert.x API usage to Future-based patterns
- [ ] Add comprehensive tests for new command structure

### Verification
- [ ] Extension command appears in help output
- [ ] Extension command executes successfully
- [ ] All command options work correctly
- [ ] DI integration works (if applicable)

## Getting Help

If you encounter issues during migration:

1. **Check the core migration guide** for detailed technical information
2. **Review existing extensions** in the project for reference implementations
3. **Test incrementally** - migrate one command at a time
4. **Use the provided examples** as templates for your migration

## Summary

The key changes for extension developers are:
- **Commands**: Use `Callable<Integer>` + `@Command` annotation instead of extending `ClasspathHandler`
- **Options**: Use Picocli `@Option` format instead of Vert.x CLI annotations
- **Registration**: Use `CommandProvider` SPI instead of `CommandFactory`
- **Dependencies**: Update to Picocli 4.6.3 and Vert.x 5.0.1

This migration ensures your extensions work with the modernized CLI framework while maintaining all existing functionality.
