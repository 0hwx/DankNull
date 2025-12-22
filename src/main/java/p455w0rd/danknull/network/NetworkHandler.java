package p455w0rd.danknull.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.network.packet.CChangeMode;
import p455w0rd.danknull.network.packet.COpenGui;
import p455w0rd.danknull.network.packet.SConfigSync;

public class NetworkHandler {

    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(DankNull.MODID);
    private static int packetId = 0;

    public static void init() {
        register(CChangeMode.class, Side.SERVER);
        register(COpenGui.class, Side.SERVER);

        register(SConfigSync.class, Side.CLIENT);
    }

    private static <T extends IPacket> void register(Class<T> clazz, Side side) {
        // We cast the first 'clazz' to a raw Class or a wildcard to bypass the strict generic check
        network.registerMessage((Class) clazz, clazz, packetId++, side);
    }

    public static void sendToServer(IPacket packet) {
        network.sendToServer(packet);
    }

    public static void sendToAll(IPacket packet) {
        network.sendToAll(packet);
    }

    public static void sendToAllAround(IPacket packet, NetworkRegistry.TargetPoint point) {
        network.sendToAllAround(packet, point);
    }

    public static void sendToWorld(IPacket packet, World world) {
        network.sendToDimension(packet, world.provider.dimensionId);
    }

    public static void sendToPlayer(IPacket packet, EntityPlayerMP player) {
        network.sendTo(packet, player);
    }
}
