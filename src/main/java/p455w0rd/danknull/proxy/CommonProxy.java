package p455w0rd.danknull.proxy;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.network.NetworkHandler;

public class CommonProxy {

    public void preInit(final FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ModEvents());
        FMLCommonHandler.instance()
            .bus()
            .register(new ModEvents());
        NetworkHandler.init();
        ModConfig.load();
    }

    public void init(final FMLInitializationEvent e) {
        ModItems.init();
        ModBlocks.init();
        ModRecipes.init();
        GameRegistry.registerTileEntity(TileDankNullDock.class, "TileDankNullDock");

    }

    public void postInit(final FMLPostInitializationEvent e) {
        ModGuiHandler.init();
    }

    public void serverStarting(final FMLServerStartingEvent e) {

    }
}
