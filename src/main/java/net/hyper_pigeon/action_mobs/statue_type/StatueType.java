package net.hyper_pigeon.action_mobs.statue_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.List;

public class StatueType {
    private final Item creationItem;
    private final EntityType entityType;

    private final List<String> poseablePartNames;

    public static final Codec<StatueType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registries.ITEM.getCodec().fieldOf("creation_item").forGetter(StatueType::getCreationItem),
            Registries.ENTITY_TYPE.getCodec().fieldOf("entity_type").forGetter(StatueType::getEntityType),
            Codec.list(Codec.STRING).fieldOf("poseable_parts").forGetter(StatueType::getPoseablePartNames)
            // Up to 16 fields can be declared here
    ).apply(instance, StatueType::new));
    public StatueType(Item creationItem, EntityType entityType, List<String> poseablePartNames) {
        this.creationItem = creationItem;
        this.entityType = entityType;
        this.poseablePartNames = poseablePartNames;
    }
    public Item getCreationItem() {
        return creationItem;
    }
    public EntityType getEntityType() {
        return entityType;
    }

    public List<String> getPoseablePartNames() {
        return poseablePartNames;
    }
}
