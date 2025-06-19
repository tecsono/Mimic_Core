package games.mimic.mimic_core;

import games.mimic.mimic_core.commands.LabyrinthSetupCommand;
import games.mimic.mimic_core.commands.SilverCommand;
import games.mimic.mimic_core.listeners.PlayerMoveEventListener;
import games.mimic.mimic_core.listeners.PlayerQuitListener;
import games.mimic.mimic_core.managers.BlockLoader;
import games.mimic.mimic_core.managers.PlaceholderManager;
import games.mimic.mimic_core.managers.SilverManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Mimic_core extends JavaPlugin {

    private static Mimic_core instance;

    private BlockLoader blockLoader;
    private SilverManager silverManager;
    private LabyrinthGameManager labyrinthGameManager;
    private JoinHandler joinHandler;

    @Override
    public void onEnable() {
        instance = this;
        System.out.println("Mimic_Core is now enabled");

        // Initialize SilverManager
        silverManager = new SilverManager(new File(getDataFolder(), "silver.json"));
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderManager(this).register();
        }
        getCommand("silver").setExecutor(new SilverCommand(this));

        // Save default data files if missing
        saveDefaultResource("blocks.json");
        saveDefaultResource("labyrinths.json");

        // Load block and labyrinth data
        blockLoader = new BlockLoader();
        blockLoader.loadBlockLists(new File(getDataFolder(), "blocks.json"));

        BlockEffectHandler blockEffectHandler = new BlockEffectHandler(this, labyrinthGameManager, silverManager);
        labyrinthGameManager = new LabyrinthGameManager(new File(getDataFolder(), "labyrinths.json"), blockLoader, blockEffectHandler);

        // Join handler (initialize after labyrinthGameManager is ready)
        joinHandler = new JoinHandler(labyrinthGameManager, this);

        // Register event listeners
        PlayerMoveEventListener moveListener = new PlayerMoveEventListener(blockLoader, labyrinthGameManager, joinHandler, this);
        getServer().getPluginManager().registerEvents(moveListener, this);

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this, labyrinthGameManager), this);

        // Register labyrinth command
        LabyrinthSetupCommand labyrinthSetupCommand = new LabyrinthSetupCommand(labyrinthGameManager, this);
        getCommand("lb").setExecutor(labyrinthSetupCommand);
    }

    @Override
    public void onDisable() {
        System.out.println("Mimic_core is disabled!");
    }

    public static Mimic_core getInstance() {
        return instance;
    }

    public BlockLoader getBlockLoader() {
        return blockLoader;
    }

    public LabyrinthGameManager getLabyrinthGameManager() {
        return labyrinthGameManager;
    }

    public SilverManager getSilverManager() {
        return silverManager;
    }

    private void saveDefaultResource(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) {
            saveResource(name, false);
        }
    }
}
