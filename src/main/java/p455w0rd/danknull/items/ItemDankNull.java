package p455w0rd.danknull.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.DankNullItemModes;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.network.NetworkHandler;
import p455w0rd.danknull.network.packet.COpenGui;

/**
 * @author p455w0rd
 */
public class ItemDankNull extends Item {

    private final DankNullTier tier;

    public ItemDankNull(final DankNullTier tier) {
        this.tier = tier;
        setTextureName("danknull:dank_null_panel_0");
        setUnlocalizedName(tier.getUnlocalizedNameForDankNull());
        setMaxStackSize(1);
        setMaxDamage(0);
    }

    public static boolean isDankNull(final ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemDankNull;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, @Nullable final EntityPlayer entityPlayer,
        final List<String> tooltip, final boolean advanced) {
        tooltip.add(
            I18n.format("dn.number_of_slots.desc") + ": "
                + DankNullTier.getTier(stack)
                    .getNumRows() * 9);
        final String maxMsg = DankNullTier.getTier(stack) == DankNullTier.CREATIVE
            ? "" + EnumChatFormatting.DARK_PURPLE + I18n.format("dn.infinite.desc") + EnumChatFormatting.GRAY
            : "" + DankNullTier.getTier(stack)
                .getMaxStackSize();
        tooltip.add(maxMsg + " " + I18n.format("dn.items_per_slot.desc"));
    }

    public DankNullTier getTier() {
        return tier;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(final ItemStack stack) {
        return true;
    }

    @Override
    public String getItemStackDisplayName(final ItemStack stack) {
        String name = super.getItemStackDisplayName(stack);
        if (Options.callItDevNull) {
            name = name.replace("/dank/", "/dev/");
        }
        return name;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player) {
        final ItemStack stack = player.getHeldItem();

        if (world.isRemote) {
            // if (player.isSneaking() && getBlockUnderPlayer(player) != Blocks.air) {
            if (player.isSneaking()) {
                int x = (int) Math.floor(player.posX);
                int y = (int) Math.floor(player.posY);
                int z = (int) Math.floor(player.posZ);
                NetworkHandler.sendToServer(new COpenGui(ModGuiHandler.GUIType.DANKNULL, x, y, z));
            }
        }

        // else if (Mods.EXTRA_UTILITIES_2.isLoaded() && ExtraUtilities.isAngelBlockSelected(stack)) {
        // if (!world.isRemote) {
        // return ExtraUtilities.tryPlaceAngelBlock(stack, player, hand);
        // } else {
        // player.swingArm(hand);
        // }
        // }
        // }
        return stack;
    }

    @Override
    public boolean isDamaged(final ItemStack stack) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    private Block getBlockUnderPlayer(final EntityPlayer player) {
        final int blockX = MathHelper.floor_double(player.posX);
        final int blockY = MathHelper.floor_double(player.boundingBox.minY - 0.5);
        final int blockZ = MathHelper.floor_double(player.posZ);
        return player.getEntityWorld()
            .getBlock(blockX, blockY, blockZ);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!(stack.getItem() instanceof ItemDankNull)) return false;

        // if (player.isSneaking() && blockUnderPlayer != Blocks.air && selectedBlock != null && blockUnderPlayer !=
        // selectedBlock) {
        if (player.isSneaking()) {
            if (world.isRemote) {
                NetworkHandler.sendToServer(new COpenGui(ModGuiHandler.GUIType.DANKNULL, x, y, z));
            }
            return false;
        }

        DankNullHandler handler = DankNullHandler.fromStack(world, stack);
        int selected = handler.getSelected();
        if (selected < 0) return false;

        ItemStack stored = handler.getStackInSlot(selected);
        if (stored == null) return false;

        DankNullItemModes.ItemPlacementMode mode = handler.getPlacementMode(selected);
        if (!player.capabilities.isCreativeMode && stored.stackSize <= mode.getNumberToKeep()) {
            return false;
        }

        if (stored.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) stored.getItem();

            ItemStack placeStack = stored.copy();
            placeStack.stackSize = 1;

            boolean placed = itemBlock.onItemUse(placeStack, player, world, x, y, z, side, hitX, hitY, hitZ);

            if (!placed) return false;

            handler.consumeForPlacement(selected);
            DankNullHandler.saveDank(stack, handler);
            return true;
        }
        //
        // if (isFluidContainer(stored)) {
        //
        // if (placeFluid(world, player, stored)) {
        //
        // if (!player.capabilities.isCreativeMode) {
        // handler.consumeForPlacement(selected);
        //// handler.extractItem(selected, 1, false);
        // DankNullHandler.saveDank(stack, handler);
        //
        // // Give empty bucket
        // ItemStack empty = FluidContainerRegistry.drainFluidContainer(stored);
        // if (empty != null) {
        // player.inventory.addItemStackToInventory(empty);
        // }
        // }
        // return true;
        // }
        // }
        return false;
    }

    private boolean placeFluid(World world, EntityPlayer player, ItemStack fluidStack) {
        MovingObjectPosition mop = rayTrace(world, player);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
        x += dir.offsetX;
        y += dir.offsetY;
        z += dir.offsetZ;

        if (!world.isAirBlock(x, y, z) && !world.getBlock(x, y, z)
            .isReplaceable(world, x, y, z)) return false;

        FluidStack fluid = getFluid(fluidStack);
        if (fluid == null) return false;

        Block fluidBlock = fluid.getFluid()
            .getBlock();
        if (fluidBlock == null) return false;

        world.setBlock(x, y, z, fluidBlock);
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, fluidBlock.stepSound.soundName, 1.0F, 1.0F);

        return true;
    }

    private boolean isFluidContainer(ItemStack stack) {
        return FluidContainerRegistry.isContainer(stack) || stack.getItem() instanceof IFluidContainerItem;
    }

    private FluidStack getFluid(ItemStack stack) {
        if (stack.getItem() instanceof IFluidContainerItem) {
            return ((IFluidContainerItem) stack.getItem()).getFluid(stack);
        }
        return FluidContainerRegistry.getFluidForFilledItem(stack);
    }

    private MovingObjectPosition rayTrace(World world, EntityPlayer player) {
        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;

        double x = player.posX;
        double y = player.posY + player.getEyeHeight();
        double z = player.posZ;

        Vec3 start = Vec3.createVectorHelper(x, y, z);
        Vec3 look = player.getLookVec();
        Vec3 end = start.addVector(look.xCoord * 5.0D, look.yCoord * 5.0D, look.zCoord * 5.0D);

        return world.rayTraceBlocks(start, end);
    }

    @Override
    public EnumRarity getRarity(final ItemStack stack) {
        return tier.getRarity();
    }
}
