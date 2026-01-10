package org.toop.framework.networking.server.tournaments.shufflers;

import java.util.List;

public interface Shuffler {
    <T> void shuffle(List<T> listToShuffle);
}
