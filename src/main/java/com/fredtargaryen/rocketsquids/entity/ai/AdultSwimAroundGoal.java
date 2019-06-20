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
    private enum StatueBlastStage {
        NONE,
        LOCATE,
        TURN
    }

    //FOR TESTING
    //private boolean goHorizontal = false;
    //private double[] angles = new double[]{-Math.PI, -3*Math.PI / 4, -Math.PI / 2, -Math.PI / 4, 0, Math.PI / 4, Math.PI / 2, 3 * Math.PI / 4};
    //private int currentAngle;

    /**
     * True if turning; false if swimming forwards
     */
    private boolean turning;

    private final Random r;
	private final double swimForce;
	private StatueBlastStage statueBlastStage;

    public AdultSwimAroundGoal(RocketSquidEntity ers, double swimForce) {
        super();
        this.squid = ers;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.turning = false;
        this.r = this.squid.getRNG();
        this.swimForce = swimForce;
        //this.currentAngle = 0;
        this.noteIndex = 0;
        this.statueBlastStage = StatueBlastStage.NONE;
    }

    @Override
    public boolean shouldExecute() {
        return this.squid.isInWater() && !this.squid.getShaking() && !this.squid.getBlasting();
    }

    /**
     * Do a turn.
     * @param hasVIPRider whether there is a rider who is a VIP
     * @return whether a turn will be executed. It won't if the squid is pointing roughly where the rider is facing.
     */
    public boolean doTurn(boolean hasVIPRider) {
        if(hasVIPRider) {
            Entity pass = this.squid.getControllingPassenger();
            float pp = (float) ((pass.rotationPitch + 90.0F) * Math.PI / 180.0F);
            float py = (float) (pass.getRotationYawHead() * Math.PI / 180.0F);
            float sp = (float) this.squid.getRotPitch();
            float sy = (float) this.squid.getRotYaw();
            if(Math.abs(pp - sp) >= 0.005 || Math.abs(py - sy) >= 0.005) {
                this.squid.setTargetRotPitch(pp);
                this.squid.setTargetRotYaw(py);
                return true;
            }
            return false;
        }
        else {
            //Random doubles between -PI and PI, added to current rotation
            this.squid.setTargetRotPitch(this.squid.getRotPitch() + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
            this.squid.setTargetRotYaw(this.squid.getRotYaw() + (this.r.nextDouble() * Math.PI * (this.r.nextBoolean() ? 1 : -1)));
            return true;
        }
    }

    /**
     * When the current action (swimming or turning) is finished (approximately),
     * decides which action to take next.
     * Odds:
     * 1/12 - starts to shake (hands over to ShakeGoal)
     * 4/12 - repeats action
     * 7/12 - goes from turning to swimming forward or vice versa
     */
    @Override
    public void tick() {
        //Code for testing squid swimming and visuals.
        //If all uncommented, will swim in an octagon without shaking.
//		if(this.turning)
//		{
//            double rp = this.squid.getRotPitch();
//            double trp = this.squid.getTargRotPitch();
//            double ry = this.squid.getRotYaw();
//            double Try = this.squid.getTargRotYaw();
//            if (Math.abs(Try - ry) < 0.005 && Math.abs(trp - rp) < 0.005)
//            {
//                this.squid.addForce(0.25);
//                this.turning = false;
//            }
//        }
//        else
//        {
//            if(Math.abs(this.squid.motionX) < 0.005 && Math.abs(this.squid.motionY) < 0.005
//                    && Math.abs(this.squid.motionZ) < 0.005)
//            {
//                if (this.goHorizontal)
//                {
//                    this.squid.setTargetRotPitch(Math.PI / 2);
//                    if (this.currentAngle == 7)
//                    {
//                        this.currentAngle = 0;
//                    }
//                    else
//                    {
//                        ++this.currentAngle;
//                    }
//                    this.squid.setTargetRotYaw(this.angles[this.currentAngle]);
//                }
//                else
//                {
//                    this.squid.setTargetRotYaw(0);
//                    if (this.currentAngle == 7)
//                    {
//                        this.currentAngle = 0;
//                    }
//                    else
//                    {
//                        ++this.currentAngle;
//                    }
//                    this.squid.setTargetRotPitch(this.angles[this.currentAngle]);
//                }
//                this.turning = true;
//            }
//        }


        double rp = this.squid.getRotPitch();
        double ry = this.squid.getRotYaw();
        if(this.squid.getBlastToStatue()) {
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
            boolean hasVIPRider = this.squid.hasVIPRider();
            if (this.turning) {
                double trp = this.squid.getTargRotPitch();
                double Try = this.squid.getTargRotYaw();
                if (Math.abs(trp - rp) < 0.0005 && Math.abs(Try - ry) < 0.0005) {
                    this.playNextNote();
                    //The last turn is as good as finished
                    int randomInt = this.r.nextInt(12);
                    if (randomInt == 0) {
                        this.squid.setShaking(true);
                    } else {
                        if (hasVIPRider) {
                            if (!this.doTurn(true)) {
                                //Squid is pointing roughly where the rider is facing
                                this.squid.addForce(this.swimForce);
                                this.turning = false;
                            }
                        } else {
                            if (randomInt < 5) {
                                this.doTurn(false);
                            } else {
                                this.squid.addForce(this.swimForce);
                                this.turning = false;
                            }
                        }
                    }
                }
            } else {
                Vec3d motion = this.squid.getMotion();
                if (Math.abs(motion.x) < 0.005 && Math.abs(motion.y) < 0.005
                        && Math.abs(motion.z) < 0.005) {
                    this.playNextNote();
                    //Last forward swim is as good as finished
                    int randomInt = this.r.nextInt(12);
                    if (randomInt == 0) {
                        this.squid.setShaking(true);
                    } else {
                        if (hasVIPRider) {
                            if (this.doTurn(true)) {
                                this.turning = true;
                            } else {
                                //Squid is pointing roughly where the rider is facing
                                this.squid.addForce(this.swimForce);
                            }
                        } else {
                            if (randomInt < 5) {
                                this.squid.addForce(this.swimForce);
                            } else {
                                this.doTurn(hasVIPRider);
                                this.turning = true;
                            }
                        }
                    }
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
