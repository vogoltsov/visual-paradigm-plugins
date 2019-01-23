# Visual Paradigm Plugins
[Visual Paradigm](https://www.visual-paradigm.com) is a great tool for software system design and architecture.
This is a set of useful plugins that I have developed and use.

## Confluence Plugin
This plugin provides the ability to export diagrams as attachments
to [Atlassian Confluence](https://www.atlassian.com/software/confluence) using the REST API.

## Design Plugin
This plugin provides a set of useful design patterns and templates:
- model element stereotypes
- diagram element styles

Also there is a useful feature to auto-apply styles to diagram elements based on their stereotypes. 

## DevTools Plugin
This plugin is only useful during plugin development and normally should not be installed.


# Build and Install
[Apache Maven](http://maven.apache.org) is used as a build tool and needs to be installed and available as `mvn`.

## Build All Plugins
```bash
mvn -Pall-plugins clean install
```

## Build Single Plugin
Sometimes it is useful to build/install a single plugin. Run to build a confluence plugin:
```bash
mvn -Pconfluence-plugin clean install
```

## Installation
There are several ways to install a plugin to Visual Paradigm:
- Help > Install Plugin > Install from a zip of a plugin - select plugin `.jar` file
- install locally using maven (e.g. during development) using `install` profile:
    ```bash
    mvn -Pall-plugins -Pinstall clean install
    ``` 

# Development
## Debugging
To enable debugging the JVM:
1. Locate the `vplauncher.vmoptions` file containing the JVM launch arguments.
    On Mac, this file is located at the following path: `~/Library/Application Support/VisualParadigm/vplauncher.vmoptions`
1. Add these lines to `vplauncher.vmoptions`:
    ```text
    -Xdebug
    -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
    ```

## Class Reloading
DevTools plugin provides the ability to reload all the plugin classes using the Visual Paradigm API.
Unfortunately this API only reloads the classes, so any changes to `plugin.xml`
(UI modifications, plugin definition, etc.) require Visual Paradigm application to be restarted.
