package net.hyper_pigeon.action_mobs.recipe;

import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.hyper_pigeon.action_mobs.register.ActionMobsRecipes;
import net.hyper_pigeon.action_mobs.statue_type.StatueType;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ArmorStandItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ActionMobRecipe extends SpecialCraftingRecipe {
    public ActionMobRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.getStackCount() != 2) {
            return false;
        }
        else {
            boolean hasArmorStand = false;
            boolean hasCreationItem = false;
            for (int i = 0; i < input.size(); i++) {
                ItemStack itemStack = input.getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    Item item = itemStack.getItem();
                    if(itemStack.getItem().equals(Items.ARMOR_STAND)) {
                        if(hasArmorStand) {
                            return false;
                        }

                        hasArmorStand = true;
                    }
                    else if(StatueTypeDataLoader.statueTypesByItem.containsKey(item)) {
                        if(hasCreationItem) {
                            return false;
                        }
                        hasCreationItem = true;
                    }
                }
            }
            return hasArmorStand && hasCreationItem;
        }
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        ItemStack armorStandStack = ItemStack.EMPTY;
        ItemStack creationItemStack = ItemStack.EMPTY;

        for(int i = 0; i < input.size(); ++i) {
            ItemStack inventoryStack = input.getStackInSlot(i);
            if(inventoryStack.getItem() instanceof ArmorStandItem) {
                armorStandStack = inventoryStack.copy();
                armorStandStack.setCount(1);
            }
            else if((StatueTypeDataLoader.statueTypesByItem.containsKey(inventoryStack.getItem()))) {
                creationItemStack = inventoryStack;
                creationItemStack.setCount(i);
            }
        }

        if(!armorStandStack.isEmpty() && !creationItemStack.isEmpty()) {
            ItemStack actionMobStack = new ItemStack(ActionMobsBlocks.ACTION_MOB_BLOCK.asItem());
            Item creationItem = creationItemStack.getItem();
            EntityType<?> entityType = StatueTypeDataLoader.statueTypesByItem.get(creationItem).getEntityType();
            NbtComponent.set(DataComponentTypes.CUSTOM_DATA, actionMobStack, nbtCompound -> {
                nbtCompound.putString("entity_type", Registries.ENTITY_TYPE.getEntry(entityType).getKey().get().getValue().toString());
            });
            return actionMobStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return ActionMobsRecipes.ACTION_MOB_RECIPE_SERIALIZER;
    }
}
