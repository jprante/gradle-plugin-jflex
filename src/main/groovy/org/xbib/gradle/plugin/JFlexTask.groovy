package org.xbib.gradle.plugin

import jflex.exceptions.GeneratorException
import jflex.generator.LexGenerator
import jflex.logging.Out
import jflex.option.Options
import jflex.skeleton.Skeleton
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

import java.nio.charset.Charset

class JFlexTask extends DefaultTask {

    @InputFiles
    Iterable<File> source

    @OutputDirectory
    File target

    @TaskAction
    void generateAndTransformJflex() throws Exception {
        JFlexExtension ext = project.extensions.findByType(JFlexExtension)
        Options.setRootDirectory(ext.rootDirectory ? ext.rootDirectory : new File(""))
        Skeleton.readDefault()
        if (ext.skel) {
            Skeleton.readSkelFile(ext.skel)
        }
        Options.encoding = ext.encoding ? Charset.forName(ext.encoding) : Charset.defaultCharset()
        Options.verbose = ext.verbose
        Options.progress = ext.progress
        Options.unused_warning = ext.unused_warning
        Options.jlex = ext.jlex
        Options.no_minimize = ext.no_minimize
        Options.no_backup = ext.no_backup
        Options.time = ext.time
        Options.dot = ext.dot
        Options.dump = ext.dump
        Options.legacy_dot = ext.legacy_dot
        source.each { file ->
            String pkg = getPackageName(file)
            File fullTarget = new File(target, pkg.replace('.','/'))
            project.mkdir(fullTarget)
            Options.directory = fullTarget
            Logging.getLogger(JFlexTask).info "jflex task: source=${file} pkg=${pkg} dir=${target}"
            try {
                new LexGenerator(file).generate()
            } catch (GeneratorException e) {
                Logging.getLogger(JFlexTask).error("JFlex error: ${e.message}", e)
                throw new StopActionException('an error occurred during JFlex code generation')
            }
        }
        if (ext.statistics) {
            Out.statistics()
        }
    }

    static String getPackageName(File file) {
        String string = file.readLines().find { line ->
            line.startsWith('package')
        }
        return string == null ? '' : string.substring(8, string.length() - 1)
    }
}
