package com.aregcraft.delta.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

public abstract class AcceptEula extends DefaultTask {
    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void run() throws IOException {
        Files.writeString(getOutputDir().file("eula.txt").get().getAsFile().toPath(), "eula=true");
    }
}
