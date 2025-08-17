package net.hyper_pigeon.action_mobs.statue_type;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class StatueTypeDataLoader extends JsonDataLoader<StatueType> implements IdentifiableResourceReloadListener {
    private final RegistryWrapper.WrapperLookup registries;

    public static final Map<Item, StatueType> statueTypesByItem = new HashMap<>();
    public static final Map<EntityType<?>, StatueType> statueTypesByEntityType = new HashMap<>();

    public static final RegistryKey<Registry<StatueType>> STATUE_REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of("statue_type"));
    public StatueTypeDataLoader(RegistryWrapper.WrapperLookup registries) {
        super(registries, StatueType.CODEC, STATUE_REGISTRY_KEY);
        this.registries = registries;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(ActionMobs.MOD_ID, "statue_type");
    }


    @Override
    protected void apply(Map<Identifier, StatueType> prepared, ResourceManager manager, Profiler profiler) {
        statueTypesByItem.clear();
        statueTypesByEntityType.clear();
        prepared.forEach((location, element) -> {
            try {
              statueTypesByItem.put(element.getCreationItem(), element);
              statueTypesByEntityType.put(element.getEntityType(),element);
            }
            catch(Exception e) {
                ActionMobs.LOGGER.error("Could not load statue type at location '{}'. (Skipping). {}", location, e);
            }


        });
    }
}
