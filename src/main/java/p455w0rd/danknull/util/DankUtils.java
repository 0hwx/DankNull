package p455w0rd.danknull.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.items.ItemDankNull;

public class DankUtils {

    public static NBTTagCompound getDankCap(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey(DankNullHandler.NBT.DANKNULL_CAP)) {
            return stack.getTagCompound()
                .getCompoundTag(DankNullHandler.NBT.DANKNULL_CAP);
        }
        return null;
    }

    public static NBTTagCompound getSlotTag(NBTTagCompound cap, int slot) {
        if (cap == null || slot < 0) return null;
        NBTTagList items = cap.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound itemTag = items.getCompoundTagAt(i);
            if (itemTag.getInteger("Slot") == slot) return itemTag;
        }
        return null;
    }

    public static int getSelectedSlot(ItemStack dank) {
        NBTTagCompound cap = getDankCap(dank);
        return cap != null ? cap.getInteger(DankNullHandler.NBT.SELECTEDINDEX) : -1;
    }

    public static ItemStack getStackInSlot(ItemStack dank, int slot) {
        NBTTagCompound slotTag = getSlotTag(getDankCap(dank), slot);
        return slotTag != null ? ItemStack.loadItemStackFromNBT(slotTag) : null;
    }

    public static ItemStack getDankStack(EntityPlayer player) {
        ItemStack mainHand = player.getHeldItem();
        if (mainHand != null && mainHand.getItem() instanceof ItemDankNull) {
            return mainHand;
        }

        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemDankNull) {
                return stack;
            }
        }
        // we can add baubles here
        return null;
    }

    public static List<ItemStack> getDankNullsForPlayer(EntityPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemDankNull) list.add(stack);
        }
        // we can add baubles here
        return list;
    }

    public static boolean canDankNullAcceptItem(ItemStack dankNull, ItemStack incoming) {
        if (incoming == null) return false;
        NBTTagCompound cap = getDankCap(dankNull);
        if (cap == null) return false;

        NBTTagList itemsList = cap.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        String incomingID = incoming.getItem().delegate.name(); // Fast String ID

        for (int i = 0; i < itemsList.tagCount(); i++) {
            NBTTagCompound itemTag = itemsList.getCompoundTagAt(i);

            if (itemTag.getString("id")
                .equals(incomingID)) {
                ItemStack internal = ItemStack.loadItemStackFromNBT(itemTag);
                if (areItemStacksEqualIgnoreSize(internal, incoming)) return true;
            }

            // OreDict Check
            if (itemTag.hasKey(DankNullHandler.NBT.DANK_SETTINGS)) {
                NBTTagCompound settings = itemTag.getCompoundTag(DankNullHandler.NBT.DANK_SETTINGS);
                if (settings.getBoolean(DankNullHandler.NBT.OREDICT)) {
                    ItemStack internal = ItemStack.loadItemStackFromNBT(itemTag);
                    if (oreMatches(internal, incoming)) return true;
                }
            }
        }
        return false;
    }

    private static boolean oreMatches(ItemStack s1, ItemStack s2) {
        int[] ids1 = OreDictionary.getOreIDs(s1);
        int[] ids2 = OreDictionary.getOreIDs(s2);
        for (int id1 : ids1) {
            for (int id2 : ids2) {
                if (id1 == id2 && ModConfig.isValidOre(OreDictionary.getOreName(id1))) return true;
            }
        }
        return false;
    }

    public static List<String> getOreNames(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        List<String> names = new ArrayList<>();
        for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if (!name.equals("Unknown") && ModConfig.isValidOre(name)) names.add(name);
        }
        return names;
    }

    public static boolean areItemStacksEqualIgnoreSize(ItemStack s1, ItemStack s2) {
        if (s1 == null || s2 == null) return false;
        return s1.getItem() == s2.getItem() && s1.getItemDamage() == s2.getItemDamage()
            && ItemStack.areItemStackTagsEqual(s1, s2);
    }
}
