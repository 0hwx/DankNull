package p455w0rd.danknull.client.render.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DankEntityItem extends EntityItem {

    public DankEntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        this.hoverStart = 0.0F;
    }
}
