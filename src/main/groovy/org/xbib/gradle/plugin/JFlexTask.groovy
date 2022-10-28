package org.xbib.gradle.plugin

import jflex.exceptions.GeneratorException
import jflex.generator.LexGenerator
import jflex.logging.Out
import jflex.option.Options
import jflex.skeleton.Skeleton
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
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

    @Input
    @Optional
    abstract Property<String> getEncoding()

    @InputDirectory
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    abstract DirectoryProperty getRootDirectory()

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    abstract RegularFileProperty getSkel()

    @Input
    abstract Property<Boolean> getVerbose()

    @Input
    abstract Property<Boolean> getJlex()

    @Input
    abstract Property<Boolean> getNo_minimize()

    @Input
    abstract Property<Boolean> getNo_backup()

    @Input
    abstract Property<Boolean> getUnused_warning()

    @Input
    abstract Property<Boolean> getProgress()

    @Input
    abstract Property<Boolean> getTime()

    @Input
    abstract Property<Boolean> getDot()

    @Input
    abstract Property<Boolean> getDump()

    @Input
    abstract Property<Boolean> getLegacy_dot()

    @Input
    abstract Property<Boolean> getStatistics()

    @Input
    abstract Property<Boolean> getWriteIntoJavaSrc()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    Iterable<File> source

    @OutputDirectory
    abstract DirectoryProperty getTarget()

    @TaskAction
    void generateAndTransformJflex() throws Exception {
        Options.setRootDirectory(rootDirectory.asFile.getOrElse(new File("")))
        Skeleton.readDefault()
        if (skel.isPresent()) {
            Skeleton.readSkelFile(skel.get().asFile)
        }
        Options.encoding = encoding.map({ Charset.forName(it) }).getOrElse(Charset.defaultCharset())
        Options.verbose = verbose.get()
        Options.progress = progress.get()
        Options.unused_warning = unused_warning.get()
        Options.jlex = jlex.get()
        Options.no_minimize = no_minimize.get()
        Options.no_backup = no_backup.get()
        Options.time = time.get()
        Options.dot = dot.get()
        Options.dump = dump.get()
        Options.legacy_dot = legacy_dot.get()
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
        if (statistics.get()) {
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
