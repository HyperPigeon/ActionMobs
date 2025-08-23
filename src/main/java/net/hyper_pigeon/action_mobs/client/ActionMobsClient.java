package net.hyper_pigeon.action_mobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.client.render.block.entity.ActionMobBlockEntityRenderer;
import net.hyper_pigeon.action_mobs.client.render.item.ActionMobBlockItemHandRenderer;
import net.hyper_pigeon.action_mobs.client.render.item.ItemHandRenderer;
import net.hyper_pigeon.action_mobs.packet.C2SUpdateActionBlockMobPart;
import net.hyper_pigeon.action_mobs.packet.S2CUpdateActionMobEquipment;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ActionMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ActionMobs.proxy = new ActionMobsClientProxy();

        BlockEntityRendererFactories.register(ActionMobsBlocks.ACTION_MOB_BLOCK_ENTITY, ActionMobBlockEntityRenderer::new);
        ItemHandRenderer.register(ActionMobsBlocks.ACTION_MOB_BLOCK.asItem(), new ActionMobBlockItemHandRenderer());

        ClientPlayNetworking.registerGlobalReceiver(S2CUpdateActionMobEquipment.ID, S2CUpdateActionMobEquipment::receive);
    }
}
