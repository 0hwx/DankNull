package p455w0rd.danknull.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.network.IPacket;
import p455w0rd.danknull.network.NetworkUtils;

// this is used to open the gui server side to sync the Inventory
public class COpenGui implements IPacket {

    private ModGuiHandler.GUIType guiType;
    int x, y, z;

    public COpenGui() {}

    public COpenGui(ModGuiHandler.GUIType guiType, int x, int y, int z) {
        this.guiType = guiType;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void encode(PacketBuffer buf) {
        NetworkUtils.writeEnumValue(buf, guiType);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    @Override
    public void decode(PacketBuffer buf) {
        guiType = NetworkUtils.readEnumValue(buf, ModGuiHandler.GUIType.class);
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

    }

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {
        EntityPlayer player = handler.playerEntity;
        World world = player.worldObj;
        FMLNetworkHandler.openGui(player, DankNull.INSTANCE, guiType.ordinal(), world, x, y, z);

        return null;
    }
}
