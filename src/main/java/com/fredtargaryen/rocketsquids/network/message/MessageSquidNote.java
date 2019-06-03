package com.fredtargaryen.rocketsquids.network.message;

import com.fredtargaryen.rocketsquids.RocketSquidsBase;
import com.fredtargaryen.rocketsquids.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
import java.util.function.Supplier;

public class MessageSquidNote {
    private byte note;

    public MessageSquidNote() {}

    public MessageSquidNote(byte note) {
        this.note = note;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            EntityPlayer ep = ctx.get().getSender();
            //Check player is wearing the conch
            Iterable<ItemStack> armour = ep.getArmorInventoryList();
            Iterator<ItemStack> iter = armour.iterator();
            iter.next();
            iter.next();
            iter.next();
            ItemStack helmet = iter.next();
            if(helmet.getItem() == RocketSquidsBase.ITEM_CONCH) {
                ep.world.playSound(ep.posX, ep.posY, ep.posZ, Sounds.CONCH_NOTES[this.note], SoundCategory.NEUTRAL, 1.0F, 1.0F, true);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageSquidNote(ByteBuf buf) {
        this.note = buf.readByte();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.note);
    }
}
