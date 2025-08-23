package net.hyper_pigeon.action_mobs.mixin;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityMixin {
    @Accessor("equipment")
    EntityEquipment getEquipment();
}
