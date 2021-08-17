package dev.oscaz.devquesting.manager;

import com.google.common.collect.Sets;
import dev.oscaz.devquesting.pojo.Quest;
import dev.oscaz.devquesting.pojo.QuestType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {

    private final JavaPlugin plugin;
    private final Set<Quest> quests;

    public QuestManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.quests = Sets.newHashSet();
    }

    public void load() {
        this.load(true);
    }

    public void load(boolean clear) {
        if (clear) this.quests.clear();

        this.plugin.getConfig().getKeys(false).forEach(key -> {
            ConfigurationSection section = this.plugin.getConfig().getConfigurationSection(key);
            String type = section.getString("type");
            int amount = section.getInt("amount", -1);
            List<String> rewardCommands = section.getStringList("rewards");
            if (Arrays.stream(QuestType.values()).noneMatch(target -> target.name().equalsIgnoreCase(type))) {
                String validQuests = Arrays.stream(QuestType.values()).map(QuestType::name).collect(Collectors.joining(", "));
                this.plugin.getLogger().severe("Could not load quest " + key + ". No matching quest type for " + type + ". Valid: [" + validQuests + "]");
                return;
            }
            if (amount < 1) {
                this.plugin.getLogger().severe("Could not load quest " + key + ". Invalid amount, must be >= 1");
                return;
            }
            if (rewardCommands.isEmpty()) {
                this.plugin.getLogger().warning("Warning, quest " + key + " does not have any reward commands. It will be loaded.");
            }
            this.quests.add(new Quest(key, QuestType.valueOf(type.toUpperCase()), amount, rewardCommands)); // Enum matching is case insensitive
        });
    }

    public Optional<Quest> getByKey(String key) {
        return this.quests.stream().filter(quest -> quest.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Set<Quest> getQuests() {
        return Collections.unmodifiableSet(this.quests);
    }



}
