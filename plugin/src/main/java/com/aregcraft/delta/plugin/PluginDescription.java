package com.aregcraft.delta.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

import java.io.Serializable;

@JsonSerialize(as = PluginDescription.class)
public interface PluginDescription extends Serializable {
    Property<String> getMain();

    Property<String> getName();

    Property<String> getVersion();

    Property<String> getDescription();

    @JsonProperty("api-version")
    Property<String> getApiVersion();

    Property<String> getLoad();

    Property<String> getAuthor();

    ListProperty<String> getAuthors();

    Property<String> getWebsite();

    ListProperty<String> getDepend();

    Property<String> getPrefix();

    @JsonProperty("softdepend")
    ListProperty<String> getSoftDepend();

    @JsonProperty("loadbefore")
    ListProperty<String> getLoadBefore();

    ListProperty<String> getLibraries();

    NamedDomainObjectContainer<Command> getCommands();

    NamedDomainObjectContainer<Permission> getPermissions();

    @JsonSerialize(as = Command.class)
    interface Command {
        @JsonIgnore
        String getName();

        Property<String> getDescription();

        ListProperty<String> getAliases();

        Property<String> getPermission();

        @JsonProperty("permission-message")
        Property<String> getPermissionMessage();

        Property<String> getUsage();
    }

    @JsonSerialize(as = Permission.class)
    interface Permission {
        @JsonIgnore
        String getName();

        Property<String> getDescription();

        Property<String> getDefault();

        MapProperty<String, Boolean> getChildren();
    }
}
