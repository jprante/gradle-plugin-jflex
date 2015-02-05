package org.xbib.gradle.plugin.jflex;

import groovy.lang.Closure;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.util.ConfigureUtil;

public class JFlexVirtualSourceDirectoryImpl implements JFlexVirtualSourceDirectory {

    private final SourceDirectorySet jflex;

    public JFlexVirtualSourceDirectoryImpl(final String parentDisplayName, FileResolver fileResolver) {
        final String displayName = String.format("%s JFlex source", parentDisplayName);
        this.jflex = new DefaultSourceDirectorySet(displayName, fileResolver);
        this.jflex.getFilter().include("**/*.jflex");
    }

    @Override
    public SourceDirectorySet getJflex() {
        return jflex;
    }

    @Override
    public JFlexVirtualSourceDirectory jflex(Closure closure) {
        ConfigureUtil.configure(closure, getJflex());
        return this;
    }
}
