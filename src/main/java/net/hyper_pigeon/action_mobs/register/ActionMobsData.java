package net.hyper_pigeon.action_mobs.register;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.statue_type.StatueType;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ActionMobsData {
    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Identifier.of(ActionMobs.MOD_ID, "statue_type"), StatueTypeDataLoader::new);
    }
}
