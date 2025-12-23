package p455w0rd.danknull.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.client.render.entity.DankEntityItem;
import p455w0rd.danknull.client.render.entity.DankRenderItem;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullPanel;
import p455w0rd.danknull.util.DankUtils;

public class DankNullRenderer implements IItemRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final DankNullTier tier;
    private final IModelCustom dankModel = AdvancedModelLoader
        .loadModel(new ResourceLocation("danknull", "models/item/dank_null.obj"));
    private final ResourceLocation frameTexture = new ResourceLocation("danknull", "textures/items/danknull/frame.png");
    private final ResourceLocation glassTexture;
    private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public DankNullRenderer(DankNullTier tier) {
        this.tier = tier;
        this.glassTexture = new ResourceLocation(
            "danknull",
            "textures/items/danknull/glass_" + tier.name()
                .toLowerCase() + ".png");

    }

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
        if (item == null || !ItemDankNull.isDankNull(item)) return;

        int selectedSlot = DankUtils.getSelectedSlot(item);
        ItemStack containedStack = DankUtils.getStackInSlot(item, selectedSlot);

        GL11.glPushMatrix();
        // Position based on render type
        switch (type) {
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.5f, 0, 0.5F);
                break;
            case ENTITY:
                GL11.glScaled(0.5F, 0.5F, 0.5F);
                break;
            case INVENTORY:
                GL11.glTranslatef(0F, -0.5F, 0F);
                break;
            default:
                break;
        }

        if (RenderItem.renderInFrame) {
            GL11.glTranslatef(0.0F, -0.51F, 0.0F);
        }

        // Render frame
        mc.renderEngine.bindTexture(frameTexture);
        dankModel.renderPart("Frame");

        if (containedStack != null) {
            renderContainedItem(containedStack, type);
        }

        // Render glass with transparency
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        mc.renderEngine.bindTexture(glassTexture);
        dankModel.renderPart("Glass");
        GL11.glDisable(GL11.GL_BLEND);

        if (item.hasEffect()) {
            GlintEffectRenderer.renderModelGlint(tier.getGlintcolor(), dankModel, "Frame");
            GlintEffectRenderer.renderModelGlint(tier.getGlintcolor(), dankModel, "Glass");
        }

        GL11.glPopMatrix();
    }

    private void renderContainedItem(ItemStack stack, ItemRenderType type) {
        if (stack == null || stack.getItem() == null) return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        ItemStack renderStack = stack.copy();
        renderStack.stackSize = 1;

        float Yoffset = 0.8f;
        float Xoffset = 0;

        if (RenderItem.renderInFrame) {
            Yoffset = 0.5f;
            Xoffset = 0.075f;
        }

        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            Yoffset -= 0.3f;
        }

        GL11.glTranslatef(Xoffset, Yoffset, 0.0F);

        float rotation = (System.currentTimeMillis() % 36000L) / 50F;

        if (renderStack.getItem() instanceof ItemDankNullPanel || renderStack.getItem() == Item.getItemFromBlock(ModBlocks.DANKNULL_DOCK)) {
            int scale = 2;
            GL11.glTranslatef(0, -0.25f, 0.0F);
            GL11.glScaled(scale,scale,scale);
            GL11.glRotatef(rotation, 0F, 1F, 0F);

        } else {
            GL11.glRotatef(rotation, 1F, 1F, 1F);
        }

        DankEntityItem entityItem = new DankEntityItem(mc.theWorld, 0, 0, 0, renderStack);
        // the -0.1D is to center the block for rotation
        DankRenderItem.INSTANCE.doRender(entityItem, 0D, -0.1D, 0D, 0, 0);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
