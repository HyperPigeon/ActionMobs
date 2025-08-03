package net.hyper_pigeon.action_mobs.client;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.*;

public class ActionMobsModelGenerator extends FabricModelProvider {
    public static final TexturedModel.Factory PARTICLE_FACTORY = TexturedModel.makeFactory(block -> TextureMap.all(Blocks.BEDROCK), Models.PARTICLE);
    public ActionMobsModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(ActionMobsBlocks.ACTION_MOB_BLOCK, PARTICLE_FACTORY);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ActionMobsBlocks.ACTION_MOB_BLOCK.asItem(), Models.GENERATED);
    }
}
