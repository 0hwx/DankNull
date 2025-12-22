package p455w0rd.danknull.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

/**
 * @author p455w0rd
 *
 */
public class GlintEffectRenderer {

    private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public static void renderModelGlint(int color, IModelCustom modelCustom, String part) {
        if (modelCustom == null) return;

        GL11.glPushMatrix();

        // SETUP SMOOTH STATES
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        // ENABLE AUTOMATIC TEXTURE GENERATION
        // This ignores the model's UVs and projects the glint in 3D space
        GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
        GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
        GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(GLINT);

        // ANIMATION
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glScalef(0.1F, 0.1F, 0.1F); // Projector scale
        float time = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F;
        GL11.glTranslatef(time * 8.0F, time * 1.0F, 0.0F);
        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);

        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 0.5F);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Instead of rendering part-by-part, we render the parts we want to glow
        // while the PROJECTOR is on.
        modelCustom.renderPart(part);

        // CLEANUP (Disable TexGen so the rest of the world isn't broken)
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }
}
