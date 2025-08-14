package net.hyper_pigeon.action_mobs.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record UpdateActionMobAngle(float newAngle, boolean isPitch) {
    public static final Codec<UpdateActionMobAngle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("new_angle").forGetter(UpdateActionMobAngle::newAngle),
            Codec.BOOL.fieldOf("is_pitch").forGetter(UpdateActionMobAngle::isPitch)
    ).apply(instance, UpdateActionMobAngle::new));

    public static final PacketCodec<ByteBuf, UpdateActionMobAngle> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, UpdateActionMobAngle::newAngle,
            PacketCodecs.BOOLEAN, UpdateActionMobAngle::isPitch,
            UpdateActionMobAngle::new
    );
}
