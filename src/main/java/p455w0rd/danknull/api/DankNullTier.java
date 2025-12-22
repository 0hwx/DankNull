package p455w0rd.danknull.api;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.EnumHelper;

import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.items.ItemBlockDankNullDock;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullPanel;

public enum DankNullTier {

    REDSTONE(-10092544),
    LAPIS(-16777114),
    IRON(-10066330),
    GOLD(-10066432),
    DIAMOND(-12097946),
    EMERALD(-16751104),
    CREATIVE(0xFF8F15D4),
    NONE(-8372020);

    int glintcolor;

    DankNullTier(int glintcolor) {
        this.glintcolor = glintcolor;
    }

    public int getGlintcolor() {
        return glintcolor;
    }

    //@formatter:off
    private static final int[] OPAQUE_HEX_COLORS = new int[]{
        0xFFEC4848,
        0xFF4885EC,
        0xFFFFFFFF,
        0xFFFFFF00,
        0xFF00FFFF,
        0xFF17FF6D,
        0xFF8F15D4,
        0x0
    };
    private static final int[] HEX_COLORS = new int[]{
        0x99EC4848,
        0x994885EC,
        0x99FFFFFF,
        0x99FFFF00,
        0x9900FFFF,
        0x9917FF6D,
        0x998F15D4,
        0x0
    };
    public static DankNullTier[] VALUES = values();
    //@formatter:on

    public String getUnlocalizedNameForDankNull() {
        return "dank_null_" + ordinal();
    }

    public String getUnlocalizedNameForPanel() {
        return "dank_null_panel_" + ordinal();
    }

    public int getMaxStackSize() {
        final int level = ordinal() + 1;
        if (level >= 6) {
            return Integer.MAX_VALUE;
        }
        return level * 128 * level;
    }

    public int getNumRows() {
        int numRows = ordinal();
        if (isCreative()) {
            numRows--;
        }
        return numRows + 1;
    }

    public int getSlots() {
        return getNumRows() * 9;
    }

    public int getNumRowsMultiplier() {
        return getNumRows() - 1;
    }

    public boolean isCreative() {
        return ordinal() == 6;
    }

    // 140=player inv
    public int getGuiHeight() {
        return 140 + getNumRowsMultiplier() * 20 + getNumRowsMultiplier() + 1;
    }

    public ResourceLocation getGuiBackground() {
        return new ResourceLocation(
            DankNull.MODID,
            "textures/gui/danknullscreen" + (getNumRowsMultiplier() + (isCreative() ? 1 : 0)) + ".png");
    }

    public EnumRarity getRarity() {
        return Rarities.getRarityFromMeta(ordinal());
    }

    public int getHexColor(final boolean opaque) {
        return opaque ? OPAQUE_HEX_COLORS[ordinal()] : HEX_COLORS[ordinal()];
    }

    public ItemStack getUpgradedVersion(final ItemStack dankNull) {
        if (ordinal() < 6) {
            final NBTTagCompound raw = new NBTTagCompound();
            dankNull.writeToNBT(raw);
            if (raw.hasKey("id", Constants.NBT.TAG_STRING)) {
                final String[] id = raw.getString("id")
                    .split(":");
                if (id.length > 0 && id[1].startsWith("dank_null_")) {
                    raw.setString("id", id[0] + ":dank_null_" + (ordinal() + 1));
                    return ItemStack.loadItemStackFromNBT(raw);
                }
            }
        }
        return dankNull.copy();
    }

    public static DankNullTier getTier(final ItemStack dankNull) {
        int meta = -1;

        if (dankNull == null || dankNull.getItem() == null) return DankNullTier.NONE;

        if (dankNull.getItem() instanceof ItemDankNull dank) {
            meta = dank.getTier()
                .ordinal();
        }

        if (dankNull.getItem() instanceof ItemDankNullPanel dank) {
            meta = dank.getTier()
                .ordinal();
        }

        if (dankNull.getItem() instanceof ItemBlockDankNullDock) {
            final ItemStack dockedDank = ItemBlockDankNullDock.getDockedDankNull(dankNull);
            final boolean isEmpty = dockedDank == null;
            if (dockedDank.getItem() instanceof ItemDankNull dank) {
                meta = !isEmpty ? dank.getTier()
                    .ordinal() : -1;
            }
        }
        return meta == -1 ? DankNullTier.NONE : DankNullTier.VALUES[meta];
    }

    public static DankNullTier isDankNull(final ItemStack dankNull) {
        if (dankNull == null || dankNull.getItem() == null) return DankNullTier.NONE;

        if (dankNull.getItem() instanceof ItemDankNull dank) {
            return DankNullTier.VALUES[dank.getTier()
                .ordinal()];
        }

        return DankNullTier.NONE;
    }

    public static class Rarities {

        private static final EnumRarity[] RARITY_CACHE = new EnumRarity[] { //@formatter:off
            createRarity("dn:redstone", EnumChatFormatting.RED),
            createRarity("dn:lapis", EnumChatFormatting.BLUE),
            createRarity("dn:iron", EnumChatFormatting.WHITE),
            createRarity("dn:gold", EnumChatFormatting.YELLOW),
            createRarity("dn:diamond", EnumChatFormatting.AQUA),
            createRarity("dn:emerald", EnumChatFormatting.GREEN),
            createRarity("dn:creative", EnumChatFormatting.LIGHT_PURPLE)//@formatter:on
        };

        public static EnumRarity getRarityFromMeta(final int meta) {
            return RARITY_CACHE[meta];
        }

        private static EnumRarity createRarity(final String name, final EnumChatFormatting color) {
            return EnumHelper.addRarity(name, color, name);
        }

    }

}
