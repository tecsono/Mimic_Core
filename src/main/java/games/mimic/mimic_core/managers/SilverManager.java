package games.mimic.mimic_core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;

public class SilverManager {
    private final File file;
    private JsonObject data;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SilverManager(File file) {
        this.file = file;
        load();
    }

    private void load() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                data = new JsonObject();
                save();
            } else if (file.length() == 0) {
                data = new JsonObject();
                save();
            } else {
                data = gson.fromJson(new FileReader(file), JsonObject.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            data = new JsonObject();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSilver(UUID playerId) {
        return data.has(playerId.toString()) ? data.get(playerId.toString()).getAsInt() : 0;
    }

    public void setSilver(UUID playerId, int amount) {
        data.addProperty(playerId.toString(), amount);
        save();
    }

    public void addSilver(UUID playerId, int amount) {
        setSilver(playerId, getSilver(playerId) + amount);
    }

    public void subtractSilver(UUID playerId, int amount) {
        setSilver(playerId, Math.max(0, getSilver(playerId) - amount));
    }
}
