// Copyright 2016-2022, 2025-2026 FredTargaryen and contributors
// See README.md for full copyright notice and contributor info
package com.fredtargaryen.rocketsquids.client.particle;

import com.fredtargaryen.rocketsquids.RSParticleTypes;
import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.util.color.ColorHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Almost all code here was copied from FireworkParticle as I couldn't extend
 */
@OnlyIn(Dist.CLIENT)
public class SquidFireworkParticle {
    @OnlyIn(Dist.CLIENT)
    static class Spark extends SimpleAnimatedParticle {
        private boolean trail;
        private boolean twinkle;
        private final ParticleEngine engine;
        private float fadeR;
        private float fadeG;
        private float fadeB;
        private boolean hasFade;

        private Spark(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ParticleEngine engine, SpriteSet sprites) {
            super(world, x, y, z, sprites, -0.004F);
            this.gravity = -0.004F;
            this.xd = xSpeed;
            this.yd = ySpeed;
            this.zd = zSpeed;
            this.engine = engine;
            this.quadSize *= 0.75F;
            this.lifetime = 48 + this.random.nextInt(12);
            this.setSpriteFromAge(sprites);
        }

        public void setTrail(boolean trailIn) {
            this.trail = trailIn;
        }

        public void setTwinkle(boolean twinkleIn) {
            this.twinkle = twinkleIn;
        }

        @Override
        public void extract(QuadParticleRenderState p_451199_, Camera p_446370_, float p_445772_) {
            if (!this.twinkle || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
                super.extract(p_451199_, p_446370_, p_445772_);
            }
        }

        public void tick() {
            super.tick();
            if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
                SquidFireworkParticle.Spark fireworkparticle$spark = new SquidFireworkParticle.Spark(
                        this.level, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D, this.engine, this.sprites);
                fireworkparticle$spark.setAlpha(0.99F);
                fireworkparticle$spark.setColor(this.rCol, this.gCol, this.bCol);
                fireworkparticle$spark.age = fireworkparticle$spark.lifetime / 2;
                if (this.hasFade) {
                    fireworkparticle$spark.hasFade = true;
                    fireworkparticle$spark.fadeR = this.fadeR;
                    fireworkparticle$spark.fadeG = this.fadeG;
                    fireworkparticle$spark.fadeB = this.fadeB;
                }

                fireworkparticle$spark.twinkle = this.twinkle;
                this.engine.add(fireworkparticle$spark);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SparkProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SparkProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                @NotNull SimpleParticleType typeIn,
                @NotNull ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                RandomSource random
        ) {
            SquidFireworkParticle.Spark fireworkparticle$spark = new SquidFireworkParticle.Spark(
                    level, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getInstance().particleEngine, this.sprites);
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
            this.explosions = RocketSquidsBase.firework.getList("Explosions").orElse(new ListTag());
            if (this.explosions.isEmpty()) {
                this.explosions = null;
            } else {
                this.lifetime = this.explosions.size() * 2 - 1;

                for (int i = 0; i < this.explosions.size(); ++i) {
                    CompoundTag compoundnbt = this.explosions.getCompound(i).orElse(new CompoundTag());
                    if (compoundnbt.getBoolean("Flicker").orElse(true)) {
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
                        CompoundTag nbttagcompound = this.explosions.getCompound(i).orElse(new CompoundTag());

                        if (nbttagcompound.getByte("Type").orElse((byte) 1) == 1) {
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
                CompoundTag nbttagcompound1 = this.explosions.getCompound(k).orElse(new CompoundTag());
                boolean flag4 = nbttagcompound1.getBoolean("Trail").orElse(false);
                boolean flag2 = nbttagcompound1.getBoolean("Flicker").orElse(true);
                int[] aint = nbttagcompound1.getIntArray("Colors").orElse(new int[]{
                        ColorHelper.WHITE
                });
                int[] aint1 = nbttagcompound1.getIntArray("FadeColors").orElse(new int[]{
                        ColorHelper.WHITE
                });

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
                        aint, aint1, flag4, flag2);

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
            return minecraft.gameRenderer.getMainCamera().position().distanceToSqr(this.x, this.y, this.z) >= 256.0D;
        }

        private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
            double d0 = shape[0][0];
            double d1 = shape[0][1];
            this.createParticle(this.x, this.y, this.z, d0 * speed, d1 * speed, 0.0D, colours, fadeColours, trail, twinkleIn);
            float f = this.random.nextFloat() * (float) Math.PI;
            double d2 = 0.034D;

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
            SquidFireworkParticle.Spark fireworkparticle$spark = (SquidFireworkParticle.Spark) this.manager.createParticle((ParticleOptions) RSParticleTypes.FIREWORK_TYPE.get(), x, y, z, xMotion, yMotion, zMotion);
            assert fireworkparticle$spark != null;
            fireworkparticle$spark.setTrail(trail);
            fireworkparticle$spark.setTwinkle(twinkle);
            int i = this.random.nextInt(colorSpark.length);
            fireworkparticle$spark.setColor(colorSpark[i]);
            if (colorSparkFade.length > 0) {
                fireworkparticle$spark.setFadeColor(colorSparkFade[this.random.nextInt(colorSparkFade.length)]);
            }
        }
    }
}
