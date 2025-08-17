package net.hyper_pigeon.action_mobs.register;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.action_mobs.packet.C2SUpdateActionBlockMobAngle;
import net.hyper_pigeon.action_mobs.packet.C2SUpdateActionBlockMobIsBaby;
import net.hyper_pigeon.action_mobs.packet.C2SUpdateActionBlockMobPart;

public class ActionMobsNetworking {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobPart.ID, C2SUpdateActionBlockMobPart.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobAngle.ID, C2SUpdateActionBlockMobAngle.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobIsBaby.ID, C2SUpdateActionBlockMobIsBaby.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobPart.ID, C2SUpdateActionBlockMobPart::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobAngle.ID, C2SUpdateActionBlockMobAngle::receive);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobIsBaby.ID, C2SUpdateActionBlockMobIsBaby::receive);
    }
}
