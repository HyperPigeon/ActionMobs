package net.hyper_pigeon.action_mobs.client;

import net.hyper_pigeon.action_mobs.block.ActionMobsCommonProxy;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.client.gui.screen.ingame.ActionMobEditScreen;
import net.minecraft.client.MinecraftClient;

public class ActionMobsClientProxy extends ActionMobsCommonProxy {
    public void openActionMobEditScreen(ActionMobBlockEntity be){
        MinecraftClient.getInstance().setScreen(new ActionMobEditScreen(be));
    }

//    public void setActionMobBlockEquipment(ActionMobBlockEntity be, EntityEquipment entityEquipment) {
//        be.setEntityEquipment(entityEquipment);
//    }
}
