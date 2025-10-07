package org.toop.framework.asset.resources;

import java.io.File;
import org.toop.framework.asset.types.FileExtension;

@FileExtension({"css"})
public class CssAsset extends BaseResource {
    private final String url;

    public CssAsset(File file) {
        super(file);
        this.url = file.toURI().toString();
    }

    public String getUrl() {
        return url;
    }
}
