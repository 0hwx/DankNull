package p455w0rd.danknull.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.inventory.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.container.ContainerDankNullItem;
import p455w0rd.danknull.util.DankUtils;

/**
 * @author p455w0rd
 */
public class ModGuiHandler implements IGuiHandler {

    public static void init() {
        DankNull.LOGGER.info("Registering GUI Handler");
        NetworkRegistry.INSTANCE.registerGuiHandler(DankNull.MODID, new ModGuiHandler());
    }

    @Override
    public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x,
        final int y, final int z) {
        GUIType type = GUIType.VALUES[id];
        switch (type) {
            case DANKNULL:
                ItemStack stack = DankUtils.getDankStack(player);
                if (stack == null) return null;
                return new ContainerDankNullItem(player, stack);

            case DANKNULL_TE:
                final TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileDankNullDock dankDock) {
                    if (dankDock.getDankNull() != null) {
                        return new ContainerDankNullDock(player, dankDock);
                    }
                }
            default:
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x,
        final int y, final int z) {
        GUIType type = GUIType.VALUES[id];
        switch (type) {
            case DANKNULL:
                ItemStack stack = DankUtils.getDankStack(player);
                if (stack == null) return null;
                return new GuiDankNull(new ContainerDankNullItem(player, stack));

            case DANKNULL_TE:
                final TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileDankNullDock dankDock) {
                    if (dankDock.getDankNull() != null) {
                        return new GuiDankNull(new ContainerDankNullDock(player, dankDock));
                    }
                }

            default:
                break;
        }
        return null;
    }

    public enum GUIType {

        DANKNULL,
        DANKNULL_TE;

        public static final GUIType[] VALUES = values();

    }

}
