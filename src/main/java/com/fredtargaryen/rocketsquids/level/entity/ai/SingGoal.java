// Copyright 2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.SquidNoteMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class SingGoal extends Goal {
    private final RocketSquidEntity squid;
    private byte noteIndex;
    private int tickCounter;
    private int nextScheduledNote;
    private final RandomSource r;

    public SingGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.r = this.squid.getRandom();
        this.noteIndex = 0;
        this.tickCounter = 0;
        this.nextScheduledNote = 0;
    }

    @Override
    public boolean canUse() {
        return !this.squid.isBaby() && this.squid.isInWater() && !this.squid.getShaking() && this.squid.blastingToStatue;
    }

    /**
     * Schedule the next note for 2-3 seconds in the future.
     * Need to allow some time for the note to play, as well as some silent time
     */
    private void scheduleNextNote() {
        this.nextScheduledNote += 20 + this.r.nextInt(10);
    }

    @Override
    public void tick() {
        ++this.tickCounter;

        if (this.tickCounter > this.nextScheduledNote) {
            this.scheduleNextNote();
            return;
        }

        if (this.tickCounter == this.nextScheduledNote) {
            this.playNextNote();
            this.scheduleNextNote();
        }
    }

    private void playNextNote() {
        int note = this.squid.getTargetNote(this.noteIndex);
        Vec3 pos = this.squid.position();
        MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage(note), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
        if (this.noteIndex == 2) {
            this.noteIndex = 0;
        } else {
            ++this.noteIndex;
        }
    }
}
