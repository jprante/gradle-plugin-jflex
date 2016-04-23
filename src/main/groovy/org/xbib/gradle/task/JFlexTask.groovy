package org.xbib.gradle.task;

import jflex.Main
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class JFlexTask extends DefaultTask {

    @InputDirectory
    @Optional
    File source = project.file('src/main/jflex')

    @OutputDirectory
    @Optional
    File generateDir = project.file("$project.buildDir/generated-src/jflex")

    @TaskAction
    void generateAndTransformJflex() throws Exception {
        project.delete(generateDir)
        project.mkdir(generateDir)
        generateJflex()
    }

    private void generateJflex() {
        project.fileTree(dir:source, include:'**/*.jflex').visit { FileVisitDetails file ->
            if (file.isDirectory()) {
                return
            }
            Main.generate(['-q', file.file.absolutePath,
                           '-d', project.file("$generateDir/${file.relativePath.parent}").absolutePath + '/'] as String[])
        }
    }
}
