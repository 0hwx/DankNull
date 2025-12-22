package p455w0rd.danknull.client.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import p455w0rd.danknull.blocks.tiles.TileDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class TESRDankNullDock extends TileEntitySpecialRenderer {

    private final IModelCustom dankModel = AdvancedModelLoader
        .loadModel(new ResourceLocation("danknull", "models/block/danknull_dock.obj"));;
    private final ResourceLocation frameTexture = new ResourceLocation("danknull", "textures/items/danknull/frame.png");
    private final ResourceLocation baseTexture = new ResourceLocation("danknull", "textures/blocks/dock/base.png");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partial) {
        if (tile != null && tile instanceof TileDankNullDock tileDankNullDock) {

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y, z + 0.5);
            this.bindTexture(baseTexture);
            dankModel.renderPart("Base");

            this.bindTexture(frameTexture);
            dankModel.renderPart("BaseFrame");

            ItemStack stack = tileDankNullDock.getDankNull();
            if (stack != null) {
                GL11.glTranslatef(0.0f, 0.15f, 0.0f);
                IItemRenderer renderer = MinecraftForgeClient
                    .getItemRenderer(stack, IItemRenderer.ItemRenderType.ENTITY);
                if (renderer != null) {
                    renderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, stack);
                }

                // EntityItem entity = new EntityItem(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, stack);
                // entity.hoverStart = 0;
                // GL11.glScaled(2d, 2d, 2d);
                // RenderManager.instance.renderEntityWithPosYaw(entity, 0, 0, 0, 0, 0);

            }
            GL11.glPopMatrix();
        }

    }
}
