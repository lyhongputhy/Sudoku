package sudoku;

import java.util.*;

public class PlayerProfile {
    private final String name;
    private final Map<String, Long> bestTimes = new HashMap<>();

    public PlayerProfile(String name) { this.name = name; }
    public String getName() { return name; }

    public Long getBestTime(String difficulty) { return bestTimes.get(difficulty); }

    public boolean updateBestTime(String difficulty, long seconds) {
        Long old = bestTimes.get(difficulty);
        if (old == null || seconds < old) {
            bestTimes.put(difficulty, seconds);
            return true;
        }
        return false;
    }

    public Map<String, Long> getBestTimes() { return Collections.unmodifiableMap(bestTimes); }
    public void putBestTime(String difficulty, long seconds) { bestTimes.put(difficulty, seconds); }
}
