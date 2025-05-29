package games.mimic.mimic_core;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.bukkit.Material;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class BlockLoader {

    public Set<Material> firstLayerBlocks = new HashSet<>();
    public Set<Material> secondLayerBlocks = new HashSet<>();

    public void loadBlockLists(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray firstLayerArray = json.getAsJsonArray("firstLayerBlocks");
            JsonArray secondLayerArray = json.getAsJsonArray("secondLayerBlocks");

            for (int i = 0; i < firstLayerArray.size(); i++) {
                Material mat = Material.matchMaterial(firstLayerArray.get(i).getAsString());
                if (mat != null) firstLayerBlocks.add(mat);
            }

            for (int i = 0; i < secondLayerArray.size(); i++) {
                Material mat = Material.matchMaterial(secondLayerArray.get(i).getAsString());
                if (mat != null) secondLayerBlocks.add(mat);
            }

            System.out.println("Loaded " + firstLayerBlocks.size() + " first layer blocks.");
            System.out.println("Loaded " + secondLayerBlocks.size() + " second layer blocks.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}