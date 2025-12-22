package p455w0rd.danknull.integration.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import mcp.mobius.waila.api.*;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.integration.WAILA;

/**
 * @author p455w0rd
 *
 */
public class WAILADankNullDockProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
        final TileEntity tile = accessor.getTileEntity();
        if (tile != null && tile instanceof TileDankNullDock) {
            final TileDankNullDock te = (TileDankNullDock) tile;
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            te.writeToNBT(nbttagcompound);
            stack.setTagInfo("BlockEntityTag", nbttagcompound);
        }
        return stack;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip,
        final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip,
        final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final TileDankNullDock dankDock = (TileDankNullDock) accessor.getTileEntity();
        if (dankDock.getDankNull() != null) {
            final ItemStack dockedDankNull = dankDock.getDankNull();
            if (dockedDankNull != null) {
                currenttip.add(WAILA.toolTipEnclose);
                currenttip.add(
                    DankNullTier.Rarities.getRarityFromMeta(
                        DankNullTier.getTier(dockedDankNull)
                            .ordinal()).rarityColor
                        + ""
                        + dockedDankNull.getDisplayName()
                        + ""
                        + EnumChatFormatting.GRAY
                        + " Docked");
                if (dankDock.getDankHandler()
                    .getSelected() < 0) {
                    return currenttip;
                }
                final ItemStack selectedStack = dankDock.getDankHandler()
                    .getStackInSlot(
                        dankDock.getDankHandler()
                            .getSelected());
                if (selectedStack != null) {
                    currenttip
                        .add(selectedStack.getDisplayName() + " " + StatCollector.translateToLocal("dn.selected.desc"));
                    currenttip.add(
                        StatCollector.translateToLocal("dn.count.desc") + ": "
                            + (DankNullTier.getTier(dockedDankNull) == DankNullTier.CREATIVE
                                ? StatCollector.translateToLocal("dn.infinite.desc")
                                : selectedStack.stackSize));
                    currenttip.add(
                        StatCollector.translateToLocal("dn.extract_mode.desc") + ": "
                            + dankDock.getDankHandler()
                                .getExtractionMode(
                                    dankDock.getDankHandler()
                                        .getSelected())
                                .getTooltip());
                }
                currenttip.add(WAILA.toolTipEnclose);
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip,
        final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return null;
    }
}
