package org.toop;

import org.toop.app.gui.LocalServerSelector;
import org.toop.framework.asset.AssetLoader;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.events.AssetEvents;
import org.toop.framework.asset.resources.TextAsset;
import org.toop.framework.audio.SoundManager;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;

import java.nio.file.NotDirectoryException;

public class Main {
    static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);

        new EventFlow().listen(Main::loadingHandler);

        AssetManager.loadAssets(new AssetLoader("app/src/main/resources/assets"));
        var text = AssetManager.getAllOfType(TextAsset.class).getFirst().getResource();
        var jpg = AssetManager.getByName("background.jpg");

        System.out.println(jpg.getResource().getFile());

        text.load();

        IO.println(text.getContent());

        new Thread(NetworkingClientManager::new).start();
        new Thread(SoundManager::new).start();

        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu.wav", true)).asyncPostEvent();
//        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("sadtrombone.wav", false)).asyncPostEvent();
//        Thread.sleep(200);
//        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu.wav", false)).asyncPostEvent();
//        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("sadtrombone.wav", false)).asyncPostEvent();
//        Thread.sleep(200);
//        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu.wav", false)).asyncPostEvent();
//        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("sadtrombone.wav", false)).asyncPostEvent();
    }

    private static void loadingHandler(AssetEvents.LoadingProgressUpdate update) {
        int loaded = update.hasLoadedAmount();
        int total = update.isLoadingAmount();
        double percent = (total == 0) ? 100.0 : (loaded * 100.0 / total);
    }

    private static void initSystems() throws NetworkingInitializationException, NotDirectoryException {
    }
}
