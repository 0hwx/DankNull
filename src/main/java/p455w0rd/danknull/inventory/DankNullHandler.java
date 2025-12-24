package p455w0rd.danknull.inventory;

import static p455w0rd.danknull.util.DankUtils.areItemStacksEqualIgnoreSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.ImmutableList;

import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.inv.ItemHandlerHelper;
import p455w0rd.danknull.util.inv.ItemStackHandler;

public class DankNullHandler extends ItemStackHandler {

    private final World world;
    private final ItemStack dank;
    private TileEntity dockTile;
    private final DankNullTier tier;

    private List<Boolean> oreDict = new ArrayList<>();
    private List<ItemExtractionMode> extractionModes = new ArrayList<>();
    private List<ItemPlacementMode> placementModes = new ArrayList<>();

    private int selected = -1;
    private boolean isLocked = false;

    public DankNullHandler(World world, ItemStack stack, TileEntity tile) {
        super(
            DankNullTier.isDankNull(stack)
                .getSlots());
        this.tier = DankNullTier.isDankNull(stack);
        this.dockTile = tile;
        this.dank = stack;
        this.world = world;

        setSizeDank(this.getSlots());
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey(NBT.DANKNULL_CAP)) {
            deserializeNBT(
                stack.getTagCompound()
                    .getCompoundTag(NBT.DANKNULL_CAP));
        }

    }

    public static DankNullHandler fromStack(World world, ItemStack stack) {
        return fromStack(world, stack, null);
    }

    public static DankNullHandler fromStack(World world, ItemStack stack, TileEntity tile) {
        if (stack == null || !(stack.getItem() instanceof ItemDankNull)) {
            return null;
        }

        DankNullHandler handler = new DankNullHandler(world, stack, tile);

        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey(NBT.DANKNULL_CAP)) {
            handler.deserializeNBT(
                stack.getTagCompound()
                    .getCompoundTag(NBT.DANKNULL_CAP));
        }

        return handler;
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        setSizeDank(size);
    }

    public void setSizeDank(int size) {
        ItemExtractionMode[] extractionMode = new ItemExtractionMode[size];
        Arrays.fill(extractionMode, ItemExtractionMode.KEEP_1);
        this.extractionModes = Arrays.asList(extractionMode);

        ItemPlacementMode[] placementModes = new ItemPlacementMode[size];
        Arrays.fill(placementModes, ItemPlacementMode.KEEP_1);
        this.placementModes = Arrays.asList(placementModes);

        Boolean[] oreDict = new Boolean[size];
        Arrays.fill(oreDict, Boolean.FALSE);
        this.oreDict = Arrays.asList(oreDict);
    }

    @Nonnull
    public DankNullTier getTier() {
        return tier;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return null;
        validateSlotIndex(slot);

        ItemStack existing = getStackInSlot(slot);
        if (existing == null) return null;

        if (getExtractionMode(slot) == ItemExtractionMode.KEEP_ALL) return null;

        int keep = getExtractionMode(slot).getNumberToKeep();
        int toExtract = Math.min(Math.min(amount, existing.getMaxStackSize()), existing.stackSize - keep);

        if (existing.stackSize <= toExtract) {
            if (!simulate) {
                setStackInSlot(slot, null);
            }
            return existing;
        }

        if (!simulate) {
            setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
        }
        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    @Nonnull
    public ItemStack extractItemIngoreExtractionMode(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    public int findItemStack(@Nonnull final ItemStack stack) {
        for (int i = 0; i < getSlots(); i++) {
            if (areItemStacksEqualIgnoreSize(getStackInSlot(i), stack)) {
                return i;
            }
        }
        return -1;
    }

    public ImmutableList<Integer> findItemStacks(@Nonnull ItemStack stack) {
        ImmutableList.Builder<Integer> results = ImmutableList.builder();
        for (int i = 0; i < getSlots(); i++) {
            if (areItemStacksEqualIgnoreSize(getStackInSlot(i), stack)) {
                results.add(i);
            }
        }
        return results.build();
    }

    public void updateSelectedSlot() {
        int sel = getSelected();

        // Already valid
        if (sel >= 0 && getStackInSlot(sel) != null) return;

        // Pick first non-null stack
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i) != null) {
                setSelected(i);
                return;
            }
        }

        // Nothing found
        setSelected(-1);
    }

    // this is to be used with ISidedInventory only
    public void setInventorySlotContents(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        if (stack == null || stack.stackSize <= 0) {
            super.setStackInSlot(slot, null);
            onContentsChanged(slot);
            return;
        }

        for (int i = slot - 1; i >= 0; i--) {
            ItemStack existing = getStackInSlot(i);
            if (existing != null && ItemHandlerHelper.canItemStacksStack(existing, stack)
                && (existing.stackSize < getSlotLimit(i))) {
                int transferable = Math.min(stack.stackSize, getSlotLimit(i) - existing.stackSize);
                if (transferable > 0) {
                    existing.stackSize += transferable;
                    stack.stackSize -= transferable;
                    onContentsChanged(i);
                }
                if (stack.stackSize <= 0) return;
            }
        }

        super.setStackInSlot(slot, stack.stackSize > 0 ? stack : null);
        onContentsChanged(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (stack == null) return false;
        ItemStack inSlot = getStackInSlot(slot);
        boolean isDank = stack.getItem() instanceof ItemDankNull;
        return !isDank && (inSlot == null || areItemStacksEqualIgnoreSize(inSlot, stack));
    }

    @Override
    public int getSlotLimit(int slot) {
        return tier.getMaxStackSize();
    }

    @Override
    protected int getStackLimit(int slot, @Nullable ItemStack stack) {
        return tier.getMaxStackSize();
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int slot) {
        selected = Math.min(Math.max(slot, -1), getSlots() - 1);
        onContentsChanged(slot);
    }

    public void cycleSelected(boolean forward) {
        List<Integer> blockSlots = getBlockSlots();
        if (ModConfig.Options.skipNonBlocksOnCycle && !blockSlots.isEmpty()) {
            int idx = blockSlots.indexOf(selected);
            int newIndex = forward ? (idx < blockSlots.size() - 1 ? blockSlots.get(idx + 1) : blockSlots.get(0))
                : (idx > 0 ? blockSlots.get(idx - 1) : blockSlots.get(blockSlots.size() - 1));
            setSelected(newIndex);
            return;
        }

        int stackCount = stackCount();
        if (stackCount <= 1) return;
        int newIndex = forward ? (selected < stackCount - 1 ? selected + 1 : 0)
            : (selected > 0 ? selected - 1 : stackCount - 1);
        if (newIndex != selected) setSelected(newIndex);
    }

    private List<Integer> getBlockSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < getSlots(); i++) {
            ItemStack s = getStackInSlot(i);
            if (s != null && s.getItem() instanceof ItemBlock) slots.add(i);
        }
        // Collections.sort(slots); to check if it's need because it was used be for the refactor
        return slots;
    }

    public int stackCount() {
        int count = 0;
        for (ItemStack s : getStacks()) if (s != null) count++;
        return count;
    }

    public boolean isOre(int slot) {
        return oreDict.get(slot);
    }

    public void setOre(int slot, boolean ore) {
        oreDict.set(slot, ore);
        onContentsChanged(slot);
    }

    public ItemExtractionMode getExtractionMode(int slot) {
        this.validateSlotIndex(slot);
        return extractionModes.get(slot);
    }

    public void setExtractionMode(int slot, ItemExtractionMode mode) {
        this.validateSlotIndex(slot);
        extractionModes.set(slot, mode);
        onContentsChanged(slot);
    }

    public ItemExtractionMode getNextExtractionMode(int slot, boolean forward) {
        ItemExtractionMode[] values = ItemExtractionMode.values();
        ItemExtractionMode current = getExtractionMode(slot);
        ItemExtractionMode next = forward ? values[(current.ordinal() + 1) % values.length]
            : values[(current.ordinal() - 1 + values.length) % values.length];
        return next;
    }

    public ItemPlacementMode getPlacementMode(int slot) {
        this.validateSlotIndex(slot);
        return placementModes.get(slot);
    }

    public void setPlacementMode(int slot, ItemPlacementMode mode) {
        this.validateSlotIndex(slot);
        placementModes.set(slot, mode);
        onContentsChanged(slot);
    }

    public ItemPlacementMode getNextPlacementMode(int slot, boolean forward) {
        ItemPlacementMode[] values = ItemPlacementMode.values();
        ItemPlacementMode current = getPlacementMode(slot);
        ItemPlacementMode next = forward ? values[(current.ordinal() + 1) % values.length]
            : values[(current.ordinal() - 1 + values.length) % values.length];
        return next;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean lock) {
        isLocked = lock;
        onContentsChanged(-1);
    }

    public boolean isLockingSupported() {
        return tier.isCreative();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList nbtTagList = new NBTTagList();

        for (int i = 0; i < this.stacks.size(); ++i) {
            if (this.stacks.get(i) != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                this.stacks.get(i)
                    .writeToNBT(itemTag);
                itemTag.setInteger("Count", stacks.get(i).stackSize);

                NBTTagCompound dankSettings = new NBTTagCompound();
                dankSettings.setBoolean(NBT.OREDICT, oreDict.get(i));
                dankSettings.setByte(
                    NBT.EXTRACTION_MODE,
                    (byte) extractionModes.get(i)
                        .ordinal());
                dankSettings.setByte(
                    NBT.PLACEMENT_MODE,
                    (byte) placementModes.get(i)
                        .ordinal());

                itemTag.setTag(NBT.DANK_SETTINGS, dankSettings);

                nbtTagList.appendTag(itemTag);
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", this.stacks.size());
        nbt.setInteger(NBT.SELECTEDINDEX, selected);
        nbt.setBoolean(NBT.LOCKED, isLocked);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.setSize(nbt.hasKey("Size", 3) ? nbt.getInteger("Size") : this.stacks.size());
        NBTTagList tagList = nbt.getTagList("Items", 10);

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                ItemStack loadedStack = ItemStack.loadItemStackFromNBT(itemTags);
                if (loadedStack != null && itemTags.hasKey("Count", Constants.NBT.TAG_INT)) {
                    loadedStack.stackSize = itemTags.getInteger("Count");
                }
                this.stacks.set(slot, loadedStack);

                if (itemTags.hasKey(NBT.DANK_SETTINGS)) {
                    NBTTagCompound dankSettings = itemTags.getCompoundTag(NBT.DANK_SETTINGS);

                    this.oreDict.set(slot, dankSettings.getBoolean(NBT.OREDICT));

                    int extMode = dankSettings.getByte(NBT.EXTRACTION_MODE);
                    int placeMode = dankSettings.getByte(NBT.PLACEMENT_MODE);

                    if (extMode >= 0 && extMode < ItemExtractionMode.values().length) {
                        this.extractionModes.set(slot, ItemExtractionMode.values()[extMode]);
                    }
                    if (placeMode >= 0 && placeMode < ItemPlacementMode.values().length) {
                        this.placementModes.set(slot, ItemPlacementMode.values()[placeMode]);
                    }
                }
            }
        }
        selected = nbt.getInteger(NBT.SELECTEDINDEX);
        isLocked = nbt.getBoolean(NBT.LOCKED);

        this.onLoad();
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (world != null && !world.isRemote && dank != null) {
            DankNullHandler.saveDank(dank, this);

            if (dockTile != null) {
                dockTile.markDirty();
            }
        }
    }

    public static void saveDank(ItemStack dank, DankNullHandler handler) {
        if (dank == null) return;
        NBTTagCompound tag = dank.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setTag(NBT.DANKNULL_CAP, handler.serializeNBT());
        dank.setTagCompound(tag);
    }

    public static class NBT {

        public static final String DANKNULL_CAP = "DankNullCap";
        public static final String OREDICT = "OreDict";
        public static final String SELECTEDINDEX = "selectedIndex";
        public static final String DANK_SETTINGS = "DankSettings";
        public static final String EXTRACTION_MODE = "ExtractionModes";
        public static final String PLACEMENT_MODE = "PlacementModes";
        public static final String LOCKED = "Locked";

        // for the tile
        public static final String BLOCKENTITYTAG = "BlockEntityTag";
        public static final String DOCKEDSTACK = "DankNullStack";
    }
}
