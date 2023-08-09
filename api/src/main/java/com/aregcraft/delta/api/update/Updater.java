package com.aregcraft.delta.api.update;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.log.Crash;
import com.aregcraft.delta.api.log.Info;
import com.aregcraft.delta.api.log.Warning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;

public class Updater {
    private static final String JAR_URL = "https://github.com/areggalstyan/%s/releases/download/v%s/%1$s-%2$s.jar";
    private static final String JAR_PATH = "%s-%s.jar";

    private final DeltaPlugin plugin;
    private final String name;

    public Updater(DeltaPlugin plugin) {
        this.plugin = plugin;
        name = plugin.getName().toLowerCase();
    }

    public void tryDownloadLatestVersion() {
        var currentVersion = plugin.getDescription().getVersion();
        var latestVersion = UpdateChecker.getLatestVersion(plugin);
        if (currentVersion.equals(latestVersion)) {
            Info.Default.UPDATE_ALREADY_LATEST.log(plugin);
            return;
        }
        try (var reader = new URL(JAR_URL.formatted(name, latestVersion)).openStream();
             var channel = new FileOutputStream(getJar(latestVersion)).getChannel()) {
            channel.transferFrom(Channels.newChannel(reader), 0, Long.MAX_VALUE);
            Info.Default.UPDATE_SUCCESS.log(plugin);
        } catch (IOException e) {
            Crash.Default.UPDATE_UNABLE_DOWNLOAD.withThrowable(e).log(plugin);
        }
        if (!getJar(currentVersion).delete()) {
            Warning.Default.UPDATE_UNABLE_DELETE.log(plugin);
        }
    }

    private File getJar(String version) {
        return new File(plugin.getDataFolder().getParentFile(), JAR_PATH.formatted(name, version));
    }

    private static class Meta {
        private String version;
    }
}
