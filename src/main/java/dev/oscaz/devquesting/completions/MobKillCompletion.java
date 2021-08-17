package dev.oscaz.devquesting.completions;

import dev.oscaz.devquesting.manager.QuestProgressManager;
import dev.oscaz.devquesting.pojo.QuestType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillCompletion implements Listener {

    private final QuestProgressManager progressManager;

    public MobKillCompletion(QuestProgressManager progressManager) {
        this.progressManager = progressManager;
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) { // Technically players count as mobs yes
            this.progressManager.addProgressByType(event.getEntity().getKiller(), QuestType.MOB_KILL, 1);
        }
    }

}
