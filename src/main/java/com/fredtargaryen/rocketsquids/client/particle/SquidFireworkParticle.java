package com.fredtargaryen.rocketsquids.client.particle;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Almost all code here was copied from FireworkParticle as I couldn't extend
 */
@OnlyIn(Dist.CLIENT)
public class SquidFireworkParticle {
    @OnlyIn(Dist.CLIENT)
    public static class OverlayCopy extends SpriteTexturedParticle {
        private OverlayCopy(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
            super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
            this.maxAge = 4;
        }

        public IParticleRenderType func_217558_b() {
            return IParticleRenderType.field_217603_c;
        }

        /**
         * Renders the particle
         */
        public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            this.setAlphaF(0.6F - ((float)this.age + partialTicks - 1.0F) * 0.25F * 0.5F);
            super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        }

        public float func_217561_b(float p_217561_1_) {
            return 7.1F * MathHelper.sin(((float)this.age + p_217561_1_ - 1.0F) * 0.25F * (float)Math.PI);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OverlayCopyFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite field_217529_a;

        public OverlayCopyFactory(IAnimatedSprite p_i50889_1_) {
            this.field_217529_a = p_i50889_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SquidFireworkParticle.OverlayCopy fireworkparticle$overlay = new SquidFireworkParticle.OverlayCopy(worldIn, x, y, z);
            fireworkparticle$overlay.func_217568_a(this.field_217529_a);
            return fireworkparticle$overlay;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Spark extends SimpleAnimatedParticle {
        private boolean trail;
        private boolean twinkle;
        private final ParticleManager effectRenderer;
        private float fadeColourRed;
        private float fadeColourGreen;
        private float fadeColourBlue;
        private boolean hasFadeColour;

        private Spark(World p_i50884_1_, double p_i50884_2_, double p_i50884_4_, double p_i50884_6_, double p_i50884_8_, double p_i50884_10_, double p_i50884_12_, ParticleManager p_i50884_14_, IAnimatedSprite p_i50884_15_) {
            super(p_i50884_1_, p_i50884_2_, p_i50884_4_, p_i50884_6_, p_i50884_15_, -0.004F);
            this.motionX = p_i50884_8_;
            this.motionY = p_i50884_10_;
            this.motionZ = p_i50884_12_;
            this.effectRenderer = p_i50884_14_;
            this.particleScale *= 0.75F;
            this.maxAge = 48 + this.rand.nextInt(12);
            this.func_217566_b(p_i50884_15_);
        }

        public void setTrail(boolean trailIn) {
            this.trail = trailIn;
        }

        public void setTwinkle(boolean twinkleIn) {
            this.twinkle = twinkleIn;
        }

        /**
         * Renders the particle
         */
        public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
            if (!this.twinkle || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
                super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
            }

        }

        public void tick() {
            super.tick();
            if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
                SquidFireworkParticle.Spark fireworkparticle$spark = new SquidFireworkParticle.Spark(this.world, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.effectRenderer, this.field_217584_C);
                fireworkparticle$spark.setAlphaF(0.99F);
                fireworkparticle$spark.setColor(this.particleRed, this.particleGreen, this.particleBlue);
                fireworkparticle$spark.age = fireworkparticle$spark.maxAge / 2;
                if (this.hasFadeColour) {
                    fireworkparticle$spark.hasFadeColour = true;
                    fireworkparticle$spark.fadeColourRed = this.fadeColourRed;
                    fireworkparticle$spark.fadeColourGreen = this.fadeColourGreen;
                    fireworkparticle$spark.fadeColourBlue = this.fadeColourBlue;
                }

                fireworkparticle$spark.twinkle = this.twinkle;
                this.effectRenderer.addEffect(fireworkparticle$spark);
            }

        }

        public void setSlightAlpha() {
            this.setAlphaF(0.99F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SparkFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite field_217530_a;

        public SparkFactory(IAnimatedSprite p_i50883_1_) {
            this.field_217530_a = p_i50883_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SquidFireworkParticle.Spark fireworkparticle$spark = new SquidFireworkParticle.Spark(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getInstance().particles, this.field_217530_a);
            fireworkparticle$spark.setSlightAlpha();
            return fireworkparticle$spark;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SquidStarter extends MetaParticle {
        private int age;
        private final ParticleManager effects;
        private ListNBT explosions;
        private boolean twinkle;
        private static final double squareLength = 1.0 / 7.0;

        public SquidStarter(World world, double x, double y, double z, ParticleManager manager) {
            super(world, x, y, z);
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.effects = manager;
            this.maxAge = 8;
            this.explosions = RocketSquidsBase.firework.getList("Explosions", 10);
            if (this.explosions.isEmpty()) {
                this.explosions = null;
            } else {
                this.maxAge = this.explosions.size() * 2 - 1;

                for (int i = 0; i < this.explosions.size(); ++i) {
                    CompoundNBT compoundnbt = this.explosions.getCompound(i);
                    if (compoundnbt.getBoolean("Flicker")) {
                        this.twinkle = true;
                        this.maxAge += 15;
                        break;
                    }
                }
            }
        }

        @Override
        public void tick() {
            if (this.age == 0 && this.explosions != null) {
                //Is the firework far away
                boolean flag = this.isFarFromCamera();
                //Is the firework big
                boolean flag1 = false;

                if (this.explosions.size() >= 3) {
                    flag1 = true;
                } else {
                    for (int i = 0; i < this.explosions.size(); ++i) {
                        CompoundNBT nbttagcompound = this.explosions.getCompound(i);

                        if (nbttagcompound.getByte("Type") == 1) {
                            flag1 = true;
                            break;
                        }
                    }
                }

                SoundEvent soundevent1;

                if (flag1) {
                    soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
                } else {
                    soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
                }

                this.world.playSound(this.posX, this.posY, this.posZ, soundevent1, SoundCategory.AMBIENT, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
            }

            if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.size()) {
                int k = this.age / 2;
                CompoundNBT nbttagcompound1 = this.explosions.getCompound(k);
                boolean flag4 = nbttagcompound1.getBoolean("Trail");
                boolean flag2 = nbttagcompound1.getBoolean("Flicker");
                int[] aint = nbttagcompound1.getIntArray("Colors");
                int[] aint1 = nbttagcompound1.getIntArray("FadeColors");

                if (aint.length == 0) {
                    aint = new int[]{0};
                }

                this.createShaped(0.5D, new double[][]{
                                //Middle leg
                                {0.0D, -1.0D}, {0.5 * squareLength, -1.0D}, {0.5 * squareLength, 0.0D},
                                //Underneath
                                {2.5 * squareLength, 0.0D},
                                //Side leg
                                {2.5 * squareLength, -1.0D}, {3.5 * squareLength, -1.0D}, {3.5 * squareLength, 1.0},
                                //Top of head
                                {0.0, 7 * squareLength}},
                        aint, aint1, flag4, flag2, true);

                int j = aint[0];
                float f = (float) ((j & 16711680) >> 16) / 255.0F;
                float f1 = (float) ((j & 65280) >> 8) / 255.0F;
                float f2 = (float) (j & 255) / 255.0F;
                OverlayCopy o = new OverlayCopy(this.world, this.posX, this.posY, this.posZ);
                o.setColor(f, f1, f2);
                this.effects.addEffect(o);
            }

            ++this.age;

            if (this.age > this.maxAge) {
                boolean flag3 = this.isFarFromCamera();
                SoundEvent soundevent = flag3 ? SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE;
                this.world.playSound(this.posX, this.posY, this.posZ, soundevent, SoundCategory.AMBIENT, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
                this.setExpired();
            }
        }

        private boolean isFarFromCamera() {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.gameRenderer.func_215316_n().func_216785_c().squareDistanceTo(this.posX, this.posY, this.posZ) >= 256.0D;
        }

        private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean p_92038_8_) {
            double d0 = shape[0][0];
            double d1 = shape[0][1];
            this.createParticle(this.posX, this.posY, this.posZ, d0 * speed, d1 * speed, 0.0D, colours, fadeColours, trail, twinkleIn);
            float f = this.rand.nextFloat() * (float) Math.PI;
            double d2 = p_92038_8_ ? 0.034D : 0.34D;

            for (int i = 0; i < 3; ++i) {
                double d3 = (double) f + (double) ((float) i * (float) Math.PI) * d2;
                double d4 = d0;
                double d5 = d1;

                for (int j = 1; j < shape.length; ++j) {
                    double d6 = shape[j][0];
                    double d7 = shape[j][1];

                    for (double d8 = 0.25D; d8 <= 1.0D; d8 += 0.25D) {
                        double d9 = (d4 + (d6 - d4) * d8) * speed;
                        double d10 = (d5 + (d7 - d5) * d8) * speed;
                        double d11 = d9 * Math.sin(d3);
                        d9 = d9 * Math.cos(d3);

                        for (double d12 = -1.0D; d12 <= 1.0D; d12 += 2.0D) {
                            this.createParticle(this.posX, this.posY, this.posZ, d9 * d12, d10, d11 * d12, colours, fadeColours, trail, twinkleIn);
                        }
                    }

                    d4 = d6;
                    d5 = d7;
                }
            }
        }

        /**
         * Creates a single particle.
         */
        private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
            SquidFireworkParticle.Spark particlefirework$spark = (SquidFireworkParticle.Spark)this.effects.addParticle(RocketSquidsBase.FIREWORK_TYPE, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_);
            particlefirework$spark.setSlightAlpha();
            particlefirework$spark.setTrail(p_92034_15_);
            particlefirework$spark.setTwinkle(p_92034_16_);
            int i = this.rand.nextInt(p_92034_13_.length);
            particlefirework$spark.setColor(p_92034_13_[i]);

            if (p_92034_14_ != null && p_92034_14_.length > 0) {
                particlefirework$spark.setColorFade(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
            }

            this.effects.addEffect(particlefirework$spark);
        }
    }
}
