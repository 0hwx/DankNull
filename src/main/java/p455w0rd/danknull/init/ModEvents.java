package p455w0rd.danknull.init;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.client.KeyBindings;
import p455w0rd.danknull.client.render.HUDRenderer;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.NetworkHandler;
import p455w0rd.danknull.network.packet.CChangeMode;
import p455w0rd.danknull.network.packet.COpenGui;
import p455w0rd.danknull.util.DankUtils;

/**
 * @author p455w0rd
 */
public class ModEvents {

    @SubscribeEvent
    public void onItemPickUp(final EntityItemPickupEvent event) {
        final EntityPlayer player = event.entityPlayer;
        final ItemStack entityStack = event.item.getEntityItem();
        if (entityStack == null || !(player instanceof EntityPlayerMP)) {
            return;
        }
        // Demagnetize integration
        if (event.item.getEntityData()
            .hasKey("PreventRemoteMovement")) {
            return;
        }
        final ImmutableList<ItemStack> dankNulls = getDankNullForStack(player, entityStack);
        if (dankNulls.isEmpty()) {
            return;
        }

        ItemStack inProgress = entityStack;
        boolean someInteraction = false;

        for (ItemStack dankStack : dankNulls) {
            if (DankUtils.canDankNullAcceptItem(dankStack, entityStack)) {
                DankNullHandler handler = DankNullHandler.fromStack(player.worldObj, dankStack);
                ImmutableList<Integer> positions = handler.findItemStacks(entityStack);
                for (int position : positions) {
                    someInteraction = true;
                    inProgress = handler.insertItem(position, inProgress, false);
                }
            }
        }
        if (someInteraction) {
            entityStack.stackSize = (0);
            event.setResult(Event.Result.ALLOW);
            // if (inProgress.isEmpty()) { // Only play if its empty to prevent duplicate playback
            // player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ,
            // SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 0.2F, ((player.getRNG().nextFloat() -
            // player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            // }
        }
    }

    private static ImmutableList<ItemStack> getDankNullForStack(final EntityPlayer player, final ItemStack stack) {
        final List<ItemStack> dankNulls = DankUtils.getDankNullsForPlayer(player);

        ImmutableList.Builder<ItemStack> validDankNulls = ImmutableList.builder();

        for (ItemStack itemStack : dankNulls) {
            if (itemStack != null && itemStack.getItem() instanceof ItemDankNull) {

                if (DankUtils.canDankNullAcceptItem(itemStack, stack)) {
                    validDankNulls.add(itemStack);
                }
            }
        }
        return validDankNulls.build();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        // Ensure we aren't typing in a search bar or another GUI
        if (Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        if (KeyBindings.toggleHUDOverlay.getIsKeyPressed()) {
            HUDRenderer.toggleHUD();
        }

        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (KeyBindings.openDankNull.isPressed()) {
            final List<ItemStack> dankNulls = DankUtils.getDankNullsForPlayer(player);

            if (!dankNulls.isEmpty()) {
                // Only send the packet ONCE per press
                int x = (int) Math.floor(player.posX);
                int y = (int) Math.floor(player.posY);
                int z = (int) Math.floor(player.posZ);
                NetworkHandler.sendToServer(new COpenGui(ModGuiHandler.GUIType.DANKNULL, x, y, z));
            }
        }

        if (KeyBindings.nextItem.isPressed() || KeyBindings.previousItem.isPressed()) {
            ItemStack held = player.getHeldItem();
            if (held != null && held.getItem() instanceof ItemDankNull) {
                DankNullHandler handler = DankNullHandler.fromStack(player.worldObj, held);
                handler.cycleSelected(KeyBindings.nextItem.getIsKeyPressed());
                NetworkHandler
                    .sendToServer(new CChangeMode(CChangeMode.ChangeType.SELECTED, handler.getSelected(), false));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(final MouseEvent event) {
        if (event.dwheel == 0 && event.button == -1) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        ItemStack heldStack = mc.thePlayer.getHeldItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemDankNull)) return;

        // Delegate to specialized handlers
        // We return early if Middle Click handles the event to avoid redundant checks
        if (handleMiddleClickPick(mc, event, heldStack)) return;

        handleMouseCycling(mc, event, heldStack);
    }

    @SideOnly(Side.CLIENT)
    private void handleMouseCycling(Minecraft mc, MouseEvent event, ItemStack heldStack) {
        boolean isScroll = event.dwheel != 0 && mc.thePlayer.isSneaking();
        boolean isKeyCycle = event.dwheel == 0 && KeyBindings.isAnyModKeybindPressed();

        if (!isScroll && !isKeyCycle) return;

        DankNullHandler handler = DankNullHandler.fromStack(mc.theWorld, heldStack);
        if (handler.stackCount() <= 1) return;

        boolean forward = false;
        boolean shouldCycle = false;

        if (isScroll) {
            forward = event.dwheel < 0;
            shouldCycle = true;
        } else {
            if (KeyBindings.nextItem.getIsKeyPressed()) {
                forward = true;
                shouldCycle = true;
            } else if (KeyBindings.previousItem.getIsKeyPressed()) {
                forward = false;
                shouldCycle = true;
            }
        }

        if (shouldCycle) {
            handler.cycleSelected(forward);
            NetworkHandler.sendToServer(new CChangeMode(CChangeMode.ChangeType.SELECTED, handler.getSelected(), false));
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean handleMiddleClickPick(Minecraft mc, MouseEvent event, ItemStack heldStack) {
        if (event.button != 2 || !event.buttonstate) return false;

        final MovingObjectPosition target = mc.objectMouseOver;
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;

        World world = mc.theWorld;
        if (world.isAirBlock(target.blockX, target.blockY, target.blockZ)) return false;

        Block block = world.getBlock(target.blockX, target.blockY, target.blockZ);
        ItemStack stackToSelect = block
            .getPickBlock(target, world, target.blockX, target.blockY, target.blockZ, mc.thePlayer);

        if (stackToSelect != null) {
            DankNullHandler handler = DankNullHandler.fromStack(world, heldStack);
            int newIndex = handler.findItemStack(stackToSelect);

            if (newIndex != -1) {
                handler.setSelected(newIndex);
                NetworkHandler.sendToServer(new CChangeMode(CChangeMode.ChangeType.SELECTED, newIndex, false));
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPostRenderOverlay(final RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.HOTBAR) {
            final Minecraft mc = Minecraft.getMinecraft();
            HUDRenderer.renderHUD(mc, new ScaledResolution(mc, mc.displayWidth, mc.displayHeight));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            ModConfig.sendConfigsToClient((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(DankNull.MODID)) {
            ModConfig.sync();
        }
    }
}
