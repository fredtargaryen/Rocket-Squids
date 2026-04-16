// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.level.entity.ai;

import com.fredtargaryen.rocketsquids.DataReference;
import com.fredtargaryen.rocketsquids.level.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.level.StatueData;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidNote;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class AdultSwimAroundGoal extends Goal {
    private final RocketSquidEntity squid;
    private byte noteIndex;
    private int tickCounter;
    private int nextScheduledMove;
    private int nextScheduledNote;

    private enum StatueBlastStage {
        NONE,
        LOCATE,
        TURN
    }

    private final RandomSource r;
    private final double swimForce;
    private StatueBlastStage statueBlastStage;

    public AdultSwimAroundGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.r = this.squid.getRandom();
        this.swimForce = 0.25;
        this.noteIndex = 0;
        this.statueBlastStage = StatueBlastStage.NONE;
        this.tickCounter = 0;
        this.nextScheduledMove = 0;
        this.nextScheduledNote = 0;
    }

    @Override
    public boolean canUse() {
        return this.squid.isInWater();
    }

    /**
     * Do a turn.
     *
     * @param playerIsRiding whether a player is riding the squid
     * @param blocked        in the directions the squid is mainly pointing in, whether there are blocks in the way
     */
    public void doTurn(boolean playerIsRiding, boolean blocked) {
        if (playerIsRiding) {
            //Rider rotations are clamped to [-PI, PI]; squid rotations are not.
            //Therefore need to work in terms of this range, or risk squids spinning ridiculous amounts if they have
            //turned around many times before being ridden.
            Entity pass = this.squid.getFirstPassenger();
            assert pass != null;
            float pp = (float) ((pass.getXRot() + 90.0F) * Math.PI / 180.0F);
            float py = (float) (pass.getYHeadRot() * Math.PI / 180.0F);
            float unclamped_sp = (float) this.squid.getRotPitch();
            float unclamped_sy = (float) this.squid.getRotYaw();
            //Clamp them to [-2PI, 2PI]
            float clamped_sp = unclamped_sp;
            while (clamped_sp > Math.PI * 2) clamped_sp -= (float) (Math.PI * 2);
            while (clamped_sp < -Math.PI * 2) clamped_sp += (float) (Math.PI * 2);
            float clamped_sy = unclamped_sy;
            while (clamped_sy > Math.PI * 2) clamped_sy -= (float) (Math.PI * 2);
            while (clamped_sy < -Math.PI * 2) clamped_sy += (float) (Math.PI * 2);
            float pitchDiff = pp - clamped_sp;
            float yawDiff = py - clamped_sy;
            if (Math.abs(pitchDiff) >= 0.005 || Math.abs(yawDiff) >= 0.005) {
                //Player rotation is sufficiently far from squid rotation for the squid to start a new turn
                //Turn by the difference in rotations, to avoid having to spin into the [-PI, PI] range
                this.squid.setTargetRotPitch(unclamped_sp + pitchDiff);
                this.squid.setTargetRotYaw(unclamped_sy + yawDiff);
            }
        } else {
            if (blocked) {
                //Just point the opposite way
                Vec3 direction = this.squid.getDirectionAsVec3();
                this.squid.pointToVector(new Vec3(-direction.x, -direction.y, -direction.z), Math.PI / 3.0);
            } else {
                //Random doubles between -PI and PI, added to current rotation
                this.squid.setTargetRotPitch(this.squid.getRotPitch() + (this.r.nextDouble() * Math.PI / 4 * (this.r.nextBoolean() ? 1 : -1)));
                this.squid.setTargetRotYaw(this.squid.getRotYaw() + (this.r.nextDouble() * Math.PI / 4 * (this.r.nextBoolean() ? 1 : -1)));
            }
        }
    }

    /**
     * Schedule the next move for 1-3 seconds in the future.
     * Need to allow some time for the move to finish, as well as some time for the squid to just hover for a bit
     */
    private void scheduleNextMove() {
        this.nextScheduledMove += 10 + this.r.nextInt(20);
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
        double rp = this.squid.getRotPitch();
        double ry = this.squid.getRotYaw();
        if (this.squid.getBlastToStatue()) {
            //Override all behaviour if it heard its target notes and needs to find and blast to a statue
            switch (this.statueBlastStage) {
                case NONE:
                    this.statueBlastStage = StatueBlastStage.LOCATE;
                    break;
                case LOCATE:
                    //Find nearest statue
                    Vec3 pos = this.squid.position();
                    int[] statueCoords = StatueData.forWorld(this.squid.level()).getNearestStatuePos(pos.x, pos.y, pos.z);
                    if (statueCoords == null) {
                        //StatueManager doesn't have any statues loaded
                        this.statueBlastStage = StatueBlastStage.NONE;
                        this.squid.setBlastToStatue(false);
                    } else {
                        //TargetPoint for playing notes related to distance
                        PacketDistributor.TargetPoint squidPoint = new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, DataReference.PLAYER_HEAR_RANGE, this.squid.level().dimension());
                        double zDistance = statueCoords[4] - pos.z;
                        double xDistance = statueCoords[2] - pos.x;
                        double hozDistanceSquared = zDistance * zDistance + xDistance * xDistance;
                        //Turn in direction of nearest statue. Not sure why but these values are necessary for it to point correctly
                        this.squid.setTargetRotYaw(Math.atan2(-xDistance, zDistance));
                        // Send "Recognition" empty sound for those using subs
                        MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE, this.squid.level().dimension())), new MessageSquidNote((byte) 36));
                        //Play a celebratory chord
                        if (hozDistanceSquared > 640000.0) {
                            //More than 50 chunks away (50 * 16 = 800 blocks). Low C Major
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 0));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 4));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 7));
                        } else if (hozDistanceSquared > 25600.0) {
                            //10-50 chunks away (10 * 16 = 160 blocks). Middle C Major
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 12));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 16));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 19));
                        } else {
                            //Less than 10 chunks away. High C Major
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 24));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 28));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte) 31));
                        }
                        if (hozDistanceSquared > 6400.0) {
                            //More than 80 blocks (5 chunks) away horizontally; blast at 45 degrees so the player can hopefully see easily
                            //A squid can go about 80 blocks at surface level and 45 degrees so this should prevent some annoying overshooting
                            this.squid.setTargetRotPitch(Math.PI / 4.0);
                        } else {
                            //Less than 80 blocks away; blast directly towards the statue
                            this.squid.setTargetRotPitch(Math.atan2(pos.y - statueCoords[3], Math.sqrt(hozDistanceSquared)) + Math.PI / 2.0);
                        }
                        this.statueBlastStage = StatueBlastStage.TURN;
                    }
                    break;
                case TURN:
                    double trp = this.squid.getTargRotPitch();
                    double Try = this.squid.getTargRotYaw();
                    if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005) {
                        this.squid.setShaking(true);
                        this.statueBlastStage = StatueBlastStage.NONE;
                        this.squid.setBlastToStatue(false);
                    }
                    break;
                default:
                    break;
            }
        } else {
            if (this.tickCounter > this.nextScheduledMove) this.scheduleNextMove();
            if (this.tickCounter > this.nextScheduledNote) this.scheduleNextNote();

            //Move and play notes if scheduled
            if (this.tickCounter == this.nextScheduledMove) {
                int randomInt = this.r.nextInt(11);
                if (randomInt == 0) {
                    if (!this.squid.areBlocksInWay()) {
                        this.squid.setShaking(true);
                    }
                } else if (randomInt < 6) {
                    this.doTurn(this.squid.getFirstPassenger() instanceof Player, this.squid.areBlocksInWay());
                    this.scheduleNextMove();
                } else {
                    if (!this.squid.areBlocksInWay()) this.squid.addForce(this.swimForce);
                    this.scheduleNextMove();
                }
            }
            if (this.tickCounter == this.nextScheduledNote) {
                this.playNextNote();
                this.scheduleNextNote();
            }
        }
    }

    private void playNextNote() {
        byte note = this.squid.getTargetNote(this.noteIndex);
        Vec3 pos = this.squid.position();
        MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, DataReference.SQUID_SING_RANGE, this.squid.level().dimension())), new MessageSquidNote(note));
        if (this.noteIndex == 2) {
            this.noteIndex = 0;
        } else {
            ++this.noteIndex;
        }
    }
}
