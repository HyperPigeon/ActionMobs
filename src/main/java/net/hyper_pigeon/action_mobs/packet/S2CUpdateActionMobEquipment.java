package net.hyper_pigeon.action_mobs.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record S2CUpdateActionMobEquipment(BlockPos blockPos, UpdateActionMobEquipment updateActionMobEquipment) implements CustomPayload{
    public static final CustomPayload.Id<S2CUpdateActionMobEquipment> ID = new CustomPayload.Id<>(Identifier.of(ActionMobs.MOD_ID, "channel.action_mob_equipment"));

    public static final PacketCodec<RegistryByteBuf, S2CUpdateActionMobEquipment> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, S2CUpdateActionMobEquipment::blockPos,
            UpdateActionMobEquipment.PACKET_CODEC, S2CUpdateActionMobEquipment::updateActionMobEquipment,
            S2CUpdateActionMobEquipment::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void receive(ClientPlayNetworking.Context context) {
        syncStatueEntity(context.player().getWorld().getBlockEntity(this.blockPos()));
    }

    private void syncStatueEntity(BlockEntity blockEntity) {
        if (blockEntity instanceof ActionMobBlockEntity be) {
            LivingEntity statueEntity = (LivingEntity) be.getStatueEntity();
            EquipmentSlot equipmentSlot = updateActionMobEquipment().equipmentSlot();
            ItemStack itemStack = updateActionMobEquipment().itemStack();
            statueEntity.equipStack(equipmentSlot, ItemStack.EMPTY);
            statueEntity.equipStack(equipmentSlot, itemStack);
        }
    }
}
