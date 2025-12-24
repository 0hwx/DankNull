package p455w0rd.danknull.integration;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.integration.waila.WAILADankNullDockProvider;

/**
 * @author p455w0rd
 */
public class WAILA {

    public static void init() {
        DankNull.LOGGER.info("Waila Integation: Enabled");
        FMLInterModComms.sendMessage(Mods.WAILA.modid, "register", WAILA.class.getName() + ".callbackRegister");
    }

    public static void callbackRegister(final IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new WAILADankNullDockProvider(), TileDankNullDock.class);
        registrar.registerStackProvider(new WAILADankNullDockProvider(), TileDankNullDock.class);
    }

}
