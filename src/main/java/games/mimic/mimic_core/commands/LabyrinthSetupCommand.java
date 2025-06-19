package games.mimic.mimic_core.commands;

import games.mimic.mimic_core.Mimic_core;
import games.mimic.mimic_core.LabyrinthGameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LabyrinthSetupCommand implements CommandExecutor {
    private final LabyrinthGameManager labyrinthGameManager;
    private final Mimic_core plugin;

    private final Map<UUID, SetupStep> playerSteps = new HashMap<>();
    private final Map<UUID, String> labyrinthNames = new HashMap<>();

    private enum SetupStep {
        NAME_ENTERED,
        MASTER_SPAWN,
        SUBJECT_SPAWN,
        MASTER_BLOCK,
        SUBJECT_BLOCK,
        WINNINGS,
        COMPLETE
    }

    public LabyrinthSetupCommand(LabyrinthGameManager labyrinthManager, Mimic_core plugin) {
        this.labyrinthGameManager = labyrinthManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            if (!player.hasMetadata("labyrinth_name") || !player.hasMetadata("labyrinth_role")) {
                player.sendMessage(ChatColor.RED + "You are not in a labyrinth.");
                return true;
            }

            String labyrinth = player.getMetadata("labyrinth_name").get(0).asString();
            String role = player.getMetadata("labyrinth_role").get(0).asString();

            // send to lobby (not yet) Location safeLocation = player.getWorld().getSpawnLocation();
            // player.teleport(safeLocation);
            player.sendMessage(ChatColor.YELLOW + "You left the labyrinth: " + ChatColor.AQUA + labyrinth);

            // Remove metadata
            player.removeMetadata("labyrinth_name", plugin);
            player.removeMetadata("labyrinth_role", plugin);

            // restore join block
            Location joinLoc = labyrinthGameManager.getJoinBlock(labyrinth, role);
            if (joinLoc != null) {
                joinLoc.getBlock().setType(Material.SEA_LANTERN);
            }
            return true;
        }

        String labyrinthName = args[1];
        if (labyrinthGameManager.labyrinthExists(labyrinthName)) {
            player.sendMessage(ChatColor.RED + "A labyrinth with that name already exists.");
            return true;
        }

        labyrinthNames.put(player.getUniqueId(), labyrinthName);
        playerSteps.put(player.getUniqueId(), SetupStep.NAME_ENTERED);
        labyrinthGameManager.createLabyrinth(labyrinthName);

        player.sendMessage(ChatColor.GREEN + "Labyrinth '" + labyrinthName + "' created.");
        sendClickableInstruction(player, "Click here to set your current location as the Master spawn.", "/lbsetup setmaster");

        return true;
    }

    public void handleProgress(Player player, String action) {
        UUID id = player.getUniqueId();
        SetupStep step = playerSteps.get(id);
        String name = labyrinthNames.get(id);

        if (step == null || name == null) return;

        switch (action) {
            case "setmaster" -> {
                labyrinthGameManager.setSpawn(name, "master", player.getLocation());
                playerSteps.put(id, SetupStep.MASTER_SPAWN);
                player.sendMessage(ChatColor.AQUA + "Master spawn set.");
                sendClickableInstruction(player, "Click here to set your current location as the Subject spawn.", "/lbsetup setsubject");
            }
            case "setsubject" -> {
                labyrinthGameManager.setSpawn(name, "subject", player.getLocation());
                playerSteps.put(id, SetupStep.SUBJECT_SPAWN);
                player.sendMessage(ChatColor.AQUA + "Subject spawn set.");
                sendClickableInstruction(player, "Stand on the MASTER join block and click here.", "/lbsetup masterblock");
            }
            case "masterblock" -> {
                Location loc = player.getLocation().getBlock().getLocation().add(0, -1, 0);
                labyrinthGameManager.setJoinBlock(name, "master", loc);
                playerSteps.put(id, SetupStep.MASTER_BLOCK);
                player.sendMessage(ChatColor.AQUA + "Master join block set.");
                sendClickableInstruction(player, "Now stand on the SUBJECT join block and click here.", "/lbsetup subjectblock");
            }
            case "subjectblock" -> {
                Location loc = player.getLocation().getBlock().getLocation().add(0, -1, 0);
                labyrinthGameManager.setJoinBlock(name, "subject", loc);
                playerSteps.put(id, SetupStep.SUBJECT_BLOCK);
                player.sendMessage(ChatColor.AQUA + "Subject join block set.");
                sendClickableInstruction(player, "Click here to enter winnings amount (e.g., /lbsetup winnings 100)", "/lbsetup winnings 100");
            }
            default -> {
                if (action.startsWith("winnings")) {
                    String[] parts = action.split(" ");
                    if (parts.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /lbsetup winnings <amount>");
                        return;
                    }
                    try {
                        int amount = Integer.parseInt(parts[1]);
                        labyrinthGameManager.setWinnings(name, amount);
                        playerSteps.put(id, SetupStep.COMPLETE);
                        player.sendMessage(ChatColor.GREEN + "Labyrinth setup complete! Winnings set to " + amount + ".");
                        labyrinthNames.remove(id);
                        playerSteps.remove(id);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Amount must be a number.");
                    }
                }
            }
        }
    }

    private void sendClickableInstruction(Player player, String message, String command) {
        Component component = Component.text(message).color(NamedTextColor.YELLOW).clickEvent(ClickEvent.runCommand(command));
        player.sendMessage(component);
    }
}
