package p455w0rd.danknull.init;

import cpw.mods.fml.common.FMLCommonHandler;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.integration.Mods;
import p455w0rd.danknull.integration.WAILA;

/**
 * @author p455w0rd
 *
 */
public class ModIntegration {

    public static void init() {
        if (FMLCommonHandler.instance()
            .getSide()
            .isClient()) {
            if (Mods.WAILA.isLoaded()) {
                WAILA.init();
            } else {
                DankNull.LOGGER.info("Waila Integation: Disabled");
            }
        }
    }
}
