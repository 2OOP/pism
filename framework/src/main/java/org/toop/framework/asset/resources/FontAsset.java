package org.toop.framework.asset.resources;

import javafx.scene.text.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@FileExtension({"ttf", "otf"})
public class FontAsset extends BaseResource implements PreloadResource {
    private String family;

    public FontAsset(final File fontFile) {
        super(fontFile);
    }

    @Override
    public void load() {
        if (!this.isLoaded) {
            try (FileInputStream fis = new FileInputStream(this.file)) {
                // Register font with JavaFX
                Font font = Font.loadFont(fis, 12); // Default preview size
                if (font == null) {
                    throw new RuntimeException("Failed to load font: " + this.file);
                }
                this.family = font.getFamily(); // Save family name for CSS / future use
                this.isLoaded = true;
            } catch (IOException e) {
                throw new RuntimeException("Error reading font file: " + this.file, e);
            }
        }
    }

    @Override
    public void unload() {
        // Font remains globally registered with JavaFX, but we just forget it locally
        this.family = null;
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /** Get a new font instance with the given size */
    public Font getFont(double size) {
        if (!this.isLoaded) {
            load();
        }
        return Font.font(this.family, size);
    }

    /** Get the family name (for CSS usage) */
    public String getFamily() {
        if (!this.isLoaded) {
            load();
        }
        return this.family;
    }
}