package games.mimic.mimic_core;

import games.mimic.mimic_core.LabyrinthGameManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class JoinHandler {
    private final LabyrinthGameManager labyrinthGameManager;
    private final Plugin plugin;

    public JoinHandler(LabyrinthGameManager labyrinthGameManager, Plugin plugin) {
        this.labyrinthGameManager = labyrinthGameManager;
        this.plugin = plugin;
    }

    public void handleJoinAttempt(Player player, Block block) {
        for (String labName : labyrinthGameManager.listLabyrinths()) {
            if (tryJoin(player, labName, "master", block)) return;
            if (tryJoin(player, labName, "subject", block)) return;
        }
    }

    private boolean tryJoin(Player player, String labyrinth, String role, Block block) {
        Location joinLoc = labyrinthGameManager.getJoinBlock(labyrinth, role);
        if (joinLoc != null && isSameBlock(block, joinLoc)) {
            if (block.getType() != Material.SEA_LANTERN) {
                player.sendMessage(ChatColor.RED + "The " + role + " role is already taken.");
            } else {
                joinLabyrinth(player, labyrinth, role, block);
            }
            return true;
        }
        return false;
    }

    private void joinLabyrinth(Player player, String labyrinth, String role, Block block) {
        player.setMetadata("labyrinth_name", new FixedMetadataValue(plugin, labyrinth));
        player.setMetadata("labyrinth_role", new FixedMetadataValue(plugin, role));
        addBars(block);
        block.setType(Material.BLACK_CONCRETE);

        Location spawn = labyrinthGameManager.getSpawn(labyrinth, role);
        if (spawn != null) {
            player.teleport(spawn);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 0.1F);
            player.sendMessage(ChatColor.GREEN + "You joined " + ChatColor.YELLOW + labyrinth + ChatColor.GREEN + " as " + ChatColor.AQUA + role + ChatColor.GREEN + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Spawn location for your role is not set.");
        }
    }

    private boolean isSameBlock(Block block, Location loc) {
        return block.getWorld().getName().equals(loc.getWorld().getName())
                && block.getX() == loc.getBlockX()
                && block.getY() == loc.getBlockY()
                && block.getZ() == loc.getBlockZ();
    }

    public void addBars(Block joinBlock) {
        if (joinBlock == null || joinBlock.getType() != Material.SEA_LANTERN) return;

        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            Block first = joinBlock.getRelative(face);
            Block second = first.getRelative(face);

            if (first.getType() == Material.AIR && second.getType() == Material.AIR) {
                Block base = second.getRelative(BlockFace.UP);
                base.setType(Material.IRON_BARS);
                base.getRelative(BlockFace.UP).setType(Material.IRON_BARS);
                base.getRelative(BlockFace.UP, 2).setType(Material.IRON_BARS);
                return;
            }
        }
    }
}

