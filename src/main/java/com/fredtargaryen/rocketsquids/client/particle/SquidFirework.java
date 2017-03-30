package com.fredtargaryen.rocketsquids.client.particle;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Almost all code here was copied from ParticleFirework as I couldn't extend
 */
@SideOnly(Side.CLIENT)
public class SquidFirework extends Particle
{
    private int age;
    private final ParticleManager effects;
    private final NBTTagList explosions;
    private static final double squareLength = 1.0 / 7.0;

    public SquidFirework(WorldClient world, double x, double y, double z, ParticleManager manager)
    {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.effects = manager;
        this.explosions = RocketSquidsBase.firework.getTagList("Explosions", 10);
    }

    public void onUpdate()
    {
        if (this.age == 0 && this.explosions != null)
        {
            //Is the firework far away
            boolean flag = this.isFarFromCamera();
            //Is the firework big
            boolean flag1 = false;

            if (this.explosions.tagCount() >= 3)
            {
                flag1 = true;
            }
            else
            {
                for (int i = 0; i < this.explosions.tagCount(); ++i)
                {
                    NBTTagCompound nbttagcompound = this.explosions.getCompoundTagAt(i);

                    if (nbttagcompound.getByte("Type") == 1)
                    {
                        flag1 = true;
                        break;
                    }
                }
            }

            SoundEvent soundevent1;

            if (flag1)
            {
                soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_LARGE_BLAST;
            }
            else
            {
                soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_BLAST;
            }

            this.world.playSound(this.posX, this.posY, this.posZ, soundevent1, SoundCategory.AMBIENT, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
        }

        if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.tagCount())
        {
            int k = this.age / 2;
            NBTTagCompound nbttagcompound1 = this.explosions.getCompoundTagAt(k);
            boolean flag4 = nbttagcompound1.getBoolean("Trail");
            boolean flag2 = nbttagcompound1.getBoolean("Flicker");
            int[] aint = nbttagcompound1.getIntArray("Colors");
            int[] aint1 = nbttagcompound1.getIntArray("FadeColors");

            if (aint.length == 0)
            {
                aint = new int[] {ItemDye.DYE_COLORS[0]};
            }

            this.createShaped(0.5D, new double[][] {
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
            float f = (float)((j & 16711680) >> 16) / 255.0F;
            float f1 = (float)((j & 65280) >> 8) / 255.0F;
            float f2 = (float)(j & 255) / 255.0F;
            OverlayCopy o = new OverlayCopy(this.world, this.posX, this.posY, this.posZ);
            o.setRBGColorF(f, f1, f2);
            this.effects.addEffect(o);
        }

        ++this.age;

        if (this.age > this.particleMaxAge)
        {
            boolean flag3 = this.isFarFromCamera();
            SoundEvent soundevent = flag3 ? SoundEvents.ENTITY_FIREWORK_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_TWINKLE;
            this.world.playSound(this.posX, this.posY, this.posZ, soundevent, SoundCategory.AMBIENT, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            this.setExpired();
        }
    }

    private boolean isFarFromCamera()
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft == null || minecraft.getRenderViewEntity() == null || minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0D;
    }

    private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean p_92038_8_)
    {
        double d0 = shape[0][0];
        double d1 = shape[0][1];
        this.createParticle(this.posX, this.posY, this.posZ, d0 * speed, d1 * speed, 0.0D, colours, fadeColours, trail, twinkleIn);
        float f = this.rand.nextFloat() * (float)Math.PI;
        double d2 = p_92038_8_ ? 0.034D : 0.34D;

        for (int i = 0; i < 3; ++i)
        {
            double d3 = (double)f + (double)((float)i * (float)Math.PI) * d2;
            double d4 = d0;
            double d5 = d1;

            for (int j = 1; j < shape.length; ++j)
            {
                double d6 = shape[j][0];
                double d7 = shape[j][1];

                for (double d8 = 0.25D; d8 <= 1.0D; d8 += 0.25D)
                {
                    double d9 = (d4 + (d6 - d4) * d8) * speed;
                    double d10 = (d5 + (d7 - d5) * d8) * speed;
                    double d11 = d9 * Math.sin(d3);
                    d9 = d9 * Math.cos(d3);

                    for (double d12 = -1.0D; d12 <= 1.0D; d12 += 2.0D)
                    {
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
    private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_)
    {
        ParticleFirework.Spark particlefirework$spark = new ParticleFirework.Spark(this.world, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_, this.effects);
        particlefirework$spark.setAlphaF(0.99F);
        particlefirework$spark.setTrail(p_92034_15_);
        particlefirework$spark.setTwinkle(p_92034_16_);
        int i = this.rand.nextInt(p_92034_13_.length);
        particlefirework$spark.setColor(p_92034_13_[i]);

        if (p_92034_14_ != null && p_92034_14_.length > 0)
        {
            particlefirework$spark.setColorFade(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
        }

        this.effects.addEffect(particlefirework$spark);
    }

    public static class OverlayCopy extends Particle
    {
        protected OverlayCopy(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_)
        {
            super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
            this.particleMaxAge = 4;
        }

        /**
         * Renders the particle
         */
        public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
        {
            float f = 0.25F;
            float f1 = 0.5F;
            float f2 = 0.125F;
            float f3 = 0.375F;
            float f4 = 7.1F * MathHelper.sin(((float)this.particleAge + partialTicks - 1.0F) * 0.25F * (float)Math.PI);
            this.setAlphaF(0.6F - ((float)this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F);
            float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
            float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
            float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
            int i = this.getBrightnessForRender(partialTicks);
            int j = i >> 16 & 65535;
            int k = i & 65535;
            worldRendererIn.pos((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).tex(0.5D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRendererIn.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex(0.5D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRendererIn.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex(0.25D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            worldRendererIn.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex(0.25D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }
}
