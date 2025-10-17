package org.toop.framework.audio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.resource.ResourceMeta;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.types.AudioResource;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SoundEffectManager.
 */
class MockSoundEffectResource extends BaseResource implements AudioResource {
    boolean played = false;
    boolean stopped = false;

    public MockSoundEffectResource(String name) {
        super(new File(name));
    }

    @Override
    public String getName() {
        return getFile().getName();
    }

    @Override
    public void play() {
        played = true;
    }

    @Override
    public void stop() {
        stopped = true;
    }

    @Override
    public void setOnEnd(Runnable callback) {}

    @Override
    public void setOnError(Runnable callback) {}

    @Override
    public void updateVolume(double volume) {}
}

public class SoundEffectManagerTest {

    private SoundEffectManager<MockAudioResource> manager;
    private MockAudioResource sfx1;
    private MockAudioResource sfx2;
    private MockAudioResource sfx3;

    @BeforeEach
    void setUp() {
        sfx1 = new MockAudioResource("explosion.wav");
        sfx2 = new MockAudioResource("laser.wav");
        sfx3 = new MockAudioResource("jump.wav");

        List<ResourceMeta<MockAudioResource>> resources = List.of(
                new ResourceMeta<>("explosion", sfx1),
                new ResourceMeta<>("laser", sfx2),
                new ResourceMeta<>("jump", sfx3)
        );

        manager = new SoundEffectManager<>(resources);
    }

    @Test
    void testPlayValidSound() {
        manager.play("explosion", false);
        assertTrue(sfx1.played, "Sound 'explosion' should be played");
    }

    @Test
    void testPlayInvalidSoundLogsWarning() {
        // Nothing should crash or throw
        assertDoesNotThrow(() -> manager.play("nonexistent", false));
    }

    @Test
    void testStopValidSound() {
        manager.stop("laser");
        assertTrue(sfx2.stopped, "Sound 'laser' should be stopped");
    }

    @Test
    void testStopInvalidSoundDoesNotThrow() {
        assertDoesNotThrow(() -> manager.stop("does_not_exist"));
    }

    @Test
    void testGetActiveAudioReturnsAll() {
        Collection<MockAudioResource> active = manager.getActiveAudio();
        assertEquals(3, active.size(), "All three sounds should be in active audio list");
        assertTrue(active.containsAll(List.of(sfx1, sfx2, sfx3)));
    }

    @Test
    void testDuplicateResourceKeepsLast() {
        MockAudioResource oldRes = new MockAudioResource("duplicate_old.wav");
        MockAudioResource newRes = new MockAudioResource("duplicate_new.wav");

        List<ResourceMeta<MockAudioResource>> list = new ArrayList<>();
        list.add(new ResourceMeta<>("dup", oldRes));
        list.add(new ResourceMeta<>("dup", newRes)); // duplicate key

        SoundEffectManager<MockAudioResource> dupManager = new SoundEffectManager<>(list);
        dupManager.play("dup", false);

        assertTrue(newRes.played, "New duplicate resource should override old one");
        assertFalse(oldRes.played, "Old duplicate resource should be discarded");
    }
}
