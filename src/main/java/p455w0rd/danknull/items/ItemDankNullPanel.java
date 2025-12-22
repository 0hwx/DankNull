package p455w0rd.danknull.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.DankNullTier;

/**
 * @author p455w0rd
 */
public class ItemDankNullPanel extends Item {

    DankNullTier tier;

    public ItemDankNullPanel(final DankNullTier tier) {
        this.tier = tier;
        setTextureName("danknull:dank_null_panel_0");
        setUnlocalizedName(tier.getUnlocalizedNameForPanel());
        setMaxDamage(0);
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

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(final ItemStack stack) {
        return StatCollector.translateToLocal(
            stack.getItem()
                .getUnlocalizedName() + ".name")
            .trim();
    }

    @Override
    public EnumRarity getRarity(ItemStack p_getRarity_1_) {
        return tier.getRarity();
    }

}
