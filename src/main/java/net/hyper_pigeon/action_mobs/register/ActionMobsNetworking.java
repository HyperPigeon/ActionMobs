package net.hyper_pigeon.action_mobs.register;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.action_mobs.packet.C2SUpdateActionBlockMobPart;

public class ActionMobsNetworking {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(C2SUpdateActionBlockMobPart.ID, C2SUpdateActionBlockMobPart.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateActionBlockMobPart.ID, C2SUpdateActionBlockMobPart::receive);
    }
}
