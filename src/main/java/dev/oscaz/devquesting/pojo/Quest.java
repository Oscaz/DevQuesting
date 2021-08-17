package dev.oscaz.devquesting.pojo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Quest {

    private final String key;
    private final QuestType type;
    private final int amount;
    private final List<String> rewardCommands;

    public Quest(String key, QuestType type, int amount, List<String> rewardCommands) {
        this.key = key;
        this.type = type;
        this.amount = amount;
        this.rewardCommands = rewardCommands;
    }

    public String getKey() {
        return this.key;
    }

    public QuestType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public List<String> getRewardCommands() {
        return Collections.unmodifiableList(this.rewardCommands);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quest quest = (Quest) o;
        return Objects.equals(key, quest.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
