# gradle-plugin-jflex

A Gradle plugin for [JFlex](http://jflex.de)

## Compatibility

| Date | Plugin | Gradle | JFLex |
|----- | ------ | ------ | ----- |
| 2020-05-13 | 1.4.0 | 6.4 | 1.8.2 |

## Usage

    plugins {
       id "org.xbib.gradle.plugin.jflex" version "1.4.0"
    }

Gradle will look for your JFlex files in the source sets you specified.
By default, it looks with the pattern `**/*.jflex` under `src/main/jflex`
and `src/test/jflex`.

You can set up the source sets like this:

    sourceSets {
      main {
         jflex {
           srcDir "src/main/jflex"
         }
         java {
           srcDir "$buildDir/my-generated-sources/jflex"
         }
      }
    }
    
The lastJava `srcDir` definition will be used as the base for the JFLex target path.
If not given, the JFlex target path for generated Java source follows the pattern:

`${project.buildDir}/generated/sources/jflex`

The JFlex target path will be added automatically to the java compile task source directory 
of the source set.

## Parameter support

The following parameters can be set in a global `jflex` extension 
in the gradle script. See also https://jflex.de/manual.html

| Name    | Description |
| ------- | ---------- |
| encoding | the file encoding |
| rootDirectory | the root directory used by JFlex (modification discouraged since the directories are derived from gradle source set)
| skel | uses external skeleton <file> in UTF-8 encoding. This is mainly for JFlex maintenance and special low level customisations. Use only when you know what you are doing! JFlex comes with a skeleton file in the src directory that reflects exactly the internal, pre-compiled skeleton and can be used with the skel option. |
| verbose | display generation progress messages (disabled by default) |
| jlex | tries even harder to comply to JLex interpretation of specs |
| no_minimize | skip the DFA minimisation step during scanner generation |
| no_backup | don't write backup files if this is true |
| unused_warning | warn about unused macros (by default false) |
| progress |  progress dots will be printed (by default false) |
| dot | If true, jflex will write graphviz .dot files for generated automata (by default, false) |
| time | If true, jflex will print time statistics about the generation process (by default false) |
| dump | If true, you will be flooded with information, e.g. dfa tables (by default, false) |
| legacy_dot | dot (.) meta character matches [^\n] instead of [^\n\r\u000B\u000C\u0085\u2028\u2029] |
| statistics | print output statistics (by default, false) |

## Credits

gradle-plugin-jflex is a plugin based on
[gradle-jflex-plugin](https://github.com/thomaslee/gradle-jflex-plugin)
which was written by [Tom Lee](http://tomlee.co).

# License

Copyright (C) 2015-2020 Jörg Prante

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

