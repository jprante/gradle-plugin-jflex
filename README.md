# gradle-plugin-jflex

A Gradle plugin for [JFlex](http://jflex.de)

## Usage

    buildscript {
        repositories {
            mavenCentral()
            maven {
                url "http://xbib.org/repository"
            }
        }
        dependencies {
            classpath 'org.xbib.gradle.plugins:gradle-plugin-jflex:1.1.0'
        }
    }

    apply plugin: 'jflex'

Gradle will look for your JFlex files in `src/main/jflex/**/*.jflex`.

## Credits

gradle-plugin-jflex is a plugin based on
[gradle-jflex-plugin](https://github.com/thomaslee/gradle-jflex-plugin)
which was written by [Tom Lee](http://tomlee.co).

# License

Copyright (C) 2015 Jörg Prante

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

