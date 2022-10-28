package org.xbib.gradle.plugin

import jflex.exceptions.GeneratorException
import jflex.generator.LexGenerator
import jflex.logging.Out
import jflex.option.Options
import jflex.skeleton.Skeleton
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

import java.nio.charset.Charset

@CacheableTask
abstract class JFlexTask extends DefaultTask {

    private final Logger logger = Logging.getLogger(JFlexTask)

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    Iterable<File> source

    @OutputDirectory
    abstract DirectoryProperty getTarget()

    @TaskAction
    void generateAndTransformJflex() throws Exception {
        JFlexExtension ext = project.extensions.findByType(JFlexExtension)
        Options.setRootDirectory(ext.rootDirectory.asFile.getOrElse(new File("")))
        Skeleton.readDefault()
        if (ext.skel.isPresent()) {
            Skeleton.readSkelFile(ext.skel.get().asFile)
        }
        Options.encoding = ext.encoding.map({ Charset.forName(it) }).getOrElse(Charset.defaultCharset())
        Options.verbose = ext.verbose.get()
        Options.progress = ext.progress.get()
        Options.unused_warning = ext.unused_warning.get()
        Options.jlex = ext.jlex.get()
        Options.no_minimize = ext.no_minimize.get()
        Options.no_backup = ext.no_backup.get()
        Options.time = ext.time.get()
        Options.dot = ext.dot.get()
        Options.dump = ext.dump.get()
        Options.legacy_dot = ext.legacy_dot.get()
        File targetFile = target.get().asFile
        source.each { file ->
            String pkg = getPackageName(file)
            File fullTarget = new File(targetFile, pkg.replace('.', '/'))
            fullTarget.mkdirs()
            Options.directory = fullTarget
            logger.info "jflex task: source=${file} pkg=${pkg} dir=${target}"
            try {
                new LexGenerator(file).generate()
            } catch (GeneratorException e) {
                Logging.getLogger(JFlexTask).error("JFlex error: ${e.message}", e)
                throw new StopActionException('an error occurred during JFlex code generation')
            }
        }
        if (ext.statistics.get()) {
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
