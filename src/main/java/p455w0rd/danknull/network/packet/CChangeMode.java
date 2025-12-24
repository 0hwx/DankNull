package p455w0rd.danknull.network.packet;

import static p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import static p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.inventory.container.ContainerDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.IPacket;
import p455w0rd.danknull.network.NetworkUtils;

public class CChangeMode implements IPacket {

    private ChangeType changeType;
    private int slot = -1;
    private boolean isGui = true;

    public CChangeMode() {}

    public CChangeMode(final ChangeType changeType) {
        this.changeType = changeType;
    }

    public CChangeMode(final ItemPlacementMode mode, final int slot) {
        switch (mode) {
            case KEEP_NONE:
                changeType = ChangeType.PLACE_KEEP_NONE;
                break;
            case KEEP_1:
                changeType = ChangeType.PLACE_KEEP_1;
                break;
            case KEEP_16:
                changeType = ChangeType.PLACE_KEEP_16;
                break;
            case KEEP_64:
                changeType = ChangeType.PLACE_KEEP_64;
                break;
            case KEEP_ALL:
                changeType = ChangeType.PLACE_KEEP_ALL;
                break;
            default:
                throw new RuntimeException("Unknown ItemPlacementMode " + mode.name());
        }
        this.slot = slot;
    }

    public CChangeMode(final ItemExtractionMode mode, final int slot) {
        switch (mode) {
            case KEEP_NONE:
                changeType = ChangeType.EXTRACT_KEEP_NONE;
                break;
            case KEEP_1:
                changeType = ChangeType.EXTRACT_KEEP_1;
                break;
            case KEEP_16:
                changeType = ChangeType.EXTRACT_KEEP_16;
                break;
            case KEEP_64:
                changeType = ChangeType.EXTRACT_KEEP_64;
                break;
            case KEEP_ALL:
                changeType = ChangeType.EXTRACT_KEEP_ALL;
                break;
            default:
                throw new RuntimeException("Unknown ItemExtractionMode " + mode.name());
        }
        this.slot = slot;
    }

    public CChangeMode(final ChangeType type, final int slot) {
        this(type, slot, true);
    }

    public CChangeMode(final ChangeType type, final int slot, final boolean isGui) {
        changeType = type;
        this.slot = slot;
        this.isGui = isGui;
    }

    @Override
    public void encode(PacketBuffer buf) {
        NetworkUtils.writeEnumValue(buf, changeType);
        buf.writeInt(slot);
        buf.writeBoolean(isGui);
    }

    @Override
    public void decode(PacketBuffer buf) {
        changeType = NetworkUtils.readEnumValue(buf, ChangeType.class);
        slot = buf.readInt();
        isGui = buf.readBoolean();
    }

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {

        if (isGui) {
            final Container container = handler.playerEntity.openContainer;
            if (container instanceof ContainerDankNull containerDankNull) {
                handleModeUpdate(containerDankNull.getHandler(), changeType, slot);
            }
        } else {
            final ItemStack stack = handler.playerEntity.getHeldItem();
            if (stack != null && stack.getItem() instanceof ItemDankNull) {
                DankNullHandler nullHandler = DankNullHandler.fromStack(handler.playerEntity.worldObj, stack);
                handleModeUpdate(nullHandler, changeType, slot);
            }
        }

        return null;
    }

    private static void handleModeUpdate(final DankNullHandler handler, final ChangeType changeType, final int slot) {
        switch (changeType) {
            case SELECTED:
                handler.setSelected(slot);
                break;

            case LOCK:
                handler.setLocked(true);
                break;
            case UNLOCK:
                handler.setLocked(false);
                break;

            case ORE_ON:
                handler.setOre(slot, true);
                break;
            case ORE_OFF:
                handler.setOre(slot, false);
                break;

            case EXTRACT_KEEP_ALL:
                handler.setExtractionMode(slot, ItemExtractionMode.KEEP_ALL);
                break;
            case EXTRACT_KEEP_1:
                handler.setExtractionMode(slot, ItemExtractionMode.KEEP_1);
                break;
            case EXTRACT_KEEP_16:
                handler.setExtractionMode(slot, ItemExtractionMode.KEEP_16);
                break;
            case EXTRACT_KEEP_64:
                handler.setExtractionMode(slot, ItemExtractionMode.KEEP_64);
                break;
            case EXTRACT_KEEP_NONE:
                handler.setExtractionMode(slot, ItemExtractionMode.KEEP_NONE);
                break;

            case PLACE_KEEP_ALL:
                handler.setPlacementMode(slot, ItemPlacementMode.KEEP_ALL);
                break;
            case PLACE_KEEP_1:
                handler.setPlacementMode(slot, ItemPlacementMode.KEEP_1);
                break;
            case PLACE_KEEP_16:
                handler.setPlacementMode(slot, ItemPlacementMode.KEEP_16);
                break;
            case PLACE_KEEP_64:
                handler.setPlacementMode(slot, ItemPlacementMode.KEEP_64);
                break;
            case PLACE_KEEP_NONE:
                handler.setPlacementMode(slot, ItemPlacementMode.KEEP_NONE);
                break;
        }
    }

    public enum ChangeType {

        LOCK,
        UNLOCK,
        SELECTED,
        ORE_ON,
        ORE_OFF,
        EXTRACT_KEEP_ALL,
        EXTRACT_KEEP_1,
        EXTRACT_KEEP_16,
        EXTRACT_KEEP_64,
        EXTRACT_KEEP_NONE,
        PLACE_KEEP_ALL,
        PLACE_KEEP_1,
        PLACE_KEEP_16,
        PLACE_KEEP_64,
        PLACE_KEEP_NONE;

        public static ChangeType[] VALUES = values(); // Learned this from McJty..if your Enum won't be modified later,
                                                      // cache the values

    }
}
