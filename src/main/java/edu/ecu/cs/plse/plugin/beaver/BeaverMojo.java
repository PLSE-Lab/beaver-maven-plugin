/*
 * MIT License
 *
 * Copyright (c) 2022 ECU Programming Languages and Software Engineering Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.ecu.cs.plse.plugin.beaver;

import beaver.Parser;
import beaver.comp.ParserGenerator;
import beaver.comp.io.SrcReader;
import beaver.comp.run.Options;
import beaver.comp.util.Log;
import beaver.spec.Grammar;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Generates parsers for one or more <a href="http://beaver.sourceforge.net/">Beaver</a> grammar files.
 *
 * @author Mark Hills (mhills@acm.org)
 */
@Mojo( name="generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class BeaverMojo
    extends AbstractMojo
{
    /**
     * Name of the default directory for beaver grammar files
     */
    public static final String SRC_MAIN_BEAVER = "src/main/beaver";

    /**
     * The current project. See:
     * http://stackoverflow.com/questions/10606293/java-maven-mojo-getting-information-from-project-pom
     * for details about the settings for this parameter.
     */
    @Parameter(defaultValue="${project}", readonly=true, required=true)
    private MavenProject project;

    /**
     * List of grammar definitions that the Beaver parser generator will
     * be run on; each of these is either a grammar definition itself or
     * a directory containing grammar definitions. Grammar definitions
     * should all have the extension ".grammar".
     *
     * By default, all files in SRC_MAIN_BEAVER will be processed.
     *
     * @see #SRC_MAIN_BEAVER
     */
    @Parameter
    private File[] grammarDefinitions;

    /**
     * Name of the target directory for generation of the parser.
     */
    @Parameter
    private File outputDirectory;

    /**
     * Suppress compression of parsing tables. This is the equivalent of the
     * Beaver -c command-line option.
     */
    @Parameter(defaultValue = "false")
    private boolean suppressCompression;

    /**
     * Generate non-anonymous delegates. This is the equivalent of the Beaver
     * -n command-line option.
     */
    @Parameter(defaultValue = "false")
    private boolean generateNonAnonymousDelegates;

    /**
     * Sort terminals by name. This is the equivalent of the Beaver -s
     * command-line option.
     */
    @Parameter(defaultValue = "false")
    private boolean sortTerminalsByName;

    /**
     * Generate terminal names. This is the equivalent of the Beaver -t
     * command-line option.
     */
    @Parameter(defaultValue = "false")
    private boolean generateTerminalNames;

    /**
     * Create a separate Terminals class. This is the equivalent of the Beaver
     * -T command-line option.
     */
    @Parameter(defaultValue = "false")
    private boolean dumpTerminalsClass;

    /**
     * Use switching to invoke reduce action code. This is the equivalent of the
     * Beaver -w command-line option.
     */
    @Parameter(defaultValue = "false")
    private boolean useSwitching;

    /**
     * Options for the grammar generator
     */
    private Options beaverOptions;

    public void execute() throws MojoExecutionException {
        this.outputDirectory = getAbsolutePath(this.outputDirectory);
        project.addCompileSourceRoot(outputDirectory.getPath());

        List<File> definitions;
        if (grammarDefinitions == null) {
            getLog().debug("Use grammar files found in default: " + SRC_MAIN_BEAVER);
            definitions = new ArrayList<File>();
            File defaultDir = getAbsolutePath(new File(SRC_MAIN_BEAVER));
            getLog().debug("The absolute path for the files is: " + defaultDir.getAbsolutePath());
            if (defaultDir.isDirectory()) {
                definitions.add(defaultDir);
            }
        } else {
            // use arguments provided in the plugin configuration
            definitions = Arrays.asList(grammarDefinitions);

            getLog().debug("Parsing " + grammarDefinitions.length
                    + " Beaver grammar files or directories given in configuration");
        }

        // Configure the options for the parser generator, based on those given in the POM
        beaverOptions = new Options();
        beaverOptions.no_compression = suppressCompression;
        beaverOptions.name_action_classes = generateNonAnonymousDelegates;
        beaverOptions.sort_terminals = sortTerminalsByName;
        beaverOptions.terminal_names = generateTerminalNames;
        beaverOptions.export_terminals = dumpTerminalsClass;
        beaverOptions.use_switch = useSwitching;
        beaverOptions.dest_dir = outputDirectory;

        // process all Beaver definitions
        for (File definition : definitions) {
            definition = getAbsolutePath(definition);
            processDefinitions(definition);
        }
    }

    private void processDefinitions(File definitions) throws MojoExecutionException {
        if (definitions.isDirectory()) {
            String[] extensions = { "grammar" };
            getLog().debug("Processing grammar files found in: " + definitions.getAbsolutePath());
            Iterator<File> fileIterator = FileUtils.iterateFiles(definitions, extensions, true);
            while (fileIterator.hasNext()) {
                File definition = fileIterator.next();
                processDefinition(definition);
            }
        } else {
            processDefinition(definitions);
        }
    }

    private void processDefinition(File definition) throws MojoExecutionException {
        try {
            SrcReader reader = new SrcReader(definition);
            Log log = new Log();

            getLog().debug("Processing file: " + definition.getAbsolutePath());

            ParserGenerator.compile(reader, beaverOptions, log);

            if (log.hasErrors()) {
                getLog().debug("Found errors processing the file");
            } else {
                getLog().debug("No errors were found processing the file");
            }
        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage());
        } catch (Parser.Exception e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (Grammar.Exception e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    /**
     * Converts the specified path argument into an absolute path. This is
     * borrowed from the JFlex plugin.
     *
     * @param path The path argument to convert, may be {@code null}.
     * @return The absolute path corresponding to the input argument.
     */
    protected File getAbsolutePath(File path) {
        if (path == null || path.isAbsolute()) {
            return path;
        }
        return new File(this.project.getBasedir().getAbsolutePath(), path.getPath());
    }

}
