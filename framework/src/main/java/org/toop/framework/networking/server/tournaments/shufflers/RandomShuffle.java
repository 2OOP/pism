package org.toop.framework.networking.server.tournaments.shufflers;

import java.util.List;
import java.util.Random;

public class RandomShuffle implements Shuffler {
    @Override
    public <T> void shuffle(List<T> listToShuffle) {
        final int SHUFFLE_AMOUNT = listToShuffle.size() * 2;

        Random rand = new Random();

        for (int i = 0; i <= SHUFFLE_AMOUNT; i++) {
            int index = rand.nextInt(listToShuffle.size());
            T match = listToShuffle.get(index);
            listToShuffle.remove(index);
            listToShuffle.addLast(match);
        }
    }
}
