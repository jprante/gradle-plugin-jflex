package org.xbib.gradle.plugin

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class JFlexExtension {

    @Input
    @Optional
    String encoding

    @Input
    @Optional
    File rootDirectory

    @Input
    @Optional
    File skel

    @Input
    @Optional
    Boolean verbose = false

    @Input
    @Optional
    Boolean jlex = false

    @Input
    @Optional
    Boolean no_minimize = false

    @Input
    @Optional
    Boolean no_backup = false

    @Input
    @Optional
    Boolean unused_warning = false

    @Input
    @Optional
    Boolean progress = false

    @Input
    @Optional
    Boolean time = false

    @Input
    @Optional
    Boolean dot = false

    @Input
    @Optional
    Boolean dump = false

    @Input
    @Optional
    Boolean legacy_dot = false

    @Input
    @Optional
    Boolean statistics = false
}
