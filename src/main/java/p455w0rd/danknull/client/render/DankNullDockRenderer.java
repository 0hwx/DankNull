package p455w0rd.danknull.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

public class DankNullDockRenderer implements IItemRenderer {

    private final IModelCustom dankModel = AdvancedModelLoader
        .loadModel(new ResourceLocation("danknull", "models/block/danknull_dock.obj"));;
    private final ResourceLocation frameTexture = new ResourceLocation("danknull", "textures/items/danknull/frame.png");
    private final ResourceLocation baseTexture = new ResourceLocation("danknull", "textures/blocks/dock/base.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();

        applyTransforms(type);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(baseTexture);
        dankModel.renderPart("Base");
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(frameTexture);
        dankModel.renderPart("BaseFrame");

        ItemStack innerDank = getDank(item);

        if (innerDank != null) {
            renderNestedItem(innerDank);
        }

        GL11.glPopMatrix();
    }

    private void renderNestedItem(ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.15f, 0.0f);

        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, ItemRenderType.ENTITY);
        if (renderer != null) {
            renderer.renderItem(ItemRenderType.ENTITY, stack);
        }
        GL11.glPopMatrix();
    }

    private ItemStack getDank(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("BlockEntityTag")) {
            NBTTagCompound nbt = stack.getTagCompound()
                .getCompoundTag("BlockEntityTag");
            if (nbt.hasKey("Inventory")) {
                NBTTagCompound invTag = nbt.getCompoundTag("Inventory");
                // Check for the first slot (index 0)
                NBTTagList list = invTag.getTagList("Items", 10);
                if (list.tagCount() > 0) {
                    ItemStack innerDank = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(0));
                    return innerDank;
                }
            }
        }
        return null;
    }

    private void applyTransforms(ItemRenderType type) {
        switch (type) {
            case EQUIPPED:
                GL11.glTranslatef(0.5f, 0.4f, 0.5f);
                GL11.glScalef(1.2f, 1.2f, 1.2f);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.5f, 0.5f, 0.5f);
                break;
            case INVENTORY:
                GL11.glTranslatef(0f, -0.4f, 0f);
                break;
            default:
                break;
        }
    }

}
