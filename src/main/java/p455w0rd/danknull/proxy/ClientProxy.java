package p455w0rd.danknull.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import p455w0rd.danknull.client.KeyBindings;
import p455w0rd.danknull.init.ModCreativeTab;
import p455w0rd.danknull.init.ModIntegration;
import p455w0rd.danknull.init.ModItems;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(final FMLPreInitializationEvent e) {
        super.preInit(e);
        ModCreativeTab.init();
        KeyBindings.register();
    }

    @Override
    public void init(final FMLInitializationEvent e) {
        super.init(e);
        ModIntegration.init();
    }

    @Override
    public void postInit(final FMLPostInitializationEvent e) {
        super.postInit(e);
        ModItems.registerRenders();
    }

    @Override
    public void serverStarting(final FMLServerStartingEvent e) {
        super.serverStarting(e);
    }
}
