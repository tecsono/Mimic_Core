package games.mimic.mimic_core.listeners;

import games.mimic.mimic_core.JoinHandler;
import games.mimic.mimic_core.LabyrinthGameManager;
import games.mimic.mimic_core.Mimic_core;
import games.mimic.mimic_core.managers.BlockLoader;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveEventListener implements Listener {
    private final BlockLoader blockLoader;
    private final LabyrinthGameManager labyrinthGameManager;
    private final Mimic_core plugin;
    private final JoinHandler joinHandler;

    public PlayerMoveEventListener(BlockLoader blockLoader, LabyrinthGameManager labyrinthGameManager, JoinHandler joinHandler, Mimic_core plugin) {
        this.blockLoader = blockLoader;
        this.labyrinthGameManager = labyrinthGameManager;
        this.joinHandler = joinHandler;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (hasNotMoved(event)) return;

        Block below = event.getTo().getBlock().getRelative(BlockFace.DOWN);

        if (player.hasMetadata("nomove")) {
            event.setTo(event.getFrom());
            return;
        }

        if (labyrinthGameManager.isInLabyrinth(player)) {
            labyrinthGameManager.handlePlayerMove(player, below);
        } else {
            joinHandler.handleJoinAttempt(player, below);
        }
    }

    private boolean hasNotMoved(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ();
    }
}