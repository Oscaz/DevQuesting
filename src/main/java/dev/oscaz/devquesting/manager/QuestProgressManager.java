package dev.oscaz.devquesting.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.oscaz.devquesting.pojo.Quest;
import dev.oscaz.devquesting.pojo.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class QuestProgressManager implements Listener {

    private final JavaPlugin plugin;
    private final QuestManager questManager;
    private final Table<UUID, Quest, Integer> progress;
    private final File file;
    private final YamlConfiguration data;

    public QuestProgressManager(JavaPlugin plugin, QuestManager questManager) {
        this.plugin = plugin;
        this.questManager = questManager;
        this.progress = HashBasedTable.create();

        this.file = new File(this.plugin.getDataFolder(), "progress.yml");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialise quest progress yml file", e);
            }
        }
        this.data = YamlConfiguration.loadConfiguration(this.file);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    public void addProgressByType(Player player, QuestType questType, int amount) {
        this.questManager.getQuests().stream()
                .filter(quest -> quest.getType() == questType)
                .forEach(quest -> this.addProgress(player, quest, amount));
    }

    public int getProgress(Player player, Quest quest) {
        return Optional.ofNullable(this.progress.get(player.getUniqueId(), quest)).orElse(0); // Why does guava not have any getOrDefault :(
    }

    public void addProgress(Player player, Quest quest, int amount) {
        this.setProgress(player, quest, this.getProgress(player, quest) + amount);
    }

    public void setProgress(Player player, Quest quest, int amount) {
        if (amount >= quest.getAmount()) { // Player has completed quest
            this.complete(player, quest);
        } else {
            this.progress.put(player.getUniqueId(), quest, amount);
        }
    }

    public void complete(Player player, Quest quest) {
        this.progress.put(player.getUniqueId(), quest, 0);
        quest.getRewardCommands().stream()
                .map(command -> command.replace("%player%", player.getName()))
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        this.runYaml(data -> {
            ConfigurationSection section = data.getConfigurationSection(event.getUniqueId().toString());
            if (section == null) return; // Player has no pre-existing data

            section.getKeys(false).forEach(questKey -> {
                int playerProgress = section.getInt(questKey, 0);
                this.questManager.getByKey(questKey).ifPresent(quest -> {
                    this.runTableOperation(progress -> progress.put(event.getUniqueId(), quest, playerProgress));
                });
            });
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.runYaml(data -> {
                this.runTableOperation(progress -> {
                    progress.row(event.getPlayer().getUniqueId()).forEach((quest, playerProgress) -> {
                        data.set(event.getPlayer().getUniqueId() + "." + quest.getKey(), playerProgress);
                    });
                    progress.row(event.getPlayer().getUniqueId()).clear();
                });
            });
            this.saveYaml();
        });
    }

    private void runTableOperation(Consumer<Table<UUID, Quest, Integer>> operation) { // Need thread-safety, none built in via guava.
        synchronized (this.progress) {
            operation.accept(this.progress);
        }
    }

    private void runYaml(Consumer<YamlConfiguration> operation) {
        synchronized (this.data) {
            operation.accept(this.data);
        }
    }

    private void saveYaml() {
        this.runYaml(data -> {
            try {
                data.save(this.file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save data file", e);
            }
        });
    }

}
