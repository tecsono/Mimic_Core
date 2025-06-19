package games.mimic.mimic_core.managers;

import games.mimic.mimic_core.Mimic_core;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion {

    private final Mimic_core plugin;

    public PlaceholderManager(Mimic_core plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "mimic"; //
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Mimic"; //
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0"; //
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }
        if (params.equals("silver")) {
            int silver = plugin.getSilverManager().getSilver(player.getUniqueId());
            return String.valueOf(silver);
        }
        return null;
    }
}
