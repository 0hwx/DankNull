package p455w0rd.danknull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import p455w0rd.danknull.proxy.CommonProxy;

@Mod(
    modid = DankNull.MODID,
    name = DankNull.NAME,
    version = DankNull.VERSION,
    guiFactory = DankNull.GUI_FACTORY,
    acceptedMinecraftVersions = "[1.7.10]",
    certificateFingerprint = "@FINGERPRINT@")
public class DankNull {

    public static final String MODID = "danknull";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "/dank/null";
    public static final String SERVER_PROXY = "p455w0rd.danknull.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "p455w0rd.danknull.proxy.ClientProxy";
    public static final String GUI_FACTORY = "p455w0rd.danknull.init.ModGuiFactory";

    @SidedProxy(clientSide = DankNull.CLIENT_PROXY, serverSide = DankNull.SERVER_PROXY)
    public static CommonProxy PROXY;

    @Mod.Instance(DankNull.MODID)
    public static DankNull INSTANCE;

    public static Logger LOGGER = LogManager.getLogger(DankNull.NAME);

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        PROXY.preInit(event);
        // OreDictionary.registerOre("railBed", new ItemStack(Blocks.LOG, 1, OreDictionary.WILDCARD_VALUE));
        // OreDictionary.registerOre("railBed", new ItemStack(Blocks.BEDROCK));
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(final FMLServerStartingEvent event) {
        PROXY.serverStarting(event);
    }

}
