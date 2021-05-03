package com.arematics.minecraft.optimizer.clearlag;

public interface ClearLagTask {
    void preClear();
    void clear();
    void postClear();
}
