package org.toop.framework.audio;

import org.toop.framework.assets.Asset;
import org.toop.framework.assets.AssetManager;
import org.toop.framework.assets.resources.AudioResource;
import org.toop.framework.assets.resources.Resource;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class SoundManager {
    private javax.sound.sampled.Line.Info lineInfo;

    private final Map<String, Clip> activeClips = new HashMap<>();
    private HashMap<String, Integer> clips = new HashMap<>();
    private AssetManager assetManager;
    private Vector afs;
    private Vector sizes;
    private Vector infos;
    private Vector audios;
    private int num=0;

    public SoundManager(AssetManager ass) {
        afs=new Vector();
        sizes=new Vector();
        infos=new Vector();
        audios=new Vector();
        this.assetManager = ass;
        for (Asset<AudioResource> resource : ass.getAllResourceOfType(AudioResource.class).values()) {
            try {
                addClip(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
        new EventFlow()
                .listen(this::handlePlaySound)
                .listen(this::handleStopSound);
    }

    private void handlePlaySound(AudioEvents.PlayAudio event) {
        this.playSound(event.fileNameNoExtensionAndNoDirectory(), event.loop());
    }

    private void handleStopSound(AudioEvents.StopAudio event) {
        this.stopSound(event.fileNameNoExtensionAndNoDirectory());
    }

    private void addClip(Asset<AudioResource> audiol)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioResource ad = audiol.getResource();
        AudioFormat af = ad.getAudioStream().getFormat();
        int size = (int) (af.getFrameSize() * ad.getAudioStream().getFrameLength());
        byte[] audio = new byte[size];
        DataLine.Info info = new DataLine.Info(Clip.class, af, size);
        ad.getInputStream().read(audio, 0, size);

        afs.add(af);
        sizes.add(new Integer(size));
        infos.add(info);
        audios.add(audio);
        this.clips.put(audiol.getName(), this.audios.size()-1);

        num++;
    }

    private ByteArrayInputStream loadStream(InputStream inputstream)
            throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte data[] = new byte[1024];
        for(int i = inputstream.read(data); i != -1; i = inputstream.read(data))
            bytearrayoutputstream.write(data, 0, i);

        inputstream.close();
        bytearrayoutputstream.close();
        data = bytearrayoutputstream.toByteArray();
        return new ByteArrayInputStream(data);
    }

    private void playSound(String audioFileName, boolean loop) {
        var b = this.assetManager.getAllResourceOfType(AudioResource.class);
        b.get(audioFileName).getResource().getClip().start();
    }

//    private void playSound(String audioFileName, boolean loop)
//            throws UnsupportedAudioFileException, LineUnavailableException {
//        int x = clips.get(audioFileName);
//        if (x > num) {
//            System.out.println("playSound: sample nr[" + x + "] is not available");
//        } else {
//            Clip clip = (Clip) AudioSystem.getLine((DataLine.Info) infos.elementAt(x));
////            clip.open((AudioFormat) afs.elementAt(x), (byte[]) audios.elementAt(x),
////                    0, ((Integer) sizes.elementAt(x)).intValue());
//
//            clip.start();
//            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
//
//            // store it so we can stop it later
//            activeClips.put(audioFileName, clip); // TODO: Do on snowflake for specific sound to stop
//
//            // remove when finished (only for non-looping sounds)
//            clip.addLineListener(event -> {
//                if (event.getType() == LineEvent.Type.STOP && !clip.isRunning()) {
//                    activeClips.remove(audioFileName);
//                    clip.close();
//                }
//            });
//        }
//    }

    public HashMap<String, Integer> getClips() {
        return this.clips;
    }

    public void stopSound(String audioFileName) {
        Clip clip = activeClips.get(audioFileName);
        if (clip != null) {
            clip.stop();
            clip.close();
            activeClips.remove(audioFileName);
        }
    }

    public void stopAllSounds() {
        for (Clip clip : activeClips.values()) {
            clip.stop();
            clip.close();
        }
        activeClips.clear();
    }
}
