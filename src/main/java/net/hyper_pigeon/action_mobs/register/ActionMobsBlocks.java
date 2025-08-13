package net.hyper_pigeon.action_mobs.register;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.block.ActionMobBlock;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Function;

public class ActionMobsBlocks {
    public static final Block ACTION_MOB_BLOCK = registerBlock(
            "action_mob",
            ActionMobBlock::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE),
            true
    );

    public static final BlockEntityType<ActionMobBlockEntity> ACTION_MOB_BLOCK_ENTITY = registerBlockEntity(
            "action_mob",
            FabricBlockEntityTypeBuilder.create(ActionMobBlockEntity::new, ACTION_MOB_BLOCK).build()
    );

    public static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(ActionMobs.MOD_ID, "action_mobs"), FabricItemGroup.builder()
            .icon(() -> new ItemStack(ACTION_MOB_BLOCK.asItem()))
            .displayName(Text.translatable("itemGroup.action_mobs.items"))
            .entries((context, entries) -> {
            })
            .build());



    public static void initialize(){}


    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            if(block instanceof ActionMobBlock actionMobBlock) {
                BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey)) {
                    @Override
                    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent
                            displayComponent, Consumer<Text> textConsumer, TooltipType type) {
                        actionMobBlock.appendTooltip(stack, context, displayComponent, textConsumer, type);
                    }
                };
                Registry.register(Registries.ITEM, itemKey, blockItem);
            }
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ActionMobs.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ActionMobs.MOD_ID, name));
    }

    public static <T extends BlockEntityType<?>> T registerBlockEntity(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ActionMobs.MOD_ID, path), blockEntityType);
    }


}
