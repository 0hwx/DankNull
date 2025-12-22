package p455w0rd.danknull.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import p455w0rd.danknull.blocks.BlockDankNullDock;
import p455w0rd.danknull.items.ItemBlockDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class ModBlocks {

    public static BlockDankNullDock DANKNULL_DOCK;

    public static void init() {
        DANKNULL_DOCK = new BlockDankNullDock();
        registerBlock(DANKNULL_DOCK, ItemBlockDankNullDock.class);
    }

    private static void registerBlock(Block block, Class<? extends ItemBlock> itemClass) {
        String name = block.getUnlocalizedName()
            .substring(5);

        GameRegistry.registerBlock(block, itemClass, name);
    }

}
