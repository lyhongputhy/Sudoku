package sudoku;

import java.io.*;
import java.nio.file.*;

public class ProfileManager {
    private final Path folder = Paths.get(System.getProperty("user.home"), ".sudoku-java-project");

    public PlayerProfile loadOrCreate(String name) {
        try {
            Files.createDirectories(folder);
            Path file = folder.resolve(safeFileName(name) + ".profile");
            PlayerProfile profile = new PlayerProfile(name);
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file)) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        try { profile.putBestTime(parts[0], Long.parseLong(parts[1])); }
                        catch (NumberFormatException ignored) { }
                    }
                }
            }
            return profile;
        } catch (IOException e) {
            return new PlayerProfile(name);
        }
    }

    public void save(PlayerProfile profile) {
        try {
            Files.createDirectories(folder);
            Path file = folder.resolve(safeFileName(profile.getName()) + ".profile");
            StringBuilder text = new StringBuilder();
            for (var entry : profile.getBestTimes().entrySet())
                text.append(entry.getKey()).append('=').append(entry.getValue()).append(System.lineSeparator());
            Files.writeString(file, text.toString());
        } catch (IOException ignored) { }
    }

    private String safeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
