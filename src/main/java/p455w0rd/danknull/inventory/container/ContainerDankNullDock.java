package p455w0rd.danknull.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.inventory.DankNullHandler;

/**
 * @author p455w0rd
 */
public class ContainerDankNullDock extends ContainerDankNull {

    private final TileDankNullDock tile;

    public ContainerDankNullDock(final EntityPlayer player, final TileDankNullDock tile) {
        super(player);
        this.tile = tile;
        init();
    }

    @Override
    public boolean canInteractWith(final EntityPlayer player) {
        return tile.getWorldObj()
            .getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord) == tile;
    }

    @Override
    public boolean isDock() {
        return true;
    }

    @Override
    public DankNullHandler getHandler() {
        return tile.getDankHandler();
    }

    @Override
    public ItemStack getDankNullStack() {
        return tile.getDankNull();
    }

}
