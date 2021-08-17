package dev.oscaz.devquesting.completions;

import dev.oscaz.devquesting.manager.QuestProgressManager;
import dev.oscaz.devquesting.pojo.QuestType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceCompletion implements Listener {

    private final QuestProgressManager progressManager;

    public BlockPlaceCompletion(QuestProgressManager progressManager) {
        this.progressManager = progressManager;
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        this.progressManager.addProgressByType(event.getPlayer(), QuestType.BLOCK_PLACE, 1);
    }

}
