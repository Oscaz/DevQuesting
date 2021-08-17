package dev.oscaz.devquesting;

import dev.oscaz.devquesting.completions.BlockBreakCompletion;
import dev.oscaz.devquesting.completions.BlockPlaceCompletion;
import dev.oscaz.devquesting.completions.MobKillCompletion;
import dev.oscaz.devquesting.manager.QuestManager;
import dev.oscaz.devquesting.manager.QuestProgressManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class DevQuesting extends JavaPlugin {

    private QuestManager questManager;
    private QuestProgressManager progressManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.questManager = new QuestManager(this);
        this.questManager.load();

        this.progressManager = new QuestProgressManager(this, this.questManager);

        Arrays.asList(
                new BlockBreakCompletion(this.progressManager),
                new BlockPlaceCompletion(this.progressManager),
                new MobKillCompletion(this.progressManager)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {

    }

}
