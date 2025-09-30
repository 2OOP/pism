package org.toop.framework.asset.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class FontAsset extends BaseResource implements LoadableResource {

    private boolean isLoaded = false;

    public FontAsset(final File fontFile) {
        super(fontFile);
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
