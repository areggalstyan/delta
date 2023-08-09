package com.aregcraft.delta.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;

public abstract class DownloadBuildTools extends DefaultTask {
    @Input
    public abstract Property<String> getUrl();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void run() throws IOException {
        getProject().delete(getOutputDir().getAsFileTree());
        try (var writer = new FileOutputStream(getOutputDir().get().file("BuildTools.jar").getAsFile())) {
            writer.getChannel().transferFrom(Channels.newChannel(new URL(getUrl().get()).openStream()),
                    0, Long.MAX_VALUE);
        }
    }
}
