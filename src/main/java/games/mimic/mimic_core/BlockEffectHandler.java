package games.mimic.mimic_core;

import games.mimic.mimic_core.LabyrinthGameManager;
import games.mimic.mimic_core.managers.SilverManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;



public class BlockEffectHandler {
    private final Plugin plugin;
    private final LabyrinthGameManager labyrinthGameManager;
    private final SilverManager silverManager;

    public BlockEffectHandler(Plugin plugin, LabyrinthGameManager labyrinthGameManager, SilverManager silverManager) {
        this.plugin = plugin;
        this.labyrinthGameManager = labyrinthGameManager; //red
        this.silverManager = silverManager;
    }

    public void applyEffect(Player player, Material secondLayer) {
        switch (secondLayer) {
            case RED_CONCRETE -> handleDeath(player);
            case MAGENTA_CONCRETE -> player.sendMessage(ChatColor.GOLD + "You got a bonus block!");
            case PINK_CONCRETE -> player.sendMessage(ChatColor.BLUE + "You found the end!");
        }
    }

    private final String[] deathMessages = {
            "Your vision fades...",
            "You feel your body slipping away...",
            "A cold whisper surrounds you...",
            "Everything goes black."
    };

    private String getRandomMessage() {
        Random random = new Random();
        int index = random.nextInt(deathMessages.length);
        return deathMessages[index];
    }

    private void handleDeath(Player player) {
        // Get metadata
        String labyrinthName = player.getMetadata("labyrinth_name").get(0).asString();
        String role = player.getMetadata("labyrinth_role").get(0).asString();
        Location spawn = labyrinthGameManager.getSpawn(labyrinthName, role);

        player.sendMessage(ChatColor.RED + "⛃-5");
        silverManager.subtractSilver(player.getUniqueId(), 5);

        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1F, 0.1F);
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 40, 0.3, 0.3, 0.3);
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation(), 30, 0.2, 0.2, 0.2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));

        player.setMetadata("nomove", new FixedMetadataValue(plugin, true));
        String randomMessage = getRandomMessage();
        player.sendMessage(ChatColor.DARK_RED + randomMessage);

    }

    private void handleBonus(Player player) {
        player.sendMessage("s");
    }
}

