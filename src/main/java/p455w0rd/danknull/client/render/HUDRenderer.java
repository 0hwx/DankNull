package p455w0rd.danknull.client.render;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.client.KeyBindings;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankUtils;

/**
 * @author p455w0rd
 */
public class HUDRenderer {

    @SideOnly(Side.CLIENT)
    public static void renderHUD(final Minecraft mc, final ScaledResolution scaledRes) {
        if (!Options.showHUD || !mc.playerController.shouldDrawHUD() && !mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }

        ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        if (currentItem == null || !ItemDankNull.isDankNull(currentItem)) return;

        NBTTagCompound nbt = currentItem.getTagCompound();
        if (nbt == null || !nbt.hasKey(DankNullHandler.NBT.DANKNULL_CAP)) return;

        NBTTagCompound cap = nbt.getCompoundTag(DankNullHandler.NBT.DANKNULL_CAP);
        int selectedIndex = cap.getInteger(DankNullHandler.NBT.SELECTEDINDEX);

        // Find the specific NBT compound for the selected slot
        NBTTagCompound itemTag = DankUtils.getSlotTag(cap, selectedIndex);
        if (itemTag == null) return;

        ItemStack selectedStack = ItemStack.loadItemStackFromNBT(itemTag);
        if (selectedStack == null) return;

        int count = itemTag.getInteger("Count");
        // Pull settings from the new DankSettings sub-compound
        NBTTagCompound settings = itemTag.getCompoundTag(DankNullHandler.NBT.DANK_SETTINGS);
        ItemPlacementMode pMode = ItemPlacementMode.VALUES[settings.getByte(DankNullHandler.NBT.PLACEMENT_MODE)];
        ItemExtractionMode eMode = ItemExtractionMode.VALUES[settings.getByte(DankNullHandler.NBT.EXTRACTION_MODE)];
        boolean oreDict = settings.getBoolean(DankNullHandler.NBT.OREDICT);

        DankNullTier tier = DankNullTier.getTier(currentItem);

        renderHUDContents(mc, scaledRes, currentItem, count, selectedStack, tier, pMode, eMode, oreDict);
    }

    private static void renderHUDContents(Minecraft mc, ScaledResolution scaledRes, ItemStack stack, int stackSize,
        ItemStack selectedStack, DankNullTier tier, ItemPlacementMode pMode, ItemExtractionMode eMode,
        boolean oreDictEnabled) {
        final TextureManager tm = mc.renderEngine;
        if (tm == null) return;

        tm.bindTexture(new ResourceLocation(DankNull.MODID, "textures/gui/danknullscreen0.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GuiUtils.drawTexturedModalRect(
            scaledRes.getScaledWidth() - 106,
            scaledRes.getScaledHeight() - 45,
            0,
            210,
            106,
            45,
            0);

        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        String dankNullName = stack.getDisplayName();
        mc.fontRenderer.drawStringWithShadow(
            dankNullName,
            scaledRes.getScaledWidth() * 2 - 212 + 55,
            scaledRes.getScaledHeight() * 2 - 83,
            tier.getHexColor(true));

        // Selected stack info
        String selectedName = selectedStack.getDisplayName();
        int maxWidth = 88;
        if (mc.fontRenderer.getStringWidth(selectedName) > maxWidth) {
            selectedName = selectedName.substring(0, 14)
                .trim() + "...";
        }

        mc.fontRenderer.drawStringWithShadow(
            StatCollector.translateToLocal("dn.selected_item.desc") + ": " + selectedName,
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 72,
            16777215);
        mc.fontRenderer.drawStringWithShadow(
            StatCollector.translateToLocal("dn.count.desc") + ": "
                + (DankNullTier.getTier(stack) == DankNullTier.CREATIVE ? "Infinite" : stackSize),
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 61,
            16777215);
        mc.fontRenderer.drawStringWithShadow(
            StatCollector.translateToLocal("dn.place.desc") + ": "
                + pMode.getTooltip()
                    .replace(
                        StatCollector.translateToLocal("dn.extract.desc")
                            .toLowerCase(Locale.ENGLISH),
                        StatCollector.translateToLocal("dn.place.desc")
                            .toLowerCase(Locale.ENGLISH))
                    .replace(
                        StatCollector.translateToLocal("dn.extract.desc"),
                        StatCollector.translateToLocal("dn.place.desc")),
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 50,
            16777215);
        mc.fontRenderer.drawStringWithShadow(
            StatCollector.translateToLocal("dn.extract.desc") + ": " + eMode.getTooltip(),
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 40,
            16777215);
        mc.fontRenderer.drawStringWithShadow(
            StatCollector.translateToLocal("dn.extract.desc") + ": ",
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 40,
            16777215);

        final String keyBind = GameSettings.getKeyDisplayString(KeyBindings.openDankNull.getKeyCode());
        mc.fontRenderer.drawStringWithShadow(
            keyBind.equalsIgnoreCase("none") ? StatCollector.translateToLocal("dn.no_open_keybind.desc")
                : StatCollector.translateToLocal("dn.open_with.desc") + " " + keyBind,
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 29,
            16777215);
        String oreDictMode = StatCollector.translateToLocal("dn.ore_dictionary.desc") + ": "
            + (oreDictEnabled ? StatCollector.translateToLocal("dn.enabled.desc")
                : StatCollector.translateToLocal("dn.disabled.desc"));
        final boolean isOreDicted = DankUtils.getOreNames(selectedStack)
            .size() > 0;
        if (!isOreDicted) {
            oreDictMode = StatCollector.translateToLocal("dn.not_oredicted.desc");
        }

        mc.fontRenderer.drawStringWithShadow(
            oreDictMode,
            scaledRes.getScaledWidth() * 2 - 212 + 45,
            scaledRes.getScaledHeight() * 2 - 18,
            16777215);

        GL11.glPopMatrix();

        // Draw item icon
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        RenderItem.getInstance()
            .renderItemAndEffectIntoGUI(
                mc.fontRenderer,
                mc.renderEngine,
                stack,
                scaledRes.getScaledWidth() - 106 + 5,
                scaledRes.getScaledHeight() - 20);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void toggleHUD() {
        Options.showHUD = !Options.showHUD;
        ModConfig.getInstance()
            .get(Configuration.CATEGORY_GENERAL, "showHUD", true)
            .setValue(Options.showHUD);
        ModConfig.getInstance()
            .save();
    }
}
