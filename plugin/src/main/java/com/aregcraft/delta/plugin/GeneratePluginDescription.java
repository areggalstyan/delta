package com.aregcraft.delta.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public abstract class GeneratePluginDescription extends DefaultTask {
    @OutputDirectory
    public abstract DirectoryProperty getResourcesDir();

    public GeneratePluginDescription() {
        getOutputs().upToDateWhen(it -> false);
    }

    @TaskAction
    public void run() throws IOException {
        new ObjectMapper(new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .enable(YAMLGenerator.Feature.INDENT_ARRAYS))
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .registerModule(new SimpleModule()
                        .addSerializer(new ProviderSerializer())
                        .addSerializer(new NamedDomainObjectContainerSerializer()))
                .writeValue(getResourcesDir().file("plugin.yml").get().getAsFile(),
                        getProject().getExtensions().getByType(PluginDescription.class));
    }
}
