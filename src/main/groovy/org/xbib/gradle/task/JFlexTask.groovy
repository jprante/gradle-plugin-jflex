package org.xbib.gradle.task

import jflex.Main
import jflex.exceptions.GeneratorException
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopActionException
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
        def flexFiles = project.fileTree(dir: source, include: '**/*.jflex')

        if (flexFiles.filter { !it.directory }.empty) {
            logger.warn("no flex files found")
        } else {
            flexFiles.visit { FileVisitDetails file ->
                if (file.isDirectory()) {
                    return
                }

                try {
                    def args = ['-q', file.file.absolutePath,
                                '-d', project.file("$generateDir/${file.relativePath.parent}").absolutePath + '/'] as String[]

                    logger.debug "running jflex $args"

                    Main.generate(args)

                    logger.info "Java code generated from JFlex file : $file.relativePath"
                } catch (GeneratorException e) {
                    logger.error("JFlex $e.message", e)
                    throw new StopActionException('error occurred during JFlex code generation')
                }
            }
        }
    }
}
