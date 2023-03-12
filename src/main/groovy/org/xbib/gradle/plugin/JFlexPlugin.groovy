package org.xbib.gradle.plugin

import org.gradle.api.file.Directory
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

class JFlexPlugin implements Plugin<Project> {

    private static final Logger logger = Logging.getLogger(JFlexPlugin)

    @Override
    void apply(Project project) {
        logger.info "JFlex plugin says hello"
        def extension = createJflexExtension(project)
        project.with {
            apply plugin: 'java-library'
            addSourceSetExtensions(project)
        }
        addJFlexTasks(project, extension)
    }

    private static void addSourceSetExtensions(Project project) {
        project.extensions.getByType(SourceSetContainer).all { SourceSet sourceSet ->
            createSourceSetExtension(project, sourceSet)
            createConfiguration(project, sourceSet)
        }
    }

    private static void createSourceSetExtension(Project project, SourceSet sourceSet) {
        String name = sourceSet.name
        SourceDirectorySet sourceDirectorySet = project.objects.sourceDirectorySet(name, "${name} JFlex source")
        sourceSet.extensions.add('jflex', sourceDirectorySet)
        sourceDirectorySet.srcDir("src/${name}/jflex")
        sourceDirectorySet.include("**/*.jflex")
    }

    private static void createConfiguration(Project project, SourceSet sourceSet) {
        String configName = sourceSet.name + capitalize('jflex' as CharSequence)
        if (project.configurations.findByName(configName) == null) {
            logger.info "create configuration ${configName}"
            project.configurations.create(configName) {
                visible = false
                transitive = true
                extendsFrom = []
            }
        }
    }

    private static JFlexExtension createJflexExtension(Project project) {
        project.extensions.create ('jflex', JFlexExtension)
    }

    private static void addJFlexTasks(Project project, JFlexExtension jFlexExtension) {
        project.extensions.getByType(SourceSetContainer).all { SourceSet sourceSet ->
            addJFlexTaskForSourceSet(project, sourceSet, jFlexExtension)
        }
    }

    private static void addJFlexTaskForSourceSet(Project project, SourceSet sourceSet, JFlexExtension jFlexExtension) {
        String taskName = sourceSet.getTaskName('generate', 'jflex')
        SourceDirectorySet sourceDirectorySet = sourceSet.extensions.getByName('jflex') as SourceDirectorySet
        Provider<Directory> defaultTargetFile = project.layout.buildDirectory.dir("generated/sources/${sourceSet.name}")

        Provider<File> targetFile = jFlexExtension.writeIntoJavaSrc.map {
            if (it) {
                if (sourceSet.java && sourceSet.java.srcDirs) {
                    logger.info "java sources: ${sourceSet.java.srcDirs}"
                    File target = sourceSet.java.srcDirs.first()
                    logger.info "switching to first java source directory ${target}"
                    return target
                } else {
                    logger.warn "writing into java source not possible, is empty"
                    return defaultTargetFile.get().asFile
                }
            } else {
                return defaultTargetFile.get().asFile
            }
        }
        TaskProvider<JFlexTask> taskProvider = project.tasks.register(taskName, JFlexTask) {
            group = 'jflex'
            description = 'Generates code from JFlex files in ' + sourceSet.name
            source = sourceDirectorySet
            target.fileProvider(targetFile)

            encoding.set(jFlexExtension.encoding)
            rootDirectory.set(jFlexExtension.rootDirectory)
            skel.set(jFlexExtension.skel)
            verbose.set(jFlexExtension.verbose)
            jlex.set(jFlexExtension.jlex)
            no_minimize.set(jFlexExtension.no_minimize)
            no_backup.set(jFlexExtension.no_backup)
            unused_warning.set(jFlexExtension.unused_warning)
            progress.set(jFlexExtension.progress)
            time.set(jFlexExtension.time)
            dot.set(jFlexExtension.dot)
            dump.set(jFlexExtension.dump)
            legacy_dot.set(jFlexExtension.legacy_dot)
            statistics.set(jFlexExtension.statistics)
            writeIntoJavaSrc.set(jFlexExtension.writeIntoJavaSrc)

        }
        logger.info "created ${taskName} for sources ${sourceDirectorySet.asList()} and target ${targetFile}"
        project.tasks.named(sourceSet.compileJavaTaskName).configure({
            dependsOn taskProvider
        })
        if (sourceSet.java && sourceSet.java.srcDirs) {
            sourceSet.java.srcDirs += defaultTargetFile
        }
    }

    private static String capitalize(CharSequence charSequence) {
        return charSequence.length() == 0 ? "" : "" + Character.toUpperCase(charSequence.charAt(0)) + charSequence.subSequence(1, charSequence.length())
    }
}
