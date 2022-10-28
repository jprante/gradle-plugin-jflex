package org.xbib.gradle.plugin

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

abstract class JFlexExtension {

    abstract Property<String> getEncoding()

    abstract DirectoryProperty getRootDirectory()

    abstract RegularFileProperty getSkel()

    abstract Property<Boolean> getVerbose()

    abstract Property<Boolean> getJlex()

    abstract Property<Boolean> getNo_minimize()

    abstract Property<Boolean> getNo_backup()

    abstract Property<Boolean> getUnused_warning()

    abstract Property<Boolean> getProgress()

    abstract Property<Boolean> getTime()

    abstract Property<Boolean> getDot()

    abstract Property<Boolean> getDump()

    abstract Property<Boolean> getLegacy_dot()

    abstract Property<Boolean> getStatistics()

    abstract Property<Boolean> getWriteIntoJavaSrc()

    JFlexExtension() {
        verbose.convention(false)
        jlex.convention(false)
        no_minimize.convention(false)
        no_backup.convention(false)
        unused_warning.convention(false)
        progress.convention(false)
        time.convention(false)
        dot.convention(false)
        dump.convention(false)
        legacy_dot.convention(false)
        statistics.convention(false)
        writeIntoJavaSrc.convention(false)
    }
}
