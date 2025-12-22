package p455w0rd.danknull.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import p455w0rd.danknull.api.DankNullTier;

public class DankNullPanelRenderer implements IItemRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final DankNullTier tier;
    private final IModelCustom dankModel = AdvancedModelLoader
        .loadModel(new ResourceLocation("danknull", "models/item/dank_null_panel.obj"));
    private final ResourceLocation frameTexture = new ResourceLocation("danknull", "textures/items/danknull/frame.png");
    private final ResourceLocation glassTexture;

    public DankNullPanelRenderer(DankNullTier tier) {
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
        GL11.glPushMatrix();
        switch (type) {
            case EQUIPPED:
                GL11.glTranslatef(1f, 0.5f, 0f);
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glRotatef(90, 1, 0, 1);
                GL11.glRotatef(100, 0, 1, 0);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glRotatef(-4, 1, 1, 0);
                GL11.glTranslatef(-0.5f, 0.5f, 0.5F);
                break;
            case ENTITY:
                GL11.glScaled(0.5F, 0.5F, 0.5F);
                break;
            case INVENTORY:
                GL11.glScalef(1.2f, 1.2f, 1.2f);
                GL11.glTranslatef(0F, -0.5F, 0F);
                break;
            default:
                break;
        }

        // Render frame
        mc.renderEngine.bindTexture(frameTexture);
        dankModel.renderPart("Frame");

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
}
