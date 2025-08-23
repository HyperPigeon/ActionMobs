package net.hyper_pigeon.action_mobs.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record UpdateActionMobEquipment(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    public static final Codec<UpdateActionMobEquipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("equipment_slot").forGetter(UpdateActionMobEquipment::equipmentSlot),
            ItemStack.OPTIONAL_CODEC.fieldOf("item_stack").forGetter(UpdateActionMobEquipment::itemStack)
    ).apply(instance, UpdateActionMobEquipment::new));

    public static final PacketCodec<RegistryByteBuf, UpdateActionMobEquipment> PACKET_CODEC = PacketCodec.tuple(
            EquipmentSlot.PACKET_CODEC,
            UpdateActionMobEquipment::equipmentSlot,
            ItemStack.OPTIONAL_PACKET_CODEC,
            UpdateActionMobEquipment::itemStack,
            UpdateActionMobEquipment::new
    );

}
