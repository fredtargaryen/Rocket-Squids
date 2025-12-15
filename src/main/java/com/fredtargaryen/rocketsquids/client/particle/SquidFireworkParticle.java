package com.fredtargaryen.rocketsquids.client.particle;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Almost all code here was copied from FireworkParticle as I couldn't extend
 */
@OnlyIn(Dist.CLIENT)
public class SquidFireworkParticle {
    @OnlyIn(Dist.CLIENT)
    static class Spark extends SimpleAnimatedParticle {
        private boolean trail;
        private boolean twinkle;
        private final ParticleEngine effectRenderer;
        private float fadeColourRed;
        private float fadeColourGreen;
        private float fadeColourBlue;
        private boolean hasFadeColour;

        private Spark(ClientLevel world, double d, double e, double f, double g, double h, double i, ParticleEngine particleengine, SpriteSet sprites) {
            super(world, d, e, f, sprites, -0.004F);
            this.xd = g;
            this.yd = h;
            this.zd = i;
            this.effectRenderer = particleengine;
            this.scale(0.75F);
            this.lifetime = 48 + this.random.nextInt(12);
            this.setSpriteFromAge(sprites);
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
        public void render(VertexConsumer buffer, Camera entityIn, float partialTicks) {
            if (!this.twinkle || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
                super.render(buffer, entityIn, partialTicks);
            }

        }

        public void tick() {
            super.tick();
            if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
                SquidFireworkParticle.Spark fireworkparticle$spark = new SquidFireworkParticle.Spark(this.level, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D, this.effectRenderer, this.sprites);
                fireworkparticle$spark.setAlpha(0.99F);
                fireworkparticle$spark.setColor(this.rCol, this.gCol, this.bCol);
                fireworkparticle$spark.age = fireworkparticle$spark.lifetime / 2;
                if (this.hasFadeColour) {
                    fireworkparticle$spark.hasFadeColour = true;
                    fireworkparticle$spark.fadeColourRed = this.fadeColourRed;
                    fireworkparticle$spark.fadeColourGreen = this.fadeColourGreen;
                    fireworkparticle$spark.fadeColourBlue = this.fadeColourBlue;
                }

                fireworkparticle$spark.twinkle = this.twinkle;
                this.effectRenderer.add(fireworkparticle$spark);
            }

        }

        public void setSlightAlpha() {
            this.setAlpha(0.99F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SparkFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SparkFactory(SpriteSet spriteSetIn) {
            this.spriteSet = spriteSetIn;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SquidFireworkParticle.Spark fireworkparticle$spark = new SquidFireworkParticle.Spark(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getInstance().particleEngine, this.spriteSet);
            fireworkparticle$spark.setSlightAlpha();
            return fireworkparticle$spark;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SquidStarter extends NoRenderParticle {
        private int age;
        private final ParticleEngine manager;
        private ListTag explosions;
        private static final double squareLength = 1.0 / 7.0;

        public SquidStarter(ClientLevel world, double x, double y, double z, ParticleEngine manager) {
            super(world, x, y, z);
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            this.manager = manager;
            this.lifetime = 8;
            this.explosions = RocketSquidsBase.firework.getList("Explosions", 10);
            if (this.explosions.isEmpty()) {
                this.explosions = null;
            } else {
                this.lifetime = this.explosions.size() * 2 - 1;

                for (int i = 0; i < this.explosions.size(); ++i) {
                    CompoundTag compoundnbt = this.explosions.getCompound(i);
                    if (compoundnbt.getBoolean("Flicker")) {
                        boolean twinkle = true;
                        this.lifetime += 15;
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
                        CompoundTag nbttagcompound = this.explosions.getCompound(i);

                        if (nbttagcompound.getByte("Type") == 1) {
                            flag1 = true;
                            break;
                        }
                    }
                }

                SoundEvent soundevent1;

                if (flag1) {
                    soundevent1 = flag ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
                } else {
                    soundevent1 = flag ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
                }

                this.level.playLocalSound(this.x, this.y, this.z, soundevent1, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
            }

            if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.size()) {
                int k = this.age / 2;
                CompoundTag nbttagcompound1 = this.explosions.getCompound(k);
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
            }

            ++this.age;

            if (this.age > this.lifetime) {
                boolean flag3 = this.isFarFromCamera();
                SoundEvent soundevent = flag3 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
                this.level.playLocalSound(this.x, this.y, this.z, soundevent, SoundSource.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }
            this.remove();
        }

        private boolean isFarFromCamera() {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0D;
        }

        private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean p_92038_8_) {
            double d0 = shape[0][0];
            double d1 = shape[0][1];
            this.createParticle(this.x, this.y, this.z, d0 * speed, d1 * speed, 0.0D, colours, fadeColours, trail, twinkleIn);
            float f = this.random.nextFloat() * (float) Math.PI;
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
                            this.createParticle(this.x, this.y, this.z, d9 * d12, d10, d11 * d12, colours, fadeColours, trail, twinkleIn);
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
        private void createParticle(double x, double y, double z, double xMotion, double yMotion, double zMotion, int[] colorSpark, int[] colorSparkFade, boolean trail, boolean twinkle) {
            SquidFireworkParticle.Spark fireworkparticle$spark = (SquidFireworkParticle.Spark) this.manager.createParticle((ParticleOptions) RocketSquidsBase.FIREWORK_TYPE.get(), x, y, z, xMotion, yMotion, zMotion);
            assert fireworkparticle$spark != null;
            fireworkparticle$spark.setTrail(trail);
            fireworkparticle$spark.setTwinkle(twinkle);
            fireworkparticle$spark.setSlightAlpha();
            int i = this.random.nextInt(colorSpark.length);
            fireworkparticle$spark.setColor(colorSpark[i]);
            if (colorSparkFade.length > 0) {
                fireworkparticle$spark.setFadeColor(colorSparkFade[this.random.nextInt(colorSparkFade.length)]);
            }
        }
    }
}
