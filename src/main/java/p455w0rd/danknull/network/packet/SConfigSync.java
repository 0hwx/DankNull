package p455w0rd.danknull.network.packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;

import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.network.IPacket;

public class SConfigSync implements IPacket {

    public Map<String, Object> values = new HashMap<>();

    public SConfigSync() {}

    @Override
    public void encode(PacketBuffer buf) throws IOException {
        buf.writeStringToBuffer(ModConfig.Options.creativeBlacklist);
        buf.writeStringToBuffer(ModConfig.Options.creativeWhitelist);
        buf.writeStringToBuffer(ModConfig.Options.oreBlacklist);
        buf.writeStringToBuffer(ModConfig.Options.oreWhitelist);
        buf.writeBoolean(ModConfig.Options.disableOreDictMode);
    }

    @Override
    public void decode(PacketBuffer buf) throws IOException {
        values.put(ModConfig.NAME_CREATIVE_BLACKLIST, buf.readStringFromBuffer(Short.MAX_VALUE));
        values.put(ModConfig.NAME_CREATIVE_WHITELIST, buf.readStringFromBuffer(Short.MAX_VALUE));
        values.put(ModConfig.NAME_OREDICT_BLACKLIST, buf.readStringFromBuffer(Short.MAX_VALUE));
        values.put(ModConfig.NAME_OREDICT_WHITELIST, buf.readStringFromBuffer(Short.MAX_VALUE));
        values.put(ModConfig.NAME_DISABLE_OREDICT, buf.readBoolean());
    }

    @Override
    public IPacket executeClient(NetHandlerPlayClient handler) {
        ModConfig.Options.creativeBlacklist = (String) this.values.getOrDefault(ModConfig.NAME_CREATIVE_BLACKLIST, "");
        ModConfig.Options.creativeWhitelist = (String) this.values.getOrDefault(ModConfig.NAME_CREATIVE_WHITELIST, "");
        ModConfig.Options.oreBlacklist = (String) this.values.getOrDefault(ModConfig.NAME_OREDICT_BLACKLIST, "");
        ModConfig.Options.oreWhitelist = (String) this.values.getOrDefault(ModConfig.NAME_OREDICT_WHITELIST, "");
        ModConfig.Options.disableOreDictMode = (Boolean) this.values
            .getOrDefault(ModConfig.NAME_DISABLE_OREDICT, false);
        return null;
    }
}
