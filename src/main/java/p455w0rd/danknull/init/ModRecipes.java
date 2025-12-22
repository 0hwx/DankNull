package p455w0rd.danknull.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;

/**
 * @author p455w0rd
 */
public class ModRecipes {

    public static void init() {
        addDankNullUpgradeRecipe(
            " a ",
            "aba",
            " a ",
            'a',
            new ItemStack(ModItems.LAPIS_PANEL),
            'b',
            new ItemStack(ModItems.REDSTONE_DANKNULL));
        addDankNullUpgradeRecipe(
            " a ",
            "aba",
            " a ",
            'a',
            new ItemStack(ModItems.IRON_PANEL),
            'b',
            new ItemStack(ModItems.LAPIS_DANKNULL));
        addDankNullUpgradeRecipe(
            " a ",
            "aba",
            " a ",
            'a',
            new ItemStack(ModItems.GOLD_PANEL),
            'b',
            new ItemStack(ModItems.IRON_DANKNULL));
        addDankNullUpgradeRecipe(
            " a ",
            "aba",
            " a ",
            'a',
            new ItemStack(ModItems.DIAMOND_PANEL),
            'b',
            new ItemStack(ModItems.GOLD_DANKNULL));
        addDankNullUpgradeRecipe(
            " a ",
            "aba",
            " a ",
            'a',
            new ItemStack(ModItems.EMERALD_PANEL),
            'b',
            new ItemStack(ModItems.DIAMOND_DANKNULL));
    }

    private static void addDankNullUpgradeRecipe(Object... params) {
        int idx = 0;
        String shape = "";
        while (params[idx] instanceof String) {
            shape += (String) params[idx];
            idx++;
        }

        Map<Character, ItemStack> itemMap = new HashMap<Character, ItemStack>();
        for (; idx < params.length; idx += 2) {
            itemMap.put((Character) params[idx], (ItemStack) params[idx + 1]);
        }

        ItemStack[] input = new ItemStack[9];
        for (int i = 0; i < shape.length(); i++) {
            char c = shape.charAt(i);
            if (itemMap.containsKey(c)) {
                input[i] = itemMap.get(c);
            }
        }

        GameRegistry.addRecipe(new RecipeDankNullUpgrade(3, 3, input));
    }
}
