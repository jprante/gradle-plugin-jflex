package org.xbib.gradle.plugin.test

import org.gradle.api.logging.Logging
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class JFlexPluginTest {

    private File projectDir

    private File settingsFile

    private File buildFile

    @BeforeEach
    void setup(@TempDir File testProjectDir) throws IOException {
        this.projectDir = testProjectDir
        this.settingsFile = new File(testProjectDir, "settings.gradle")
        this.buildFile = new File(testProjectDir, "build.gradle")
    }

    @Test
    void testJFlex() {
        String settingsFileContent = '''
rootProject.name = 'jflex-test'
'''
        settingsFile.write(settingsFileContent)
        String buildFileContent = '''
plugins {
    id 'org.xbib.gradle.plugin.jflex'
}

sourceSets {
  test {
     jflex {
       srcDir "${System.getProperty('user.dir')}/src/test/jflex"
     }
     java {
       srcDir "${System.getProperty('user.dir')}/build/my-generated-sources/jflex"
     }
  }
}

jflex {
   verbose = false
   dump = false
   progress = false
}

'''
        buildFile.write(buildFileContent)
        BuildResult result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(":build", "--info")
                .withPluginClasspath()
                .forwardOutput()
                .build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":build").getOutcome())

        File file = new File("${System.getProperty('user.dir')}/build/my-generated-sources/jflex")
        if (file.exists()) {
            List<File> list = Arrays.asList(file.listFiles())
            assertEquals(1, list.size())
        }
    }
}
