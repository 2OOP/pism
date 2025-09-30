package org.toop.framework.assets.resources;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class TextResource extends Resource implements ResourceType<TextResource> {

    TextResource(File file) {
        super(file);
    }

    public TextResource load() {
        return this;
    }

}
