package net.hyper_pigeon.action_mobs.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record UpdateActionMobBlockScale(float newScale) {
    public static final Codec<UpdateActionMobBlockScale> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("scale").forGetter(UpdateActionMobBlockScale::newScale)
    ).apply(instance, UpdateActionMobBlockScale::new));

    public static final PacketCodec<ByteBuf, UpdateActionMobBlockScale> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, UpdateActionMobBlockScale::newScale,
            UpdateActionMobBlockScale::new
    );
}
