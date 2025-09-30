package org.toop.framework.asset.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class TextAsset extends BaseResource implements LoadableResource {

    TextAsset(final File file) {
        super(file);
    }

    @Override
    public void load() throws FileNotFoundException {

    }

    @Override
    public void unload() {

    }

    @Override
    public boolean isLoaded() {
        return false;
    }
}
