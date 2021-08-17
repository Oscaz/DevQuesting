package dev.oscaz.devquesting.completions;

import dev.oscaz.devquesting.manager.QuestProgressManager;
import dev.oscaz.devquesting.pojo.QuestType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakCompletion implements Listener {

    private final QuestProgressManager progressManager;

    public BlockBreakCompletion(QuestProgressManager progressManager) {
        this.progressManager = progressManager;
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        this.progressManager.addProgressByType(event.getPlayer(), QuestType.BLOCK_BREAK, 1);
    }

}
