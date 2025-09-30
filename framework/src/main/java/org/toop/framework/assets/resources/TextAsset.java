package org.toop.framework.assets.resources;

import org.w3c.dom.Text;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
