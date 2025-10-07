package org.toop.framework.asset.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.toop.framework.asset.types.FileExtension;
import org.toop.framework.asset.types.LoadableResource;

@FileExtension({"json"})
public class JsonAsset<T> extends BaseResource implements LoadableResource {

    private T content;
    private Class<T> type;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonAsset(File file, Class<T> type) {
        super(file);
        this.type = type;
    }

    @Override
    public void load() {
        File file = getFile();
        if (!file.exists()) {
            try {
                // make a new file with the declared constructor (example: settings) if it doesn't
                // exist
                content = type.getDeclaredConstructor().newInstance();
                save();
            } catch (Exception e) {
                throw new RuntimeException("Could not make default JSON settings for" + file, e);
            }
        } else {
            // else open the file, try reading it using gson, and set it to loaded
            try (FileReader reader = new FileReader(file)) {
                content = gson.fromJson(reader, type);
                this.isLoaded = true;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load JSON asset" + getFile(), e);
            }
        }
    }

    @Override
    public void unload() {
        this.content = null;
        this.isLoaded = false;
    }

    public T getContent() {
        if (!isLoaded()) {
            load();
        }
        return content;
    }

    public void save() {
        File file = getFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean isDirectoryMade = parent.mkdirs();
            assert isDirectoryMade;
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(content, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save JSON asset" + getFile(), e);
        }
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }
}
