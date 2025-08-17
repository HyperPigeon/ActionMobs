package net.hyper_pigeon.action_mobs.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record UpdateActionBlockMobIsBaby(boolean isBaby) {
    public static final Codec<UpdateActionBlockMobIsBaby> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("is_baby").forGetter(UpdateActionBlockMobIsBaby::isBaby)
    ).apply(instance, UpdateActionBlockMobIsBaby::new));

    public static final PacketCodec<ByteBuf, UpdateActionBlockMobIsBaby> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, UpdateActionBlockMobIsBaby::isBaby,
            UpdateActionBlockMobIsBaby::new
    );

    @Override
    public boolean isBaby() {
        return isBaby;
    }
}
