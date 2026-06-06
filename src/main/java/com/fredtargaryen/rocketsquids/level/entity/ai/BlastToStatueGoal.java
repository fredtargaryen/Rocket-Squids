// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.StatueData;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.SquidNoteMessage;
import com.fredtargaryen.rocketsquids.util.RotationHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Determine the position of the nearest squid statue, turn towards it and begin countdown sequence.
 * This Goal is to be performed only by adult Rocket Squids.
 */
public class BlastToStatueGoal extends Goal {
    private final RocketSquidEntity squid;

    public BlastToStatueGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.squid.blastingToStatue && this.squid.isInWater();
    }

    @Override
    public void start() {
        //Find nearest statue
        Vec3 pos = this.squid.position();
        List<Integer> statueCoords = StatueData.forLevel(this.squid.level()).getNearestStatuePos(pos.x, pos.y, pos.z);
        if (statueCoords == null) {
            //StatueManager doesn't have any statues loaded
            this.squid.blastingToStatue = false;
        } else {
            double zDistance = statueCoords.get(4) - pos.z;
            double xDistance = statueCoords.get(2) - pos.x;
            double hozDistanceSquared = zDistance * zDistance + xDistance * xDistance;
            //Turn in direction of nearest statue. Not sure why but these values are necessary for it to point correctly
            this.squid.setTargetYaw(Math.atan2(-xDistance, zDistance));
            // Send "Recognition" empty sound for those using subs
            MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 36), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
            //Play a celebratory chord
            if (hozDistanceSquared > 640000.0) {
                //More than 50 chunks away (50 * 16 = 800 blocks). Low C Major
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 0), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 4), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 7), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
            } else if (hozDistanceSquared > 25600.0) {
                //10-50 chunks away (10 * 16 = 160 blocks). Middle C Major
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 12), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 16), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 19), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
            } else {
                //Less than 10 chunks away. High C Major
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 24), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 28), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
                MessageHandler.sendToPlayersNear((ServerLevel) this.squid.level(), new SquidNoteMessage((byte) 31), pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE);
            }
            if (hozDistanceSquared > 6400.0) {
                //More than 80 blocks (5 chunks) away horizontally; blast at 45 degrees so the player can hopefully see easily
                //A squid can go about 80 blocks at surface level and 45 degrees so this should prevent some annoying overshooting
                this.squid.setTargetPitch(Math.PI / 4.0);
            } else {
                //Less than 80 blocks away; blast directly towards the statue
                this.squid.setTargetPitch(Math.atan2(pos.y - statueCoords.get(3), Math.sqrt(hozDistanceSquared)) + Math.PI / 2.0);
            }
        }
    }

    @Override
    public void tick() {
        double pitch = this.squid.getPitch();
        double yaw = this.squid.getYaw();
        double targetPitch = this.squid.getTargetPitch();
        double targetYaw = this.squid.getTargetYaw();
        if (Math.abs(targetPitch - pitch) < 0.005 && Math.abs(targetYaw - yaw) < 0.005) {
            this.squid.blastingToStatue = false;
            this.squid.beginCountdown();
        }
    }

    @Override
    public void stop() {
        this.squid.blastingToStatue = false;
    }
}
