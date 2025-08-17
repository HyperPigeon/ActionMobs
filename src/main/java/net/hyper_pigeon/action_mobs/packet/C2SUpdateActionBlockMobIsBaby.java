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

public record C2SUpdateActionBlockMobIsBaby(BlockPos blockPos, UpdateActionBlockMobIsBaby updateActionBlockMobIsBaby) implements CustomPayload {
    public static final CustomPayload.Id<C2SUpdateActionBlockMobIsBaby> ID = new CustomPayload.Id<>(Identifier.of(ActionMobs.MOD_ID, "channel.action_mob_is_baby"));

    public static final PacketCodec<RegistryByteBuf, C2SUpdateActionBlockMobIsBaby> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, C2SUpdateActionBlockMobIsBaby::blockPos,
            UpdateActionBlockMobIsBaby.PACKET_CODEC, C2SUpdateActionBlockMobIsBaby::updateActionBlockMobIsBaby,
            C2SUpdateActionBlockMobIsBaby::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void receive(ServerPlayNetworking.Context context) {
        sync(context.player().getWorld(), context.player().getWorld().getBlockEntity(this.blockPos()));
    }

    public void sync (ServerWorld world, BlockEntity blockEntity) {
        if (blockEntity instanceof ActionMobBlockEntity be) {
            be.updateIsBaby(updateActionBlockMobIsBaby());
        }
    }
}
