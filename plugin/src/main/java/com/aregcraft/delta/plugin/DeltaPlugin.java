package com.aregcraft.delta.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.bundling.Jar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DeltaPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        addRepositories(target);
        removeArchiveClassifier(target);
        registerPluginDescription(target);
        getShadowJar(target).dependsOn("generatePluginDescription");
        registerTask(target, "downloadBuildTools", DownloadBuildTools.class,
                this::configureDownloadBuildTools);
        registerTask(target, "buildServer", JavaExec.class, this::configureBuildServer);
        registerTask(target, "copyServer", Copy.class, this::configureCopyServer);
        registerTask(target, "acceptEula", AcceptEula.class, this::configureAcceptEula);
        registerTask(target, "cleanServer", Delete.class, this::configureCleanServer);
        registerTask(target, "generatePluginDescription", GeneratePluginDescription.class,
                this::configureGeneratePluginDescription);
        registerTask(target, "copyPlugin", Copy.class, this::configureCopyPlugin);
        registerTask(target, "runServer", JavaExec.class, this::configureRunServer);
        registerTask(target, "postUpdate", PostUpdate.class, this::configurePostUpdate);
    }

    private void addRepositories(Project target) {
        target.getRepositories().mavenCentral();
        target.getRepositories()
                .maven(it -> it.setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
    }

    private void removeArchiveClassifier(Project target) {
        getShadowJar(target).getArchiveClassifier().set((String) null);
    }

    private void registerPluginDescription(Project target) {
        var pluginDescription = target.getExtensions()
                .create("pluginDescription", PluginDescription.class);
        pluginDescription.getName().convention(capitalize(target.getName()));
        pluginDescription.getVersion().convention(target.provider(() -> target.getVersion().toString()));
    }

    private String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    private <T extends Task> void registerTask(Project target, String name, Class<T> type,
                                               CheckedAction<? super T> configurationAction) {
        target.getTasks().register(name, type, configurationAction).configure(it -> it.setGroup("plugin"));
    }

    private void configureDownloadBuildTools(DownloadBuildTools task) {
        task.getUrl().set("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target"
                + "/BuildTools.jar");
        task.getOutputDir().set(getPluginBuildToolsDir(task));
    }

    private void configureBuildServer(JavaExec task) {
        task.dependsOn("downloadBuildTools");
        task.workingDir(getPluginBuildToolsDir(task));
        task.classpath(new File(getPluginBuildToolsDir(task), "BuildTools.jar"));
    }

    private void configureCopyServer(Copy task) throws IOException {
        if (isDirectoryEmpty(getPluginBuildToolsDir(task))) {
            task.dependsOn("buildServer");
        }
        task.dependsOn("acceptEula");
        task.from(getPluginBuildToolsDir(task));
        task.into(getServerDir(task));
        task.include("spigot-*.jar");
        task.rename("spigot-.*.jar", "server.jar");
    }

    private void configureAcceptEula(AcceptEula task) {
        task.getOutputDir().set(getServerDir(task));
    }

    private void configureCleanServer(Delete task) {
        task.delete(task.getProject().fileTree(getPluginsDir(task))
                .include(task.getProject().getName() + "-*.jar"),
                new File(getPluginsDir(task), getPluginName(task)));
    }

    private void configureGeneratePluginDescription(GeneratePluginDescription task) {
        task.getResourcesDir().set(task.getProject().getExtensions().getByType(SourceSetContainer.class)
                .getByName("main").getOutput().getResourcesDir());
    }

    private void configureCopyPlugin(Copy task) {
        task.dependsOn("cleanServer");
        task.from(getShadowJar(task.getProject()).getArchiveFile());
        task.into(getPluginsDir(task));
    }

    private void configureRunServer(JavaExec task) throws IOException {
        if (isDirectoryEmpty(getServerDir(task))) {
            task.dependsOn("copyServer");
        }
        task.dependsOn("copyPlugin");
        task.workingDir(getServerDir(task));
        task.classpath(new File(task.getWorkingDir(), "server.jar"));
        task.args("nogui");
        task.setStandardInput(System.in);
    }

    private void configurePostUpdate(PostUpdate task) {
        task.dependsOn("copyPlugin");
    }

    private Jar getShadowJar(Project target) {
        return (Jar) target.getTasks().getByName("shadowJar");
    }

    private String getPluginName(Task task) {
        return task.getProject().getExtensions().getByType(PluginDescription.class).getName().get();
    }

    private boolean isDirectoryEmpty(File dir) throws IOException {
        try (var directoryStream = Files.newDirectoryStream(dir.toPath())) {
            return !dir.exists() || !directoryStream.iterator().hasNext();
        }
    }

    private File getPluginsDir(Task task) {
        return new File(getServerDir(task), "plugins");
    }

    private File getServerDir(Task task) {
        return task.getProject().file("debug/server");
    }

    private File getPluginBuildToolsDir(Task task) {
        return new File(task.getProject().getGradle().getGradleUserHomeDir(), "plugin-build-tools");
    }
}
