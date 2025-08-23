package net.hyper_pigeon.action_mobs.mixin;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;

@Mixin(EntityEquipment.class)
public interface EntityEquipmentMixin {
    @Accessor("map")
    EnumMap<EquipmentSlot, ItemStack> getMap();
}
