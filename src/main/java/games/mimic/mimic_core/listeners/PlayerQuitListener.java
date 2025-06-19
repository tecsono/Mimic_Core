package games.mimic.mimic_core.listeners;

import games.mimic.mimic_core.LabyrinthGameManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

public class PlayerQuitListener implements Listener {
    private final LabyrinthGameManager labyrinthGameManager;
    private final JavaPlugin plugin;

    public PlayerQuitListener(JavaPlugin plugin, LabyrinthGameManager labyrinthGameManager) {
        this.plugin = plugin;
        this.labyrinthGameManager = labyrinthGameManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("labyrinth_name") && player.hasMetadata("labyrinth_role")) {
            String labyrinth = player.getMetadata("labyrinth_name").get(0).asString();
            String role = player.getMetadata("labyrinth_role").get(0).asString();

            // Remove metadata safely
            player.removeMetadata("labyrinth_name", plugin);
            player.removeMetadata("labyrinth_role", plugin);

            // Reset the join block
            Location joinLocation = labyrinthGameManager.getJoinBlock(labyrinth, role);
            Block joinBlock = joinLocation.getBlock();
            joinBlock.setType(Material.SEA_LANTERN);
        }
    }
}
