package org.xbib.gradle.plugin

import org.xbib.gradle.task.JFlexTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class JFlexPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.with {
            apply plugin: 'java'
            tasks.create(name: 'jflex', type: JFlexTask)
            tasks.compileJava.dependsOn tasks.jflex
            sourceSets.main.java.srcDirs += tasks.jflex.generateDir
        }
    }
}
