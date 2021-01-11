package com.arematics.minecraft.strongholds;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.data.service.StrongholdService;
import com.arematics.minecraft.strongholds.capture.controller.StrongholdCaptureController;

import java.util.concurrent.TimeUnit;

public class StrongholdBoot extends Bootstrap {

    public StrongholdBoot() {
        super(false);
    }

    @Override
    public void postEnable() {
        ArematicsExecutor.asyncDelayed(() -> {
            StrongholdCaptureController controller = Boots.getBoot(CoreBoot.class).getContext().getBean(StrongholdCaptureController.class);
            Stronghold stronghold = Boots.getBoot(CoreBoot.class).getContext().getBean(StrongholdService.class).findById("test");
            controller.enableStronghold(stronghold);
        }, 30, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {

    }
}
