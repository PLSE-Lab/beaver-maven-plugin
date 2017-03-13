# Beaver Maven Plugin

[![Build Status](https://travis-ci.org/ecu-pase-lab/beaver-maven-plugin.svg?branch=master)](https://travis-ci.org/ecu-pase-lab/beaver-maven-plugin)

This plugin allows Beaver to be integrated into a Maven build. Beaver grammar definition files
are included in a `beaver` language directory, with grammars generated in a `generated-sources`
directory under target.

## Adding the Beaver Plugin Repository

Currently the plugin is still under development so it is not yet on Maven central. To add
the repository for use in your own `pom.xml` file, add the following repository:

```
<pluginRepositories>
    <pluginRepository>
        <id>beaver-maven-plugin</id>
        <url>https://raw.github.com/ecu-pase-lab/beaver-maven-plugin/mvn-repo</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
        </snapshots>
    </pluginRepository>
</pluginRepositories>

```

This repository contains the current version of the plugin, based on the sources checked
in to GitHub. Feel free to change `updatePolicy` based on your needs. You also need to
add the plugin as a dependency:

```
<dependencies>
    <dependency>
        <groupId>edu.ecu.cs.pase</groupId>
        <artifactId>beaver-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Using the Plugin

To use the plugin, the following should be added to your `pom.xml` file as part of the
`build` configuration:

```
<build>
    <plugins>
        <plugin>
            <groupId>edu.ecu.cs.pase</groupId>
            <artifactId>beaver-maven-plugin</artifactId>
            <configuration>
                <grammarDefinitions>
                    <param>src/main/beaver/expr.grammar</param>
                </grammarDefinitions>
                <outputDirectory>target/generated-sources/beaver</outputDirectory>
            </configuration>
            <version>1.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>
                            generate
                        </goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

```

Most of this can remain unchanged. The grammar definition itself, given as the `param` in the
`grammarDefinitions` tag, should be changed to point to the Beaver grammar you are trying
to compile. The `outputDirectory` can probably remain unchanged unless you have a strong
reason for putting the compiled sources elsewhere. There are several other parameters
available as well; additional documentation will be forthcoming shortly.