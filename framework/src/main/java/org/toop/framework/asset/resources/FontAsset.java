package org.toop.framework.asset.resources;

import java.io.File;

public class FontAsset extends BaseResource implements LoadableResource {

    public FontAsset(final File fontFile) {
        super(fontFile);
    }

    @Override
    public void load() {
        this.isLoaded = true;
    }

    @Override
    public void unload() {
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }
}
