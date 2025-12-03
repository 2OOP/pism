package org.toop;

import org.toop.app.App;
import org.toop.framework.audio.*;
import org.toop.framework.networking.NetworkingClientEventListener;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.resource.ResourceLoader;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.MusicAsset;
import org.toop.framework.resource.resources.SoundEffectAsset;

public final class Main {
    static void main(String[] args) {
        App.run(args);
    }
}
