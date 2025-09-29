package org.toop.framework.audio;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class AudioFiles {

    private Set<String> audioFiles;
    private String audioDirectory;

    public AudioFiles(String audioDirectory) throws NotDirectoryException {
        if (!audioDirectory.endsWith("/") && !audioDirectory.endsWith("\\")) {
            throw new NotDirectoryException(audioDirectory);
        }
        this.audioFiles = AudioFiles.getAudioFiles(audioDirectory);
        this.audioDirectory = audioDirectory;
    }

    public Set<String> getAudioFiles() {
        return this.audioFiles;
    }

    public String getAudioDirectory() {
        return this.audioDirectory;
    }

    public String getAudioFile(String file) {
        if (!audioFiles.contains(file)) {
            return null;
        }
        return audioDirectory + file;
    }

    public static String removeAllKeepOnlyName(String fileDirectoryNameExtension) {
        String withoutExtensionAndDirectory = "";

        int i = fileDirectoryNameExtension.lastIndexOf('.');
        if (i > 0) {
            withoutExtensionAndDirectory = fileDirectoryNameExtension.substring(0, i);
        }
        int y = withoutExtensionAndDirectory.lastIndexOf("/");
        int k = withoutExtensionAndDirectory.lastIndexOf("\\");
        if (y > 0) {
            withoutExtensionAndDirectory = withoutExtensionAndDirectory.substring(y+1);
        } else if (k > 0) {
            withoutExtensionAndDirectory = withoutExtensionAndDirectory.substring(k+1);
        }
        return withoutExtensionAndDirectory;
    }

    private static Set<String> getAudioFiles(String audioDirectory) {
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
