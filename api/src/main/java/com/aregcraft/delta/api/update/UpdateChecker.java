package com.aregcraft.delta.api.update;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.json.annotation.JsonConfiguration;
import com.aregcraft.delta.api.log.Crash;
import com.aregcraft.delta.api.log.Info;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@JsonConfiguration("update_checker")
public class UpdateChecker {
    private static final String META_URL = "https://raw.githubusercontent.com/areggalstyan/%s/master/meta.json";

    private final String message;
    private final long period;

    public UpdateChecker(String message, long period) {
        this.message = message;
        this.period = period;
    }

    public static String getLatestVersion(DeltaPlugin plugin) {
        try (var reader = new InputStreamReader(new URL(getMetaUrl(plugin)).openStream())) {
            return plugin.getGson().fromJson(reader, Meta.class).version;
        } catch (Exception e) {
            Crash.Default.UPDATE_UNABLE_GET_LATEST.withThrowable(e).log(plugin);
        }
        return null;
    }

    private static String getMetaUrl(DeltaPlugin plugin) {
        return META_URL.formatted(plugin.getName().toLowerCase());
    }

    public void scheduleChecks(DeltaPlugin plugin) {
        plugin.getAsynchronousScheduler().scheduleRepeatingTask(() -> check(plugin), period);
    }

    public void check(DeltaPlugin plugin) {
        if (!plugin.getDescription().getVersion().equals(getLatestVersion(plugin))) {
            new Info.Custom(message).log(plugin);
        }
    }

    private static class Meta {
        private String version;
    }
}
