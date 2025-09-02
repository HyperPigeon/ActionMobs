package net.hyper_pigeon.action_mobs.register;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.action_mobs.packet.*;

public class ActionMobsNetworking {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobPart.ID, C2SUpdateActionBlockMobPart.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobAngle.ID, C2SUpdateActionBlockMobAngle.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobIsBaby.ID, C2SUpdateActionBlockMobIsBaby.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(S2CUpdateActionMobEquipment.ID, S2CUpdateActionMobEquipment.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionMobOffset.ID, C2SUpdateActionMobOffset.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionMobBlockScale.ID, C2SUpdateActionMobBlockScale.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobPart.ID, C2SUpdateActionBlockMobPart::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobAngle.ID, C2SUpdateActionBlockMobAngle::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobIsBaby.ID, C2SUpdateActionBlockMobIsBaby::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionMobOffset.ID, C2SUpdateActionMobOffset::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionMobBlockScale.ID, C2SUpdateActionMobBlockScale::receive);
    }
}
