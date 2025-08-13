package net.hyper_pigeon.action_mobs.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public record UpdateActionBlockMobPart(Vector3f newAngles, String partName) {
    public static final Codec<UpdateActionBlockMobPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.VECTOR_3F.fieldOf("new_angles").forGetter(UpdateActionBlockMobPart::newAngles),
            Codec.STRING.fieldOf("part_name").forGetter(UpdateActionBlockMobPart::partName)
    ).apply(instance, UpdateActionBlockMobPart::new));

    public static final PacketCodec<ByteBuf, UpdateActionBlockMobPart> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F, UpdateActionBlockMobPart::newAngles,
            PacketCodecs.STRING, UpdateActionBlockMobPart::partName,
            UpdateActionBlockMobPart::new
    );

    public Vector3f getNewAngles() {
        return newAngles;
    }

    public String getPartName(){
        return partName;
    }
}
