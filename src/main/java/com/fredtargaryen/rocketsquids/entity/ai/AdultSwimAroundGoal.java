package com.fredtargaryen.rocketsquids.entity.ai;

import com.fredtargaryen.rocketsquids.entity.RocketSquidEntity;
import com.fredtargaryen.rocketsquids.network.MessageHandler;
import com.fredtargaryen.rocketsquids.network.message.MessageSquidNote;
import com.fredtargaryen.rocketsquids.world.StatueManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.EnumSet;
import java.util.Random;

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

    private final Random r;
	private final double swimForce;
	private StatueBlastStage statueBlastStage;

    public AdultSwimAroundGoal(RocketSquidEntity ers) {
        super();
        this.squid = ers;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.r = this.squid.getRNG();
        this.swimForce = 0.25;
        this.noteIndex = 0;
        this.statueBlastStage = StatueBlastStage.NONE;
        this.tickCounter = 0;
        this.nextScheduledMove = 0;
        this.nextScheduledNote = 0;
        this.scheduleNextMove();
        this.scheduleNextNote();
    }

    @Override
    public boolean shouldExecute() {
        return this.squid.isInWater() && !this.squid.getShaking() && !this.squid.getBlasting();
    }

    /**
     * Do a turn.
     * @param hasVIPRider whether there is a rider who is a VIP
     * @param blocked in the directions the squid is mainly pointing in, whether there are blocks in the way
     * @return whether a turn will be executed. It won't if the squid is pointing roughly where the rider is facing.
     */
    public boolean doTurn(boolean hasVIPRider, boolean blocked) {
        if(hasVIPRider) {
            //Rider rotations are clamped to [-PI, PI]; squid rotations are not.
            //Therefore need to work in terms of this range, or risk squids spinning ridiculous amounts if they have
            //turned around many times before being ridden.
            Entity pass = this.squid.getControllingPassenger();
            float pp = (float) ((pass.rotationPitch + 90.0F) * Math.PI / 180.0F);
            float py = (float) (pass.getRotationYawHead() * Math.PI / 180.0F);
            float unclamped_sp = (float) this.squid.getRotPitch();
            float unclamped_sy = (float) this.squid.getRotYaw();
            //Clamp them to [-2PI, 2PI]
            float clamped_sp = unclamped_sp;
            while(clamped_sp > Math.PI * 2) clamped_sp -= Math.PI * 2;
            while(clamped_sp < -Math.PI * 2) clamped_sp += Math.PI * 2;
            float clamped_sy = unclamped_sy;
            while(clamped_sy > Math.PI * 2) clamped_sy -= Math.PI * 2;
            while(clamped_sy < -Math.PI * 2) clamped_sy += Math.PI * 2;
            float pitchDiff = pp - clamped_sp;
            float yawDiff = py - clamped_sy;
            if(Math.abs(pitchDiff) >= 0.005 || Math.abs(yawDiff) >= 0.005) {
                //Player rotation is sufficiently far from squid rotation for the squid to start a new turn
                //Turn by the difference in rotations, to avoid having to spin into the [-PI, PI] range
                this.squid.setTargetRotPitch(unclamped_sp + pitchDiff);
                this.squid.setTargetRotYaw(unclamped_sy + yawDiff);
                return true;
            }
            return false;
        }
        else {
            if(blocked) {
                //Just point the opposite way
                Vec3d direction = this.squid.getDirectionAsVector();
                this.squid.pointToVector(new Vec3d(-direction.x, -direction.y, -direction.z), Math.PI / 3.0);
            }
            else {
                //Random doubles between -PI and PI, added to current rotation
                this.squid.setTargetRotPitch(this.squid.getRotPitch() + (this.r.nextDouble() * Math.PI / 4 * (this.r.nextBoolean() ? 1 : -1)));
                this.squid.setTargetRotYaw(this.squid.getRotYaw() + (this.r.nextDouble() * Math.PI / 4 * (this.r.nextBoolean() ? 1 : -1)));
            }
            return true;
        }
    }

    /**
     * Schedule the next move for 1-3 seconds in the future.
     * Need to allow some time for the move to finish, as well as some time for the squid to just hover for a bit
     */
    private void scheduleNextMove() {
        this.nextScheduledMove += 20 + this.r.nextInt(80);
    }

    /**
     * Schedule the next note for 2-3 seconds in the future.
     * Need to allow some time for the note to play, as well as some silent time
     */
    private void scheduleNextNote() {
        this.nextScheduledNote += 40 + this.r.nextInt(20);
    }

    @Override
    public void tick() {
        ++this.tickCounter;
        double rp = this.squid.getRotPitch();
        double ry = this.squid.getRotYaw();
        if(this.squid.getBlastToStatue()) {
            //Override all behaviour if it heard its target notes and needs to find and blast to a statue
            switch (this.statueBlastStage) {
                case NONE:
                    this.statueBlastStage = StatueBlastStage.LOCATE;
                    break;
                case LOCATE:
                    //Find nearest statue
                    int[] statueCoords = StatueManager.forWorld(this.squid.world).getNearestStatuePos(this.squid.posX, this.squid.posY, this.squid.posZ);
                    if(statueCoords[3] < 1) {
                        //StatueManager doesn't have any statues loaded
                        this.statueBlastStage = StatueBlastStage.NONE;
                        this.squid.setBlastToStatue(false);
                    }
                    else {
                        //TargetPoint for playing notes related to distance
                        PacketDistributor.TargetPoint squidPoint = new PacketDistributor.TargetPoint(this.squid.posX, this.squid.posY, this.squid.posZ, 16.0F, this.squid.dimension);
                        double zDistance = statueCoords[4] - this.squid.posZ;
                        double xDistance = statueCoords[2] - this.squid.posX;
                        double hozDistanceSquared = zDistance * zDistance + xDistance * xDistance;
                        //Turn in direction of nearest statue. Not sure why but these values are necessary for it to point correctly
                        this.squid.setTargetRotYaw(Math.atan2(-xDistance, zDistance));
                        //Play a celebratory chord
                        if (hozDistanceSquared > 640000.0) {
                            //More than 50 chunks away (50 * 16 = 800 blocks). Low C Major
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)0));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)4));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)7));
                        }
                        else if (hozDistanceSquared > 25600.0) {
                            //10-50 chunks away (10 * 16 = 160 blocks). Middle C Major
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)12));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)16));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)19));
                        }
                        else {
                            //Less than 10 chunks away. High C Major
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)24));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)28));
                            MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> squidPoint), new MessageSquidNote((byte)31));
                        }
                        if (hozDistanceSquared > 6400.0) {
                            //More than 80 blocks (5 chunks) away horizontally; blast at 45 degrees so the player can hopefully see easily
                            //A squid can go about 80 blocks at surface level and 45 degrees so this should prevent some annoying overshooting
                            this.squid.setTargetRotPitch(Math.PI / 4.0);
                        } else {
                            //Less than 80 blocks away; blast directly towards the statue
                            this.squid.setTargetRotPitch(Math.atan2(statueCoords[3] - this.squid.posY, Math.sqrt(hozDistanceSquared)) + Math.PI / 2.0);
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
        }
        else {
            //Move and play notes if scheduled
            if(this.tickCounter == this.nextScheduledMove) {
                if(!this.squid.areBlocksInWay()) this.squid.addForce(this.swimForce);
                this.scheduleNextMove();
            }
            if(this.tickCounter == this.nextScheduledNote) {
                this.playNextNote();
                this.scheduleNextNote();
            }

            double trp = this.squid.getTargRotPitch();
            double Try = this.squid.getTargRotYaw();
            if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005) {
                //The last turn is as good as finished
                int randomInt = this.r.nextInt(12);
                if (randomInt == 0) {
                    if(!this.squid.areBlocksInWay()) this.squid.setShaking(true);
                } else {
                    this.doTurn(this.squid.hasVIPRider(), this.squid.areBlocksInWay());
                }
            }
        }
    }

    private void playNextNote() {
        byte note = this.squid.getTargetNote(this.noteIndex);
        MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(this.squid.posX, this.squid.posY, this.squid.posZ, 16.0F, this.squid.dimension)), new MessageSquidNote(note));
        if(this.noteIndex == 2) {
            this.noteIndex = 0;
        }
        else {
            ++this.noteIndex;
        }
    }
}
