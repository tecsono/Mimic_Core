package games.mimic.mimic_core.utils;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class MetadataUtils {
    public static String getString(Player player, String key) {
        if (player.hasMetadata(key)) {
            return player.getMetadata(key).get(0).asString();
        }
        return null;
    }

    public static void set(Player player, String key, Object value, Plugin plugin) {
        player.setMetadata(key, new FixedMetadataValue(plugin, value));
    }

    public static void remove(Player player, String key, Plugin plugin) {
        player.removeMetadata(key, plugin);
    }
}
