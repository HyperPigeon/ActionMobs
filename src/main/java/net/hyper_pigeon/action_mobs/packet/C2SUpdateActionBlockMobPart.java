package net.hyper_pigeon.action_mobs.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record C2SUpdateActionBlockMobPart(BlockPos blockPos, UpdateActionBlockMobPart updateActionBlockMobPart) implements CustomPayload {
    public static final CustomPayload.Id<C2SUpdateActionBlockMobPart> ID = new CustomPayload.Id<>(Identifier.of(ActionMobs.MOD_ID, "channel.action_mob_part"));
    public static final PacketCodec<RegistryByteBuf, C2SUpdateActionBlockMobPart> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, C2SUpdateActionBlockMobPart::blockPos,
            UpdateActionBlockMobPart.PACKET_CODEC, C2SUpdateActionBlockMobPart::updateActionBlockMobPart,
            C2SUpdateActionBlockMobPart::new
    );

    public C2SUpdateActionBlockMobPart(BlockPos blockPos, UpdateActionBlockMobPart updateActionBlockMobPart) {
        this.blockPos = blockPos;
        this.updateActionBlockMobPart = updateActionBlockMobPart;
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void receive(ServerPlayNetworking.Context context) {
        syncPart(context.player().getWorld(), context.player().getWorld().getBlockEntity(this.blockPos()));
    }

    public void syncPart (ServerWorld world, BlockEntity blockEntity) {
        if (blockEntity instanceof ActionMobBlockEntity be) {
            be.updatePart(updateActionBlockMobPart());
        }
    }
}
