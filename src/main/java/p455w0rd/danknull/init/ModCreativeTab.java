package p455w0rd.danknull.init;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.DankNull;

/**
 * @author p455w0rd
 *
 */
public class ModCreativeTab extends CreativeTabs {

    public static CreativeTabs TAB;

    public ModCreativeTab() {
        super(DankNull.MODID);
    }

    public static void init() {
        TAB = new ModCreativeTab();
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(ModItems.CREATIVE_DANKNULL);
    }

    @Override
    public Item getTabIconItem() {
        return null;
    }

    @Override
    public void displayAllReleventItems(final List<ItemStack> items) {
        for (final Item item : ModItems.ITEM_LIST) {
            if (!(item instanceof ItemBlock)) {
                items.add(new ItemStack(item));
            }
        }
        items.add(new ItemStack(ModBlocks.DANKNULL_DOCK));
    }

}
