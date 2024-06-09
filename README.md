# Delta

Delta is the backbone of my plugins: Reforging and Pets. It consists of three modules: plugin, api, and meta.

## Plugin

This module is a gradle plugin designed to aid in developing and testing Spigot plugins. There are two categories of
tasks: setup and testing.

### Setup

The tasks in this category are responsible for downloading the BuildTools, building the server, accepting the EULA, and
copying it into the project directory.

### Testing

The tasks in this category are responsible for deleting the old build of the plugin, generating the plugin description
file required by Spigot, building the new version, copying it into the server, resetting the configuration files and
running the server.

## API

This module is the library shaded into the plugin file and used at runtime. It provides a more user-friendly API for
developing Spigot plugins. It supplies extensive, convenient, and powerful APIs for creating custom blocks, and
commands, managing items, entities, recipes, and event listeners, formatting text, logging, and scheduling
(a)synchronous tasks. It supplies an annotation-based configuration system, on top of which there is a registry system
for managing the configuration files. It has an auto-updater and a localization system, which are extremely easy to
integrate.

## Meta

This module is currently of limited use. It is a Java doclet that analyzes code at compile-time, parses Javadocs from 
specific places, and produces a JSON file intended to be used by the documentation website for dynamically updating the
configuration guide. The JSON file also contains the version of the plugin used by the auto-updater.
