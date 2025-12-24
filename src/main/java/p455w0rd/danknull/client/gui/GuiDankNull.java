package p455w0rd.danknull.client.gui;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.inventory.container.ContainerDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.network.IPacket;
import p455w0rd.danknull.network.NetworkHandler;
import p455w0rd.danknull.network.packet.CChangeMode;
import p455w0rd.danknull.util.DankUtils;
import p455w0rd.danknull.util.ReadableNumberConverter;
import p455w0rd.danknull.util.inv.ClickType;
import yalter.mousetweaks.api.MouseTweaksIgnore;

/**
 * @author p455w0rd
 */
@MouseTweaksIgnore
public class GuiDankNull extends GuiContainer {

    private final DankNullTier tier;
    private ResourceLocation backgroundTexture;

    public GuiDankNull(final ContainerDankNull c) {
        super(c);
        tier = c.getHandler()
            .getTier();
        xSize = 210;
        ySize = tier.getGuiHeight();
        backgroundTexture = tier.getGuiBackground();
    }

    public DankNullHandler getDankNullHandler() {
        return ((ContainerDankNull) inventorySlots).getHandler();
    }

    @Override
    public void initGui() {
        super.initGui();

        if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && tier.isCreative()) {
            buttonList.clear();
            buttonList.add(
                new GuiButton(
                    0,
                    guiLeft + xSize / 2 - 25,
                    guiTop - 20,
                    50,
                    20,
                    (getDankNullHandler().isLocked() ? "Unl" : "L") + "ock"));
        }
    }

    @Override
    protected void actionPerformed(final GuiButton btn) {
        final DankNullHandler dankNullHandler = getDankNullHandler();
        if (btn.id == 0) {
            final String lock = StatCollector.translateToLocal("dn.lock.desc");
            final String unlock = StatCollector.translateToLocal("dn.unlock.desc");
            boolean isLocked = false;
            if (btn.displayString.equals(lock)) {
                btn.displayString = unlock;
                dankNullHandler.setLocked(true);
                isLocked = true;
            } else {
                btn.displayString = lock;
                dankNullHandler.setLocked(false);
            }
            NetworkHandler
                .sendToServer(new CChangeMode(isLocked ? CChangeMode.ChangeType.LOCK : CChangeMode.ChangeType.UNLOCK));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        final DankNullHandler dankNullHandler = getDankNullHandler();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        final int fontColor = 16777215;
        final int yOffset = tier.getNumRows() * 20 + 18 + tier.getNumRows() - 1;
        final String name = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
        mc.fontRenderer.drawString(name, 7, 6, tier.getHexColor(true), true);
        mc.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 7, yOffset, fontColor);
        if (dankNullHandler.getSelected() > -1) {
            mc.fontRenderer
                .drawString("=" + StatCollector.translateToLocal("dn.selected.desc"), xSize - 64, 6, fontColor);
        }
        GL11.glPushMatrix();
        // Draw the top "Selected" indicator box
        if (dankNullHandler.getSelected() > -1) {
            drawSelectionBox(xSize);
        }

        // Draw the highlight around the actual slot in the grid
        int selectedIndex = dankNullHandler.getSelected();
        if (selectedIndex != -1) {
            Slot selectedSlot = getSlotByIndex(selectedIndex);
            // If the selected slot is empty, find the fallback highlight
            if (selectedSlot == null || !selectedSlot.getHasStack()) {
                selectedSlot = findFallbackSlot(selectedIndex);
            }

            if (selectedSlot != null) {
                drawSelectionBox(selectedSlot.xDisplayPosition, selectedSlot.yDisplayPosition);
            }
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); // Correct for 1.7.10
        mc.getTextureManager()
            .bindTexture(backgroundTexture);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    private void drawSelectionBox(final int x) {
        final int selectedBoxColor = getDankNullHandler().getTier()
            .ordinal() == 0 ? 0xFFFFFF00 : -1140916224;
        drawGradientRect(x - 75, 4, x - 66, 5, selectedBoxColor, selectedBoxColor);
        drawGradientRect(x - 75, 4, x - 74, 14, selectedBoxColor, selectedBoxColor);
        drawGradientRect(x - 75, 13, x - 66, 14, selectedBoxColor, selectedBoxColor);
        drawGradientRect(x - 66, 4, x - 65, 14, selectedBoxColor, selectedBoxColor);
    }

    private void drawSelectionBox(final int x, final int y) {
        final int selectedBoxColor = getDankNullHandler().getTier()
            .ordinal() == 0 ? 0xFFFFFF00 : -1140916224;
        drawGradientRect(x - 1, y - 1, x + 16, y, selectedBoxColor, selectedBoxColor);
        drawGradientRect(x - 1, y - 1, x, y + 17, selectedBoxColor, selectedBoxColor);
        drawGradientRect(x + 16, y - 1, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
        drawGradientRect(x - 1, y + 16, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
    }

    /**
     * In 1.7.10, func_146977_a is the internal method for "drawSlot".
     * By overriding this, we don't need a loop in drawScreen at all.
     */
    @Override
    public void func_146977_a(Slot slot) {
        if (slot instanceof SlotDankNull) {
            // Run your custom Dank/Null logic
            this.drawDankNullSlot(slot);
        } else {
            // Run vanilla logic for normal player slots
            super.func_146977_a(slot);
        }
    }

    // Helper to find the first available slot to highlight if selected is empty
    private Slot findFallbackSlot(int startIndex) {
        // Search backwards
        for (int i = startIndex; i >= 0; i--) {
            Slot s = getSlotByIndex(i);
            if (s != null && s.getHasStack()) return s;
        }
        // Search forwards
        int maxSlots = tier.getNumRows() * 9;
        for (int i = 0; i < maxSlots; i++) {
            Slot s = getSlotByIndex(i);
            if (s != null && s.getHasStack()) return s;
        }
        return null;
    }

    public Slot getSlotByIndex(final int index) {
        final List<Slot> slots = inventorySlots.inventorySlots;
        return slots.get(index + 36);
    }

    public Slot getSlotAtPosition(final int x, final int y) {
        final List<Slot> slots = inventorySlots.inventorySlots;
        for (int i = 0; i < slots.size(); i++) {
            if (isMouseOverSlot(slots.get(i), x, y)) {
                return slots.get(i);
            }
        }
        return null;
    }

    private void drawDankNullSlot(final Slot slotIn) {
        final int i = slotIn.xDisplayPosition;
        final int j = slotIn.yDisplayPosition;
        ItemStack itemstack = slotIn.getStack();

        boolean isBeingDragged = slotIn == clickedSlot && draggedStack != null && !isRightMouseClick;
        final ItemStack mouseStack = this.mc.thePlayer.inventory.getItemStack();

        if (slotIn == clickedSlot && draggedStack != null && isRightMouseClick && itemstack != null) {
            itemstack = itemstack.copy();
            itemstack.stackSize = (itemstack.stackSize / 2);
        } else if (field_147007_t && field_147008_s.contains(slotIn) && mouseStack != null) {
            if (field_147008_s.size() == 1) {
                return;
            }
            field_147008_s.remove(slotIn);
        }
        zLevel = 100.0F;
        itemRender.zLevel = 100.0F;
        if (itemstack == null) {
            IIcon iicon = slotIn.getBackgroundIconIndex();
            if (iicon != null) {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                mc.getTextureManager()
                    .bindTexture(TextureMap.locationItemsTexture);
                this.drawTexturedModelRectFromIcon(i, j, iicon, 16, 16);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                isBeingDragged = true;
            }
        }
        if (!isBeingDragged && itemstack != null) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            final ItemStack tempStack = itemstack.copy();
            tempStack.stackSize = 1;
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), tempStack, i, j);
            renderItemOverlayIntoGUI(this.fontRendererObj, itemstack, i, j);
        }
        itemRender.zLevel = 0.0F;
        zLevel = 0.0F;
    }

    private void renderItemOverlayIntoGUI(final FontRenderer fontRenderer, @Nonnull final ItemStack is, final int par4,
        final int par5) {
        if (is != null) {
            float scaleFactor = 0.5F;
            float inverseScaleFactor = 1.0F / scaleFactor;
            int offset = -1;
            String stackSize = "";
            final boolean unicodeFlag = fontRenderer.getUnicodeFlag();
            fontRenderer.setUnicodeFlag(false);
            if (is.getItem()
                .showDurabilityBar(is)) {
                final double health = is.getItem()
                    .getDurabilityForDisplay(is);
                final int j = (int) Math.round(13.0D - health * 13.0D);
                final int i = (int) Math.round(255.0D - health * 255.0D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                final Tessellator tessellator = Tessellator.instance;
                draw(tessellator, par4 + 2, par5 + 13, 13, 2, 0, 0, 0, 255);
                draw(tessellator, par4 + 2, par5 + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
                draw(tessellator, par4 + 2, par5 + 13, j, 1, 255 - i, i, 0, 255);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
            }
            final int amount = is.stackSize;
            if (amount != 0) {
                scaleFactor = 0.5F;
                inverseScaleFactor = 1.0F / scaleFactor;
                offset = -1;
                stackSize = getToBeRenderedStackSize(amount);
            }
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPushMatrix();
            GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
            final int X = (int) ((par4 + offset + 16.0F - fontRenderer.getStringWidth(stackSize) * scaleFactor)
                * inverseScaleFactor);
            final int Y = (int) ((par5 + offset + 16.0F - 7.0F * scaleFactor) * inverseScaleFactor);
            if (amount > 1L) {
                fontRenderer.drawStringWithShadow(stackSize, X, Y, 16777215);
            }
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            fontRenderer.setUnicodeFlag(unicodeFlag);
        }
    }

    private void draw(final Tessellator renderer, final int x, final int y, final int width, final int height,
        final int red, final int green, final int blue, final int alpha) {
        renderer.startDrawingQuads();
        renderer.setColorRGBA(red, green, blue, alpha);
        renderer.addVertex(x + 0, y + 0, 0.0D);
        renderer.addVertex(x + 0, y + height, 0.0D);
        renderer.addVertex(x + width, y + height, 0.0D);
        renderer.addVertex(x + width, y + 0, 0.0D);
        renderer.draw();
    }

    private String getToBeRenderedStackSize(final long originalSize) {
        return ReadableNumberConverter.INSTANCE.toWideReadableForm(originalSize);
    }

    @Override
    public void updateScreen() {
        if (getDankNullHandler() == null || !Minecraft.getMinecraft().thePlayer.isEntityAlive()
            || Minecraft.getMinecraft().thePlayer.isDead) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == 1 || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.thePlayer.closeScreen();
        }
        if (theSlot != null && theSlot.getHasStack()) {
            if (keyCode == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                handleMouseClick(theSlot, theSlot.slotNumber, 0, ClickType.CLONE.toNumber());
            } else if (keyCode == mc.gameSettings.keyBindDrop.getKeyCode()) {
                handleMouseClick(
                    theSlot,
                    theSlot.slotNumber,
                    isCtrlKeyDown() && !(theSlot instanceof SlotDankNull) ? 1 : 0,
                    ClickType.THROW.toNumber());
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        Slot hoveredSlot = this.getSlotAtPosition(mouseX, mouseY);

        // check if it is a valid DankNull slot and has an item
        boolean validSlot = hoveredSlot instanceof SlotDankNull && hoveredSlot.getHasStack();

        if (validSlot && mouseButton == 0) {
            DankNullHandler handler = getDankNullHandler();
            boolean shouldSync = false;
            IPacket syncPacket = null;
            int index = hoveredSlot.getSlotIndex();

            // Check P Key
            if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
                ItemPlacementMode nextMode = handler.getNextPlacementMode(index, true);
                handler.setPlacementMode(index, nextMode);
                syncPacket = new CChangeMode(nextMode, index);
                shouldSync = true;
            }
            // Check O Key
            else if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
                boolean newState = !handler.isOre(index);
                handler.setOre(index, newState);
                // syncPacket = new CChangeMode(newState ? CChangeMode.ChangeType.ORE_OFF :
                // CChangeMode.ChangeType.ORE_ON, index); this is how it was original, will check later to see if it
                // broken
                syncPacket = new CChangeMode(
                    newState ? CChangeMode.ChangeType.ORE_ON : CChangeMode.ChangeType.ORE_OFF,
                    index);
                shouldSync = true;
            }
            // Check Ctrl (Extraction)
            else if (isCtrlKeyDown() && !isAltKeyDown()) {
                ItemExtractionMode nextMode = handler.getNextExtractionMode(index, true);
                handler.setExtractionMode(index, nextMode);
                syncPacket = new CChangeMode(nextMode, index);
                shouldSync = true;
            }
            // Check Alt (Selection)
            else if (isAltKeyDown() && !isCtrlKeyDown()) {
                if (index >= 0) {
                    handler.setSelected(index);
                    syncPacket = new CChangeMode(CChangeMode.ChangeType.SELECTED, index);
                    shouldSync = true;
                }
            }

            // 4. If we did a custom action...
            if (shouldSync) {
                if (syncPacket != null) {
                    NetworkHandler.sendToServer(syncPacket);
                }
                inventorySlots.detectAndSendChanges();
                // Play a click sound (since we are cancelling the default click)
                this.mc.getSoundHandler()
                    .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));

                // Do NOT call super.mouseClicked()
                // This prevents the game from picking up the item.
                return;
            }
        }

        // If no custom keys were pressed, let the game handle the normal click (pickup item)
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    // add chisel and thaumcraft
    @Override
    protected void renderToolTip(final ItemStack stack, final int x, final int y) {
        if (stack == null) return;

        List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(
                    i,
                    stack.getItem()
                        .getRarity(stack).rarityColor + list.get(i));
            } else {
                list.set(i, EnumChatFormatting.GRAY + list.get(i));
            }
        }

        final Slot s = getSlotAtPosition(x, y);
        final DankNullHandler dankNullHandler = getDankNullHandler();
        if (s instanceof SlotDankNull && s.getHasStack()) {
            final boolean showOreDictMessage = ModConfig.isOreDictBlacklistEnabled()
                && !ModConfig.isItemOreDictBlacklisted(s.getStack())
                || ModConfig.isOreDictWhitelistEnabled() && ModConfig.isItemOreDictWhitelisted(s.getStack())
                || !ModConfig.isOreDictBlacklistEnabled() && !ModConfig.isOreDictWhitelistEnabled();
            final ItemExtractionMode extractMode = dankNullHandler.getExtractionMode(s.getSlotIndex());
            final ItemPlacementMode placementMode = dankNullHandler.getPlacementMode(s.getSlotIndex());
            final Block selectedBlock = Block.getBlockFromItem(stack.getItem());
            final boolean isSelectedStackABlock = selectedBlock != null && selectedBlock != Blocks.air;
            if (extractMode != null) {
                list.add(1, StatCollector.translateToLocal("dn.extract_mode.desc") + ": " + extractMode.getTooltip());
            }

            list.add(
                2,
                EnumChatFormatting.GRAY + ""
                    + EnumChatFormatting.ITALIC
                    + "  "
                    + StatCollector.translateToLocal("dn.ctrl_click_change.desc"));
            if (isSelectedStackABlock) {
                list.add(
                    2,
                    EnumChatFormatting.GRAY + ""
                        + EnumChatFormatting.ITALIC
                        + "  "
                        + StatCollector.translateToLocal("dn.p_click_toggle.desc"));
            }
            if (dankNullHandler.getSelected() != s.getSlotIndex()) {
                list.add(
                    3,
                    EnumChatFormatting.GRAY + ""
                        + EnumChatFormatting.ITALIC
                        + "  "
                        + StatCollector.translateToLocal("dn.alt_click_set.desc"));
            }
            if (placementMode != null && isSelectedStackABlock) {
                list.add(
                    1,
                    StatCollector.translateToLocal("dn.placement_mode.desc") + ": "
                        + placementMode.getTooltip()
                            .replace(
                                StatCollector.translateToLocal("dn.extract.desc")
                                    .toLowerCase(Locale.ENGLISH),
                                StatCollector.translateToLocal("dn.place.desc")
                                    .toLowerCase(Locale.ENGLISH))
                            .replace(
                                StatCollector.translateToLocal("dn.extract.desc"),
                                StatCollector.translateToLocal("dn.place.desc")));
            }
            if (showOreDictMessage) {
                final String oreDictMode = dankNullHandler.isOre(s.getSlotIndex())
                    ? StatCollector.translateToLocal("dn.enabled.desc")
                    : StatCollector.translateToLocal("dn.disabled.desc");
                final boolean oreDicted = OreDictionary.getOreIDs(s.getStack()).length > 0;
                int lineOffset = 0;
                if (oreDicted) {
                    list.add(2, StatCollector.translateToLocal("dn.ore_dictionary.desc") + ": " + oreDictMode);
                    final List<String> oreNames = DankUtils.getOreNames(stack);
                    if (oreNames.size() > 0 && GuiScreen.isShiftKeyDown()) {
                        list.add(
                            3,
                            "" + EnumChatFormatting.YELLOW
                                + EnumChatFormatting.UNDERLINE
                                + EnumChatFormatting.BOLD
                                + " Enabled OreDict Conversions: ");
                    }
                    if (GuiScreen.isShiftKeyDown()) {
                        for (int i = 0; i < oreNames.size(); i++) {
                            lineOffset = 5 + i;
                            list.add(
                                4 + i,
                                "" + EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC + "   - " + oreNames.get(i));
                        }
                    }
                }
                if (oreDicted) {
                    list.add(
                        GuiScreen.isShiftKeyDown() ? lineOffset + 1 : 4,
                        EnumChatFormatting.GRAY + ""
                            + EnumChatFormatting.ITALIC
                            + "  "
                            + StatCollector.translateToLocal("dn.o_click_toggle.desc"));
                    /*
                     * final List<String> oreNames = DankNullHandler.getOreNames(stack);
                     * for (int i = 0; i < oreNames.size(); i++) {
                     * list.add(5 + i, EnumChatFormatting.ITALIC + " - " + oreNames.get(i));
                     * }
                     */
                }

            }
        }

        drawToolTipWithBorderColor(
            this,
            list,
            x,
            y,
            dankNullHandler.getTier()
                .getHexColor(true), // top/left border
            dankNullHandler.getTier()
                .getHexColor(false)); // bottom/right borde
    }

    public void drawToolTipWithBorderColor(final GuiScreen gui, final List<String> text, final int x, final int y,
        final int borderColor1, final int borderColor2) {
        drawHoveringText(
            gui,
            text,
            x,
            y,
            Minecraft.getMinecraft().fontRenderer,
            0xF0100010,
            borderColor1,
            borderColor2);
    }

    private void drawHoveringText(final GuiScreen gui, final List<String> textLines, final int x, final int y,
        final FontRenderer font, final int backgroundColor, final int borderColor1, final int borderColor2) {
        // net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, gui.width, gui.height, -1,
        // font);
        if (textLines.isEmpty()) return;

        if (!textLines.isEmpty()) {
            GL11.glPushMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int i = 0;

            for (final String s : textLines) {
                final int j = font.getStringWidth(s);

                if (j > i) {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > gui.width) {
                l1 -= 28 + i;
            }

            if (i2 + k + 8 > gui.height) {
                i2 = gui.height - k - 8;
            }

            this.zLevel = 300.0f;
            this.itemRender.zLevel = 300.0f;
            drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, backgroundColor, backgroundColor);
            drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, backgroundColor, backgroundColor);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, backgroundColor, backgroundColor);
            drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, backgroundColor, backgroundColor);
            drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, backgroundColor, backgroundColor);
            drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, borderColor1, borderColor2);
            drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, borderColor1, borderColor2);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, borderColor1, borderColor1);
            drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, borderColor2, borderColor2);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                final String s1 = textLines.get(k1);
                font.drawStringWithShadow(s1, l1, i2, -1);

                if (k1 == 0) {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0f;
            this.itemRender.zLevel = 0.0f;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();

        }
    }

    public void drawGradientRect(final int left, final int top, final int right, final int bottom, final int startColor,
        final int endColor) {
        final float f = (startColor >> 24 & 255) / 255.0F;
        final float f1 = (startColor >> 16 & 255) / 255.0F;
        final float f2 = (startColor >> 8 & 255) / 255.0F;
        final float f3 = (startColor & 255) / 255.0F;

        final float f4 = (endColor >> 24 & 255) / 255.0F;
        final float f5 = (endColor >> 16 & 255) / 255.0F;
        final float f6 = (endColor >> 8 & 255) / 255.0F;
        final float f7 = (endColor & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(right, top, this.zLevel);
        tessellator.addVertex(left, top, this.zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(left, bottom, this.zLevel);
        tessellator.addVertex(right, bottom, this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
