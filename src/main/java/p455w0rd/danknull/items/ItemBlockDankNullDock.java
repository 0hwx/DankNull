package p455w0rd.danknull.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.inventory.DankNullHandler;

/**
 * @author p455w0rd
 */
public class ItemBlockDankNullDock extends ItemBlock {

    public ItemBlockDankNullDock(Block block) {
        super(block);
    }

    public static ItemStack getDockedDankNull(final ItemStack dankDock) {
        ItemStack dockedDank = null;
        if (dankDock.hasTagCompound() && dankDock.getTagCompound()
            .hasKey(DankNullHandler.NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
            final NBTTagCompound nbt = dankDock.getTagCompound()
                .getCompoundTag(DankNullHandler.NBT.BLOCKENTITYTAG);
            if (nbt != null) {
                dockedDank = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(DankNullHandler.NBT.DOCKEDSTACK));
            }
        }
        return dockedDank;
    }

    @Override
    public String getItemStackDisplayName(final ItemStack stack) {
        String name = StatCollector.translateToLocal(getUnlocalizedName() + ".name")
            .trim();
        if (Options.callItDevNull) {
            name = name.replace("/dank/", "/dev/");
        }
        return name;
    }
}
