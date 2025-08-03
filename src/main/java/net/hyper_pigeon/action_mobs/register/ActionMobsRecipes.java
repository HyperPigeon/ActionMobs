package net.hyper_pigeon.action_mobs.register;

import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.recipe.ActionMobRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ActionMobsRecipes {
    public static RecipeSerializer<ActionMobRecipe> ACTION_MOB_RECIPE_SERIALIZER;

    public static void init() {
        ACTION_MOB_RECIPE_SERIALIZER =  Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(ActionMobs.MOD_ID,"crafting_action_mob"),
                new SpecialCraftingRecipe.SpecialRecipeSerializer<>(ActionMobRecipe::new));
    }
}
