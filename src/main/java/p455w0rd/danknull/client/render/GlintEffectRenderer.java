package p455w0rd.danknull.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import p455w0rd.danknull.init.ModConfig;

/**
 * @author p455w0rd
 *
 */
public class GlintEffectRenderer {

    private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public static void renderModelGlint(int color, IModelCustom model, String part) {
        if (model == null) return;

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);

        GL11.glDepthFunc(GL11.GL_LEQUAL);

        GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
        GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
        GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(GLINT);

        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        if (ModConfig.Options.superShine) {
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        } else {
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        }

        render(model, part, 0.1F, 3000L, 8.0F, r, g, b, 0.5F);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private static void render(IModelCustom model, String part, float scale, long ms, float speed, float r, float g,
        float b, float alpha) {
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();

        GL11.glScalef(scale, scale, scale);
        float time = (float) (Minecraft.getSystemTime() % ms) / (float) ms;
        GL11.glTranslatef(time * speed, time * speed * 0.2F, 0.0F);
        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glColor4f(r, g, b, alpha);
        model.renderPart(part);

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
}
