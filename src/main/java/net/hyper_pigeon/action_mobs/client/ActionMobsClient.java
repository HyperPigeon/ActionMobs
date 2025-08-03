package net.hyper_pigeon.action_mobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.hyper_pigeon.action_mobs.client.render.block.entity.ActionMobBlockEntityRenderer;
import net.hyper_pigeon.action_mobs.client.render.item.ActionMobBlockItemHandRenderer;
import net.hyper_pigeon.action_mobs.client.render.item.ItemHandRenderer;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ActionMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ActionMobsBlocks.ACTION_MOB_BLOCK_ENTITY, ActionMobBlockEntityRenderer::new);
        ItemHandRenderer.register(ActionMobsBlocks.ACTION_MOB_BLOCK.asItem(), new ActionMobBlockItemHandRenderer());
    }
}
