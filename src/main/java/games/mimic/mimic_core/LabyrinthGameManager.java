package games.mimic.mimic_core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import games.mimic.mimic_core.managers.BlockLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;

public class LabyrinthGameManager {
    private final File file;
    private JsonObject data;
    private final Gson gson;

    private final BlockLoader loader;
    private final BlockEffectHandler blockEffectHandler;

    public LabyrinthGameManager(File file, BlockLoader loader, BlockEffectHandler blockEffectHandler) {
        this.file = file;
        this.loader = loader;
        this.blockEffectHandler = blockEffectHandler;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    // === FILE HANDLING ===

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
                data = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
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

    // === LABYRINTH SETUP ===

    public boolean createLabyrinth(String name) {
        if (data.has(name)) return false;
        data.add(name, new JsonObject());
        save();
        return true;
    }

    public boolean deleteLabyrinth(String name) {
        if (!data.has(name)) return false;
        data.remove(name);
        save();
        return true;
    }

    public boolean labyrinthExists(String name) {
        return data.has(name);
    }

    public Set<String> listLabyrinths() {
        return data.keySet();
    }

    public boolean setSpawn(String name, String role, Location loc) {
        if (!data.has(name)) return false;

        JsonObject lab = data.getAsJsonObject(name);
        JsonObject locObj = new JsonObject();
        locObj.addProperty("world", loc.getWorld().getName());
        locObj.addProperty("x", loc.getX());
        locObj.addProperty("y", loc.getY());
        locObj.addProperty("z", loc.getZ());
        locObj.addProperty("yaw", loc.getYaw());
        locObj.addProperty("pitch", loc.getPitch());

        lab.add(role.toLowerCase() + "_spawn", locObj);
        save();
        return true;
    }

    public boolean setJoinBlock(String name, String role, Location loc) {
        if (!data.has(name)) return false;

        JsonObject lab = data.getAsJsonObject(name);
        JsonObject blockObj = new JsonObject();
        blockObj.addProperty("world", loc.getWorld().getName());
        blockObj.addProperty("x", loc.getBlockX());
        blockObj.addProperty("y", loc.getBlockY());
        blockObj.addProperty("z", loc.getBlockZ());

        lab.add(role.toLowerCase() + "_join_block", blockObj);
        save();
        return true;
    }

    public void setWinnings(String name, int amount) {
        if (!data.has(name)) return;
        JsonObject lab = data.getAsJsonObject(name);
        lab.addProperty("winnings", amount);
        save();
    }

    public int getWinnings(String name) {
        if (!data.has(name)) return 0;
        JsonObject lab = data.getAsJsonObject(name);
        return lab.has("winnings") ? lab.get("winnings").getAsInt() : 0;
    }

    // === LOCATION GETTERS ===

    public Location getSpawn(String name, String role) {
        if (!data.has(name)) return null;
        JsonObject lab = data.getAsJsonObject(name);
        JsonObject locObj = lab.getAsJsonObject(role.toLowerCase() + "_spawn");
        if (locObj == null) return null;

        World world = Bukkit.getWorld(locObj.get("world").getAsString());
        if (world == null) return null;

        return new Location(
                world,
                locObj.get("x").getAsDouble(),
                locObj.get("y").getAsDouble(),
                locObj.get("z").getAsDouble(),
                locObj.get("yaw").getAsFloat(),
                locObj.get("pitch").getAsFloat()
        );
    }

    public Location getJoinBlock(String name, String role) {
        if (!data.has(name)) return null;
        JsonObject lab = data.getAsJsonObject(name);
        JsonObject locObj = lab.getAsJsonObject(role.toLowerCase() + "_join_block");
        if (locObj == null) return null;

        World world = Bukkit.getWorld(locObj.get("world").getAsString());
        if (world == null) return null;

        return new Location(
                world,
                locObj.get("x").getAsInt(),
                locObj.get("y").getAsInt(),
                locObj.get("z").getAsInt()
        );
    }

    // === GAME LOGIC ===

    public boolean isInLabyrinth(Player player) {
        return player.hasMetadata("labyrinth_name") && player.hasMetadata("labyrinth_role");
    }

    public void handlePlayerMove(Player player, Block below) {
        Material firstLayer = below.getType();
        Material secondLayer = below.getRelative(BlockFace.DOWN).getType();

        if (loader.firstLayerBlocks.contains(firstLayer) && loader.secondLayerBlocks.contains(secondLayer)) {
            blockEffectHandler.applyEffect(player, secondLayer);
        }
    }
}
