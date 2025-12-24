package p455w0rd.danknull.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.render.DankNullDockRenderer;
import p455w0rd.danknull.client.render.DankNullPanelRenderer;
import p455w0rd.danknull.client.render.DankNullRenderer;
import p455w0rd.danknull.client.render.TESRDankNullDock;
import p455w0rd.danknull.client.render.entity.DankEntityItem;
import p455w0rd.danknull.client.render.entity.DankRenderItem;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullPanel;

/**
 * @author p455w0rd
 *
 */
public class ModItems {

    public static ItemDankNull REDSTONE_DANKNULL;
    public static ItemDankNull LAPIS_DANKNULL;
    public static ItemDankNull IRON_DANKNULL;
    public static ItemDankNull GOLD_DANKNULL;
    public static ItemDankNull DIAMOND_DANKNULL;
    public static ItemDankNull EMERALD_DANKNULL;
    public static ItemDankNull CREATIVE_DANKNULL;

    public static ItemDankNullPanel REDSTONE_PANEL;
    public static ItemDankNullPanel LAPIS_PANEL;
    public static ItemDankNullPanel IRON_PANEL;
    public static ItemDankNullPanel GOLD_PANEL;
    public static ItemDankNullPanel DIAMOND_PANEL;
    public static ItemDankNullPanel EMERALD_PANEL;

    public static final List<Item> ITEM_LIST = new ArrayList<Item>();

    public static void init() {
        REDSTONE_DANKNULL = new ItemDankNull(DankNullTier.REDSTONE);
        LAPIS_DANKNULL = new ItemDankNull(DankNullTier.LAPIS);
        IRON_DANKNULL = new ItemDankNull(DankNullTier.IRON);
        GOLD_DANKNULL = new ItemDankNull(DankNullTier.GOLD);
        DIAMOND_DANKNULL = new ItemDankNull(DankNullTier.DIAMOND);
        EMERALD_DANKNULL = new ItemDankNull(DankNullTier.EMERALD);
        CREATIVE_DANKNULL = new ItemDankNull(DankNullTier.CREATIVE);

        REDSTONE_PANEL = new ItemDankNullPanel(DankNullTier.REDSTONE);
        LAPIS_PANEL = new ItemDankNullPanel(DankNullTier.LAPIS);
        IRON_PANEL = new ItemDankNullPanel(DankNullTier.IRON);
        GOLD_PANEL = new ItemDankNullPanel(DankNullTier.GOLD);
        DIAMOND_PANEL = new ItemDankNullPanel(DankNullTier.DIAMOND);
        EMERALD_PANEL = new ItemDankNullPanel(DankNullTier.EMERALD);

        registerItem(REDSTONE_DANKNULL);
        registerItem(LAPIS_DANKNULL);
        registerItem(IRON_DANKNULL);
        registerItem(GOLD_DANKNULL);
        registerItem(DIAMOND_DANKNULL);
        registerItem(EMERALD_DANKNULL);
        registerItem(CREATIVE_DANKNULL);

        registerItem(REDSTONE_PANEL);
        registerItem(LAPIS_PANEL);
        registerItem(IRON_PANEL);
        registerItem(GOLD_PANEL);
        registerItem(DIAMOND_PANEL);
        registerItem(EMERALD_PANEL);
    }

    private static void registerItem(Item item) {
        String name = item.getUnlocalizedName()
            .substring(5);
        GameRegistry.registerItem(item, name);
        ITEM_LIST.add(item);
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        MinecraftForgeClient.registerItemRenderer(REDSTONE_DANKNULL, new DankNullRenderer(DankNullTier.REDSTONE));
        MinecraftForgeClient.registerItemRenderer(LAPIS_DANKNULL, new DankNullRenderer(DankNullTier.LAPIS));
        MinecraftForgeClient.registerItemRenderer(IRON_DANKNULL, new DankNullRenderer(DankNullTier.IRON));
        MinecraftForgeClient.registerItemRenderer(GOLD_DANKNULL, new DankNullRenderer(DankNullTier.GOLD));
        MinecraftForgeClient.registerItemRenderer(DIAMOND_DANKNULL, new DankNullRenderer(DankNullTier.DIAMOND));
        MinecraftForgeClient.registerItemRenderer(EMERALD_DANKNULL, new DankNullRenderer(DankNullTier.EMERALD));
        MinecraftForgeClient.registerItemRenderer(CREATIVE_DANKNULL, new DankNullRenderer(DankNullTier.CREATIVE));

        MinecraftForgeClient.registerItemRenderer(REDSTONE_PANEL, new DankNullPanelRenderer(DankNullTier.REDSTONE));
        MinecraftForgeClient.registerItemRenderer(LAPIS_PANEL, new DankNullPanelRenderer(DankNullTier.LAPIS));
        MinecraftForgeClient.registerItemRenderer(IRON_PANEL, new DankNullPanelRenderer(DankNullTier.IRON));
        MinecraftForgeClient.registerItemRenderer(GOLD_PANEL, new DankNullPanelRenderer(DankNullTier.GOLD));
        MinecraftForgeClient.registerItemRenderer(DIAMOND_PANEL, new DankNullPanelRenderer(DankNullTier.DIAMOND));
        MinecraftForgeClient.registerItemRenderer(EMERALD_PANEL, new DankNullPanelRenderer(DankNullTier.EMERALD));

        RenderingRegistry.registerEntityRenderingHandler(DankEntityItem.class, new DankRenderItem());

        ClientRegistry.bindTileEntitySpecialRenderer(TileDankNullDock.class, new TESRDankNullDock());
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(ModBlocks.DANKNULL_DOCK), new DankNullDockRenderer());
    }
}
