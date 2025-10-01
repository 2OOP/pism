package org.toop.framework.asset.resources;

import java.io.File;

public interface BundledResource {
    /**
     * Load or merge an additional file into this resource.
     */
    void loadFile(File file);

    /**
     * Return a base name for grouping multiple files into this single resource.
     */
    String getBaseName();
}