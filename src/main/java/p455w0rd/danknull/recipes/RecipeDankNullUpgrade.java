package p455w0rd.danknull.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 */
public class RecipeDankNullUpgrade extends ShapedOreRecipe {

    public RecipeDankNullUpgrade(ItemStack output, Object... recipe) {
        super(output, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack oldDank = null;

        // Find the existing Dank/Null in the grid to get its NBT
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slotStack = inv.getStackInSlot(i);
            if (slotStack != null && ItemDankNull.isDankNull(slotStack)) {
                oldDank = slotStack;
                break;
            }
        }

        // Get the standard result (the next tier item)
        ItemStack result = super.getCraftingResult(inv);

        if (oldDank != null && result != null && oldDank.hasTagCompound()) {
            // Copy the old NBT
            NBTTagCompound nbt = (NBTTagCompound) oldDank.getTagCompound()
                .copy();

            // FIX: Update the "Size" tag to match the NEW tier's capacity
            if (result.getItem() instanceof ItemDankNull dankNull) {
                int newSize = dankNull.getTier()
                    .getSlots();
                if (nbt.hasKey(DankNullHandler.NBT.DANKNULL_CAP)) {
                    {
                        nbt.getCompoundTag(DankNullHandler.NBT.DANKNULL_CAP)
                            .setInteger("Size", newSize);
                    }
                }
            }
            result.setTagCompound(nbt);
        }
        return result;
    }
}
