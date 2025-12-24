package p455w0rd.danknull.blocks.tiles;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.util.inv.ItemStackHandler;

public class TileDankNullDock extends TileEntity implements ISidedInventory {

    private DankNullHandler cachedHandler;

    // the Dank inventory
    private final ItemStackHandler inventory = new ItemStackHandler(1) {

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1; // Only allow one item
        }
    };

    public TileDankNullDock() {}

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nullable
    public DankNullHandler getDankHandler() {
        ItemStack dank = getDankNull();
        if (dank == null) {
            cachedHandler = null;
            return null;
        }

        if (cachedHandler == null) {
            cachedHandler = DankNullHandler.fromStack(worldObj, dank, this);
        }
        cachedHandler.updateSelectedSlot();
        return cachedHandler;
    }

    public ItemStack getDankNull() {
        return inventory.getStackInSlot(0);
    }

    public void setDankNull(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
        cachedHandler = null; // force rebuild
        markDirty();
    }

    public void removeDankNull() {
        inventory.setStackInSlot(0, null);
    }

    @Override
    @Nullable
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -1, nbt);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public int getSizeInventory() {
        if (getDankHandler() != null) {
            return getDankHandler().getSlots();
        }
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        DankNullHandler handler = getDankHandler();
        if (handler == null) return null;
        return handler.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        DankNullHandler handler = getDankHandler();
        if (handler == null || worldObj.isRemote) return null;

        ItemStack extracted = handler.extractItemIngoreExtractionMode(slot, amount, false);
        if (extracted != null) {
            markDirty();
        }
        return extracted;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        DankNullHandler handler = getDankHandler();
        if (handler == null || worldObj.isRemote) return;

        handler.setInventorySlotContents(slot, stack);
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "Dock";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        if (getDankHandler() != null) {
            return getDankHandler().getTier()
                .getMaxStackSize();
        }
        return 0;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (worldObj != null) { // Shouldn't be null
            worldObj.markBlockRangeForRenderUpdate(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                this.xCoord,
                this.yCoord,
                this.zCoord);
            worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            worldObj.scheduleBlockUpdateWithPriority(this.xCoord, this.yCoord, this.zCoord, getBlockType(), 0, 0);
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (getDankHandler() != null) {
            return getDankHandler().isItemValid(index, stack);
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        int[] slots = new int[getSizeInventory()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return true;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        return true;
    }
}
