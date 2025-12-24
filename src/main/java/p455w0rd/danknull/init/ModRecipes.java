package p455w0rd.danknull.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;

/**
 * @author p455w0rd
 */
public class ModRecipes {

    // spotless:off
    public static void init() {
        addPanelRecipe(ModItems.REDSTONE_PANEL, Items.redstone, DankColor.RED);
        addPanelRecipe(ModItems.LAPIS_PANEL, "gemLapis", DankColor.BLUE);
        addPanelRecipe(ModItems.IRON_PANEL, Items.iron_ingot, DankColor.WHITE);
        addPanelRecipe(ModItems.GOLD_PANEL, Items.gold_ingot, DankColor.YELLOW);
        addPanelRecipe(ModItems.DIAMOND_PANEL, Items.diamond, DankColor.CYAN);
        addPanelRecipe(ModItems.EMERALD_PANEL, Items.emerald, DankColor.LIME);

        addBaseCraft(ModItems.REDSTONE_DANKNULL, ModItems.REDSTONE_PANEL);

        registerTier(ModItems.LAPIS_DANKNULL, ModItems.LAPIS_PANEL, ModItems.REDSTONE_DANKNULL);
        registerTier(ModItems.IRON_DANKNULL, ModItems.IRON_PANEL, ModItems.LAPIS_DANKNULL);
        registerTier(ModItems.GOLD_DANKNULL, ModItems.GOLD_PANEL, ModItems.IRON_DANKNULL);
        registerTier(ModItems.DIAMOND_DANKNULL, ModItems.DIAMOND_PANEL, ModItems.GOLD_DANKNULL);
        registerTier(ModItems.EMERALD_DANKNULL, ModItems.EMERALD_PANEL, ModItems.DIAMOND_DANKNULL);

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.DANKNULL_DOCK),
            "aba", "bcb", "aba", 'a', Items.emerald, 'b', Items.redstone, 'c', Blocks.obsidian));
    }

    /**
     * Helper to register both recipes for a tier at once.
     */
    private static void registerTier(Object output, Object panel, Object previousTier) {
        addBaseCraft(output, panel);
        addUpgradeCraft(output, panel, previousTier);
    }

    private static void addPanelRecipe(Object output, Object material, DankColor color) {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack((Item) output),
            "aca", "cbc", "aca",
            'a', material,
            'c', Blocks.coal_block,
            'b', new ItemStack(Blocks.stained_glass_pane, 1, color.getMeta())));
    }

    private static void addBaseCraft(Object output, Object panel) {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack((Item) output), " a ", "aaa", " a ", 'a', panel));
    }

    private static void addUpgradeCraft(Object output, Object panel, Object previousTier) {
        GameRegistry.addRecipe(new RecipeDankNullUpgrade(new ItemStack((Item) output), " a ", "aba", " a ", 'a', panel, 'b', previousTier));
    }
    // spotless:on

    public enum DankColor {

        WHITE(0, "White"),
        YELLOW(4, "Yellow"),
        LIME(5, "Lime"),
        CYAN(9, "Cyan"),
        BLUE(11, "Blue"),
        RED(14, "Red");

        private final int meta;
        private final String name;

        DankColor(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta() {
            return meta;
        }

        public String getName() {
            return name;
        }
    }
}
