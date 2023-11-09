package com.kusrunes;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod(modid = KusRunes.MODID, version = KusRunes.VERSION)
public class KusRunes {

    public static final String MODID = "kusrunes";
    public static final String VERSION = "1.0";

    public KusRunes() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public class KusRuneItem extends Item {

        @Override
        public boolean hasEffect(ItemStack stack) {
            return true; // Make the item glow to signify its special nature
        }

        private static List<AttributeData> attributeList = new ArrayList<>();

        static {
            // Initialize the list with your attribute data
            attributeList.add(new AttributeData("generic.attackDamage", 0.01, 1)); // Attribute name, value, operation
            attributeList.add(new AttributeData("generic.maxHealth", 1, 0));
            attributeList.add(new AttributeData("generic.attackSpeed", 0.01, 1));
            attributeList.add(new AttributeData("enderskills.generic.abilityPower", 0.01, 1));
            attributeList.add(new AttributeData("enderskills.generic.abilityDuration", 0.008, 1));
            attributeList.add(new AttributeData("enderskills.generic.abilityRage", 0.008, 1));
            attributeList.add(new AttributeData("potioncore.magicDamage", 0.01, 1));
            attributeList.add(new AttributeData("potioncore.projectileDamage", 0.01, 1));
            attributeList.add(new AttributeData("cool-attributes.healPerTick", 0.00018333, 0));
            attributeList.add(new AttributeData("enderskills.generic.cooldownReduction", 0.0015, 0));
            attributeList.add(new AttributeData("enderskills.generic.maxEndurance", 0.01, 1));
            
            // Add more attributes as needed
        }

        public KusRuneItem() {
            super();
            this.setMaxStackSize(1); // Ensure the item does not stack
            this.setUnlocalizedName("kusrune"); // Set a unique name for your item
            this.setRegistryName("kusrune"); // Set a unique registry name
            this.setCreativeTab(CreativeTabs.MISC); // Add your item to the Creative Miscellaneous tab
        }

        @Override
        public void onCreated(ItemStack stack, World world, EntityPlayer player) {
            // When the item is first created (e.g., obtained), randomly select an attribute
            NBTTagCompound nbt = new NBTTagCompound();

            AttributeData randomAttribute = getRandomAttribute();
            
            nbt.setString("selectedAttribute", randomAttribute.name);
            
            double attributeValue = randomAttribute.value;
            int operation = randomAttribute.operation;

            // Create an attribute modifier
            IAttribute attribute = SharedMonsterAttributes.MAX_HEALTH; // You can change this to the desired attribute
            UUID modifierUUID = UUID.randomUUID(); // Generate a unique UUID for the modifier (you can use a fixed UUID)
            AttributeModifier modifier = new AttributeModifier(modifierUUID, "Custom Modifier", attributeValue, operation);

            // Create an NBTTagList to store the attribute modifier
            NBTTagList modifiers = new NBTTagList();
            modifiers.appendTag(SharedMonsterAttributes.writeAttributeModifierToNBT(modifier));

            // Attach the attribute modifier to the NBT data
            nbt.setTag("AttributeModifiers", modifiers);

            stack.setTagCompound(nbt);
        }

        private AttributeData getRandomAttribute() {
            Random random = new Random();
            int index = random.nextInt(attributeList.size());
            return attributeList.get(index);
        }
    }
}