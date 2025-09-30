package org.toop.framework.assets.resources;

import java.io.File;

public class FontResource extends Resource implements ResourceType<FontResource> {

    public FontResource(File fontFile) {
        super(fontFile);
    }

    public FontResource load() {
        return this;
    }
}
