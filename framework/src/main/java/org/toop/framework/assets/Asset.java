package org.toop.framework.assets;

import org.toop.framework.SnowflakeGenerator;

import java.nio.file.Path;

public class Asset {
    private Long id;
    private String name;
    private Path assetPath;
    private String asset;

    public Asset(String name, Path assetPath) {
        this.id = new SnowflakeGenerator().nextId();
        this.name = name;
        this.assetPath = assetPath;
    }

    private void loadAsset() {
        java.nio.file.Path
        this.asset = this.assetPath;
    }

}
