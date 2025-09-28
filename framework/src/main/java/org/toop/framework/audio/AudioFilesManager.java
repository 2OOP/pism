package org.toop.framework.audio;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class AudioFilesManager {

    private Set<String> audioFiles;
    private String audioDirectory;

    public AudioFilesManager(String audioDirectory) throws NotDirectoryException {
        if (!audioDirectory.endsWith("/") && !audioDirectory.endsWith("\\")) {
            throw new NotDirectoryException(audioDirectory);
        }
        this.audioFiles = AudioFilesManager.getAllAudioFiles(audioDirectory);
        this.audioDirectory = audioDirectory;
    }

    public Set<String> getAudioFiles() {
        return this.audioFiles;
    }

    public String getAudioFile(String file) {
        if (!audioFiles.contains(file)) {
            return null;
        }
        return audioDirectory + file;
    }

    private static Set<String> getAllAudioFiles(String audioDirectory) {
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(audioDirectory))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String extension = "";

                    int i = path.getFileName().toString().lastIndexOf('.');
                    if (i > 0) {
                        extension = path.getFileName().toString().substring(i+1);
                    }
                    if (extension.equalsIgnoreCase("wave") || extension.equalsIgnoreCase("wav")
                    ||  extension.equalsIgnoreCase("mp3"))
                        fileSet.add(path.getFileName()
                            .toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileSet;
    }
}
