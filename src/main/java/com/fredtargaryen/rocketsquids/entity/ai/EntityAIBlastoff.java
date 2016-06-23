package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.EntityRocketSquid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBlastoff extends EntityAIBase {
    public EntityAIBlastoff(EntityRocketSquid entityRocketSquid) {
    }

    @Override
    public boolean shouldExecute() {
        return false;
    }
}
