package name.ulbricht.led;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public final class Settings {

    private static final String ROOT = "name/ulbricht/led/2.1";

    private static final int MAX_MRU_COUNT = 10;
    private static final String MRU = ROOT + "/mru";
    private static final String MRU_OPEN_DIR = "openDir";
    private static final String MRU_SAVE_DIR = "saveDir";

    public static List<Path> getMRUFiles() {
        List<Path> files = new ArrayList<>();

        Preferences node = Preferences.userRoot().node(MRU);
        for (int i = 0; i < MAX_MRU_COUNT; i++) {
            String fileName = node.get(Integer.toString(i), null);
            if (fileName != null) {
                try {
                    Path file = Paths.get(fileName);
                    if (Files.exists(file)) {
                        files.add(file);
                    }
                } catch (InvalidPathException ex) {
                    // skip this file
                }
            }
        }
        return files;
    }

    public static void addMRUFile(Path file) {
        List<Path> files = getMRUFiles();
        int idx = files.indexOf(file);
        if (idx > 0) {
            files.remove(idx);
            files.add(0, file);
        } else if (idx < 0) {
            files.add(0, file);
        }

        Preferences node = Preferences.userRoot().node(MRU);
        for (int i = 0; i < MAX_MRU_COUNT; i++) {
            if (i < files.size()) {
                node.put(Integer.toString(i), files.get(i).toString());
            } else {
                node.remove(Integer.toString(i));
            }
        }
    }

    public static Path getMRUOpenDir() {
        return getPath(Preferences.userRoot().node(MRU), MRU_OPEN_DIR);
    }

    public static void setMRUOpenDir(Path dir) {
        Preferences node = Preferences.userRoot().node(MRU);
        node.put(MRU_OPEN_DIR, dir.toString());
    }

    public static Path getMRUSaveDir() {
        return getPath(Preferences.userRoot().node(MRU), MRU_SAVE_DIR);
    }

    public static void setMRUSaveDir(Path dir) {
        Preferences node = Preferences.userRoot().node(MRU);
        node.put(MRU_SAVE_DIR, dir.toString());
    }

    private static Path getPath(Preferences node, String key) {
        String dir = node.get(key, null);
        if (dir != null) {
            try {
                return Paths.get(dir);
            } catch (InvalidPathException ex) {
                // fall-back to default path
            }
        }
        return getDefaultDirectory();
    }

    private static Path getDefaultDirectory() {
        Path homePath = Paths.get(System.getProperty("user.home"));

        if (System.getProperty("os.name").startsWith("Windows")) {
            Path docPath = homePath.resolve("Documents");
            if (Files.exists(docPath)) return docPath;
        }

        return homePath;
    }

    private Settings() {
        // hidden
    }
}
