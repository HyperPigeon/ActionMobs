package net.hyper_pigeon.action_mobs.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public record UpdateActionMobOffset(Vector3f offsetVector) {
    public static final Codec<UpdateActionMobOffset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.VECTOR_3F.fieldOf("offset_vector").forGetter(UpdateActionMobOffset::offsetVector)
    ).apply(instance, UpdateActionMobOffset::new));

    public static final PacketCodec<ByteBuf, UpdateActionMobOffset> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, UpdateActionMobOffset::offsetVector,
            UpdateActionMobOffset::new
    );
}
