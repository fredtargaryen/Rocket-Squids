package com.fredtargaryen.rocketsquids.level.entity;

import com.fredtargaryen.rocketsquids.network.message.TrickMessage;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TrickParameters(Byte forwardAxis, Byte sideAxis) {
    public static TrickParameters createFromClientInput(ClientInput input) {
        byte forwardAxis = 0;
        byte sideAxis = 0;
        if (input.keyPresses.forward()) ++forwardAxis;
        if (input.keyPresses.backward()) --forwardAxis;
        // Might catch you out one day - positive X is to the left of the squid
        if (input.keyPresses.left()) ++sideAxis;
        if (input.keyPresses.right()) --sideAxis;
        return new TrickParameters(forwardAxis, sideAxis);
    }

    public static final StreamCodec<FriendlyByteBuf, TrickParameters> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE, TrickParameters::forwardAxis,
                    ByteBufCodecs.BYTE, TrickParameters::sideAxis,
                    TrickParameters::new);
}
