package com.kusrunes;

import com.kusrunes.KusRunes;
import com.kusrunes.KusRunes.KusRuneItem;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class KusRuneTransferRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack kusrune = ItemStack.EMPTY;
        ItemStack targetItem = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof KusRuneItem) {
                    kusrune = stack.copy();
                } else if (!stack.isStackable() && !stack.isItemEqual(kusrune)) {
                    targetItem = stack.copy();
                }
            }
        }

        return !kusrune.isEmpty() && !targetItem.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack kusrune = ItemStack.EMPTY;
        ItemStack targetItem = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof KusRuneItem) {
                    kusrune = stack.copy();
                } else if (!stack.isStackable() && !stack.isItemEqual(kusrune)) {
                    targetItem = stack.copy();
                }
            }
        }

        NBTTagCompound targetNBT = targetItem.getTagCompound();

        // Check if the target item already has a transfer count NBT tag
        if (!targetItem.isEmpty() && targetNBT != null) {
            int transferCount = targetNBT.getInteger("kusruneTransferCount");

            if (transferCount >= 20) {
                // Reduce the effectiveness of attributes by 90% for new attributes
                reduceAttributes(targetItem);
            } else {
                transferAttributes(targetItem, kusrune);
                targetNBT.setInteger("kusruneTransferCount", transferCount + 1);
            }
        } else {
            // First transfer to the target item
            transferAttributes(targetItem, kusrune);
            targetNBT = new NBTTagCompound();
            targetNBT.setInteger("kusruneTransferCount", 1);
            targetItem.setTagCompound(targetNBT);
        }

        return targetItem;
    }

    private void transferAttributes(ItemStack targetItem, ItemStack kusrune) {
        NBTTagCompound kusruneNBT = kusrune.getTagCompound();
        if (kusruneNBT != null && kusruneNBT.hasKey("AttributeModifiers")) {
            NBTTagList kusruneModifiers = kusruneNBT.getTagList("AttributeModifiers", 10);

            NBTTagCompound targetNBT = targetItem.getTagCompound();
            if (targetNBT == null) {
                targetNBT = new NBTTagCompound();
            }

            NBTTagList targetModifiers = targetNBT.getTagList("AttributeModifiers", 10);

            // Merge the attribute modifiers from the kusrune to the target item
            for (int i = 0; i < kusruneModifiers.tagCount(); i++) {
                targetModifiers.appendTag(kusruneModifiers.get(i));
            }

            targetNBT.setTag("AttributeModifiers", targetModifiers);
            targetItem.setTagCompound(targetNBT);
        }
    }

    private void reduceAttributes(ItemStack targetItem) {
        if (targetItem.getTagCompound() != null) {
            NBTTagCompound targetNBT = targetItem.getTagCompound();
            NBTTagList targetModifiers = targetNBT.getTagList("AttributeModifiers", 10);

            // Reduce the effectiveness of attributes by 90% for new attributes
            for (int i = 0; i < targetModifiers.tagCount(); i++) {
                NBTTagCompound modifier = targetModifiers.get(i);
                double amount = modifier.getDouble("Amount");

                // Check if the attribute was added after 20 transfers
                if (modifier.hasKey("kusruneTransferred") && modifier.getBoolean("kusruneTransferred")) {
                    modifier.setDouble("Amount", amount * 0.1);
                }
            }

            targetNBT.setTag("AttributeModifiers", targetModifiers);
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY; // The output will be determined in getCraftingResult
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name) {
        return super.setRegistryName(new ResourceLocation(kusrunes.KusRunes, name.getPath()));
    }
}
