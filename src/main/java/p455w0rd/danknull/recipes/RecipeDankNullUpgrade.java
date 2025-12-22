package p455w0rd.danknull.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 */
// this is broken
public class RecipeDankNullUpgrade extends ShapedRecipes {

    private final ItemStack[] recipeItems;

    public RecipeDankNullUpgrade(int width, int height, ItemStack[] items) {
        super(width, height, items, null);
        this.recipeItems = items;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return DankNullTier.getTier(getInputDankNull())
            .getUpgradedVersion(getInputDankNull());
    }

    public ItemStack getInputDankNull() {
        for (final ItemStack item : recipeItems) {
            // In 1.7.10, we check for null instead of .isEmpty()
            if (item != null && ItemDankNull.isDankNull(item)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean matches(final InventoryCrafting inv, final World worldIn) {
        for (int widthIndex = 0; widthIndex <= 3 - this.recipeWidth; ++widthIndex) {
            for (int heightIndex = 0; heightIndex <= 3 - this.recipeHeight; ++heightIndex) {
                if (checkMatch(inv, widthIndex, heightIndex)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(final InventoryCrafting inventory, final int widthIndexStart,
        final int heightIndexStart) {
        for (int column = 0; column < 3; ++column) {
            for (int row = 0; row < 3; ++row) {
                final int recipeColumn = column - widthIndexStart;
                final int recipeRow = row - heightIndexStart;
                ItemStack recipeStack = null;
                if (recipeColumn >= 0 && recipeRow >= 0 && recipeColumn < 3 && recipeRow < 3) {

                    recipeStack = recipeItems[recipeColumn + recipeRow * 3];
                }

                ItemStack slotStack = inventory.getStackInRowAndColumn(column, row);

                if (slotStack != null || recipeStack != null) {
                    if (slotStack == null && recipeStack != null || slotStack != null && recipeStack == null) {
                        return false;
                    }
                    if (recipeStack.getItem() != slotStack.getItem()) {
                        return false;
                    }
                    if (recipeStack.getItemDamage() != 32767
                        && recipeStack.getItemDamage() != slotStack.getItemDamage()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (ItemDankNull.isDankNull(inv.getStackInSlot(i))) {
                NBTTagCompound oldNBT = inv.getStackInSlot(i)
                    .getTagCompound();
                final ItemStack newStack = getNewNextTierStack(inv.getStackInSlot(i));
                newStack.setTagCompound(oldNBT);
                return newStack;
            }
        }
        return null;
    }

    private ItemStack getNewNextTierStack(final ItemStack dankNull) {
        final int tier = DankNullTier.getTier(dankNull)
            .ordinal();
        if (tier < 5) {
            ItemDankNull item = null;
            switch (tier) {
                case 0:
                    item = ModItems.LAPIS_DANKNULL;
                    break;
                case 1:
                    item = ModItems.IRON_DANKNULL;
                    break;
                case 2:
                    item = ModItems.GOLD_DANKNULL;
                    break;
                case 3:
                    item = ModItems.DIAMOND_DANKNULL;
                    break;
                case 4:
                    item = ModItems.EMERALD_DANKNULL;
                    break;
            }
            return new ItemStack(item);
        }
        return null;
    }
}
