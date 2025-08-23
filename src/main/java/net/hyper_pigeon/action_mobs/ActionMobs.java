package net.hyper_pigeon.action_mobs;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.hyper_pigeon.action_mobs.block.ActionMobsCommonProxy;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.hyper_pigeon.action_mobs.register.ActionMobsData;
import net.hyper_pigeon.action_mobs.register.ActionMobsNetworking;
import net.hyper_pigeon.action_mobs.register.ActionMobsRecipes;
import org.slf4j.Logger;

public class ActionMobs implements ModInitializer {

    public static String MOD_ID = "action_mobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ActionMobsCommonProxy proxy = new ActionMobsCommonProxy();
    @Override
    public void onInitialize() {
        ActionMobsData.init();
        ActionMobsBlocks.initialize();
        ActionMobsRecipes.init();
        ActionMobsNetworking.init();
    }
}
