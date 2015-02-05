package org.xbib.gradle.plugin.jflex;

import jflex.Main;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JFlexTask extends SourceTask {

    private FileCollection fileCollection;

    private File outputDirectory;

    @TaskAction
    public void generate() throws Exception {
        for (final File sourceFile : getSource().getFiles()) {
            File outDir = findOutdir(sourceFile.getAbsolutePath(), getOutputDirectory().getAbsolutePath());
            outDir.mkdirs();
            Main.generate(new String[]{
                    "-q", sourceFile.getAbsolutePath(),
                    "-d", outDir.getAbsolutePath()
            });
        }
    }

    @InputFiles
    public FileCollection getJflexClasspath() {
        return fileCollection;
    }

    public void setJflexClasspath(final FileCollection fileCollection) {
        this.fileCollection = fileCollection;
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Find output directory and add package name, if given
     */
    private File findOutdir(String infile, String outdir) throws IOException {
        File destDir;
        if (outdir != null) {
            String packageName = findPackage(infile);
            if (packageName == null) {
                destDir = new File(outdir);
            } else {
                destDir = new File(outdir, packageName.replace('.', File.separatorChar));
            }
        } else {
            destDir = new File(new File(infile).getParent());
        }
        return destDir;
    }

    /**
     * Peek into .jflex file to get package name
     *
     * @throws java.io.IOException if there is a problem reading the .jflex file
     */
    private String findPackage(String infile) throws IOException {
        String packageName = null;
        BufferedReader reader = new BufferedReader(new FileReader(infile));
        String line;
        while (packageName == null) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            int index = line.indexOf("package");
            if (index >= 0) {
                packageName = line.substring(index + 7, line.length() - 1).trim();
            }
        }
        return packageName;
    }
}
