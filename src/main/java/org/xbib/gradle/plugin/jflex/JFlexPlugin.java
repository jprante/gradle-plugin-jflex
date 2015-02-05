package org.xbib.gradle.plugin.jflex;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;

import javax.inject.Inject;
import java.io.File;

public final class JFlexPlugin implements Plugin<Project> {
    private final FileResolver fileResolver;

    @Inject
    public JFlexPlugin(final FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(JavaPlugin.class);
        configureConfigurations(project);
        configureSourceSets(project);
    }

    private void configureConfigurations(Project project) {
        final Configuration jflexConfiguration =
                project.getConfigurations().create("jflex").setVisible(false);
        project.getConfigurations().getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(jflexConfiguration);
    }

    private void configureSourceSets(final Project project) {
        project.getConvention()
                .getPlugin(JavaPluginConvention.class).getSourceSets()
                .all(sourceSet -> {
                    JFlexVirtualSourceDirectoryImpl jflexSourceSet =
                            new JFlexVirtualSourceDirectoryImpl(((DefaultSourceSet) sourceSet).getDisplayName(), fileResolver);
                    new DslObject(sourceSet).getConvention().getPlugins().put("jflex", jflexSourceSet);
                    String srcDir = String.format("src/%s/jflex", sourceSet.getName());
                    jflexSourceSet.getJflex().srcDir(srcDir);
                    sourceSet.getAllSource().source(jflexSourceSet.getJflex());
                    String taskName = sourceSet.getTaskName("generate", "JFlexSource");
                    JFlexTask jflexTask = project.getTasks().create(taskName, JFlexTask.class);
                    jflexTask.setDescription(String.format("Processes the %s JFlex files.", sourceSet.getName()));
                    jflexTask.setSource(jflexSourceSet.getJflex());
                    jflexTask.getConventionMapping().map("jflexClasspath",
                            () -> project.getConfigurations().getByName("jflex").copy().setTransitive(true));
                    String outputDirectoryName =
                            String.format("%s/generated-src/jflex/%s", project.getBuildDir(),
                                    sourceSet.getName());
                    File outputDirectory = new File(outputDirectoryName);
                    jflexTask.setOutputDirectory(outputDirectory);
                    sourceSet.getJava().srcDir(outputDirectory);
                    project.getTasks().getByName(sourceSet.getCompileJavaTaskName()).dependsOn(taskName);
                });
    }
}
