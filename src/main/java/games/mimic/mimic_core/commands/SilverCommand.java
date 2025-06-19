package games.mimic.mimic_core.commands;

import games.mimic.mimic_core.Mimic_core;
import games.mimic.mimic_core.managers.SilverManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SilverCommand implements CommandExecutor {

    private final Mimic_core plugin;
    private final SilverManager silverManager;

    public SilverCommand(Mimic_core plugin) {
        this.plugin = plugin;
        this.silverManager = plugin.getSilverManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/silver <check|give|take|set> <player> <amount>");
            return true;
        }

        if (args.length < 2 && !args[0].equalsIgnoreCase("check")) {
            sender.sendMessage(ChatColor.RED + "Usage: /silver <check|give|take|set> <player> <amount>");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("check")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can check their balance.");
                return true;
            }

            if (!sender.hasPermission("mimic.normal")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            Player player = (Player) sender;
            int silver = silverManager.getSilver(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have " + silver + " silver.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /silver " + sub + " <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a positive number.");
            return true;
        }

        UUID uuid = target.getUniqueId();

        switch (sub) {
            case "give":
                silverManager.addSilver(uuid, amount);
                sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " silver to " + target.getName());
                target.sendMessage(ChatColor.GREEN + "You received " + amount + " silver!");
                break;
            case "take":
                silverManager.subtractSilver(uuid, amount);
                sender.sendMessage(ChatColor.GREEN + "Took " + amount + " silver from " + target.getName());
                target.sendMessage(ChatColor.RED + "You lost " + amount + " silver.");
                break;
            case "set":
                silverManager.setSilver(uuid, amount);
                sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s silver to " + amount);
                target.sendMessage(ChatColor.YELLOW + "Your silver is now " + amount);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
        }

        return true;
    }
}