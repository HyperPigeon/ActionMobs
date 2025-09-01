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

public record C2SUpdateActionMobOffset(BlockPos blockPos, UpdateActionMobOffset updateActionMobOffset) implements CustomPayload {

    public static final CustomPayload.Id<C2SUpdateActionMobOffset> ID = new CustomPayload.Id<>(Identifier.of(ActionMobs.MOD_ID, "channel.action_mob_offset"));

    public static final PacketCodec<RegistryByteBuf, C2SUpdateActionMobOffset> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, C2SUpdateActionMobOffset::blockPos,
            UpdateActionMobOffset.PACKET_CODEC, C2SUpdateActionMobOffset::updateActionMobOffset,
            C2SUpdateActionMobOffset::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void receive(ServerPlayNetworking.Context context) {
        syncOffset(context.player().getWorld(), context.player().getWorld().getBlockEntity(this.blockPos()));
    }

    public void syncOffset (ServerWorld world, BlockEntity blockEntity) {
        if (blockEntity instanceof ActionMobBlockEntity be) {
            be.updateOffset(updateActionMobOffset());
        }
    }
}
