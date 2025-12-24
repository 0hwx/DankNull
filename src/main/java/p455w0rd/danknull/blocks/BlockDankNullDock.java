package p455w0rd.danknull.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.NetworkHandler;
import p455w0rd.danknull.network.packet.COpenGui;

/**
 * @author p455w0rd
 *
 */
public class BlockDankNullDock extends BlockContainer {

    public BlockDankNullDock() {
        super(Material.iron);
        setBlockName("danknull_dock");
        setBlockTextureName("danknull:dock/base");
        setResistance(6000000.0F);
        setHardness(10.0F);
        setLightOpacity(255);
        useNeighborBrightness = true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileDankNullDock();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        TileDankNullDock dock = (TileDankNullDock) world.getTileEntity(x, y, z);
        if (dock == null) return false;

        if (MinecraftServer.getServer()
            .isBlockProtected(world, x, y, z, player)) {
            return false;
        }

        ItemStack held = player.getHeldItem();

        if (world.isRemote && dock.getDankNull() != null && !player.isSneaking()) {
            NetworkHandler
                .sendToServer(new COpenGui(ModGuiHandler.GUIType.DANKNULL_TE, dock.xCoord, dock.yCoord, dock.zCoord));
            return true;
        }

        if (!world.isRemote) { // server-side
            if (dock.getDankNull() == null && ItemDankNull.isDankNull(held)) {
                dock.setDankNull(held.copy());
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                player.inventory.markDirty();
                return true;
            }

            if (held == null && dock.getDankNull() != null && player.isSneaking()) {
                player.setCurrentItemOrArmor(0, dock.getDankNull());
                dock.removeDankNull();
                player.inventory.markDirty();
                return true;
            }

        }

        return true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        ItemStack stack = new ItemStack(this);

        if (te instanceof TileDankNullDock tileDankNullDock) {
            NBTTagCompound teTag = new NBTTagCompound();
            tileDankNullDock.writeToNBT(teTag);

            // Clean up coordinate tags so they don't mess with the new location
            teTag.removeTag("x");
            teTag.removeTag("y");
            teTag.removeTag("z");

            // Store it under "BlockEntityTag"
            if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
            stack.stackTagCompound.setTag("BlockEntityTag", teTag);
        }
        return stack;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);

        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("BlockEntityTag")) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileDankNullDock tileDankNullDock) {
                NBTTagCompound nbt = stack.getTagCompound()
                    .getCompoundTag("BlockEntityTag");

                tileDankNullDock.readFromNBT(nbt);

                tileDankNullDock.xCoord = x;
                tileDankNullDock.yCoord = y;
                tileDankNullDock.zCoord = z;

                tileDankNullDock.markDirty();
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileDankNullDock) {
            TileDankNullDock dock = (TileDankNullDock) te;
            ItemStack stack = dock.getDankNull();

            if (stack != null) {
                float f = world.rand.nextFloat() * 0.8F + 0.1F;
                float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
                float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, stack);

                float f3 = 0.05F;
                entityitem.motionX = (float) world.rand.nextGaussian() * f3;
                entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float) world.rand.nextGaussian() * f3;

                world.spawnEntityInWorld(entityitem);
            }
        }
        // Call super last to ensure TileEntity is still there when we check it
        super.breakBlock(world, x, y, z, block, meta);
    }

    public int getRenderType() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isOpaqueCube() {
        return false;
    }

}
