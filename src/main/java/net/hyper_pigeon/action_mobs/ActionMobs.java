package net.hyper_pigeon.action_mobs;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.hyper_pigeon.action_mobs.register.ActionMobsData;
import net.hyper_pigeon.action_mobs.register.ActionMobsRecipes;
import org.slf4j.Logger;

public class ActionMobs implements ModInitializer {

    public static String MOD_ID = "action_mobs";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ActionMobsBlocks.initialize();
        ActionMobsData.init();
        ActionMobsRecipes.init();
    }
}
