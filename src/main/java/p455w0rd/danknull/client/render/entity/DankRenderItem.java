package p455w0rd.danknull.client.render.entity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class DankRenderItem extends RenderItem {

    public static DankRenderItem INSTANCE = new DankRenderItem();

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(
        "textures/misc/enchanted_item_glint.png");
    private Random random = new Random();
    private RenderBlocks renderBlocksRi = new RenderBlocks();

    public DankRenderItem() {
        this.setRenderManager(RenderManager.instance);
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float yaw, float partialTicks) {
        ItemStack stack = entity.getEntityItem();
        if (stack == null || stack.getItem() == null) return;
        if (renderManager.renderEngine == null) return;

        this.bindEntityTexture(entity);
        TextureUtil.func_152777_a(false, false, 1.0F);
        this.random.setSeed(187L);

        GL11.glPushMatrix();

        float bob = shouldBob()
            ? MathHelper.sin(((float) entity.age + partialTicks) / 10.0F + entity.hoverStart) * 0.1F + 0.1F
            : 0F;

        float rotation = (((float) entity.age + partialTicks) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI);

        byte miniCount = getMiniBlockCount(stack);

        GL11.glTranslatef((float) x, (float) y + bob, (float) z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        boolean rendered = ForgeHooksClient.renderEntityItem(
            entity,
            stack,
            bob,
            rotation,
            random,
            renderManager.renderEngine,
            field_147909_c,
            miniCount);

        if (!rendered) {
            renderItemInternal(entity, stack, rotation, partialTicks, miniCount);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        this.bindEntityTexture(entity);
        TextureUtil.func_147945_b();
    }

    private void renderItemInternal(EntityItem entity, ItemStack stack, float rotation, float partialTicks,
        byte miniCount) {

        if (stack.getItemSpriteNumber() == 0 && stack.getItem() instanceof ItemBlock
            && RenderBlocks.renderItemIn3d(
                Block.getBlockFromItem(stack.getItem())
                    .getRenderType())) {

            renderBlockItem(stack, rotation, miniCount);
            return;
        }

        renderFlatItem(entity, stack, partialTicks, miniCount);
    }

    private void renderBlockItem(ItemStack stack, float rotation, byte miniCount) {
        Block block = Block.getBlockFromItem(stack.getItem());
        GL11.glRotatef(rotation, 0F, 1F, 0F);

        if (renderInFrame) {
            GL11.glScalef(1.25F, 1.25F, 1.25F);
            // GL11.glTranslatef(0F, 0.05F, 0F);
            GL11.glRotatef(-90F, 0F, 1F, 0F);
        }

        float scale = (block.getRenderType() == 1 || block.getRenderType() == 2
            || block.getRenderType() == 12
            || block.getRenderType() == 19) ? 0.5F : 0.375F;

        if (block.getRenderBlockPass() > 0) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        GL11.glScalef(scale, scale, scale);

        for (int i = 0; i < miniCount; i++) {
            GL11.glPushMatrix();
            if (i > 0) {
                GL11.glTranslatef(
                    (random.nextFloat() * 2 - 1) * 0.2F / scale,
                    (random.nextFloat() * 2 - 1) * 0.2F / scale,
                    (random.nextFloat() * 2 - 1) * 0.2F / scale);
            }

            renderBlocksRi.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);
            GL11.glPopMatrix();
        }

        if (block.getRenderBlockPass() > 0) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void renderFlatItem(EntityItem entity, ItemStack stack, float partialTicks, byte miniCount) {

        if (stack.getItem()
            .requiresMultipleRenderPasses()) {

            if (renderInFrame) {
                GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                // GL11.glTranslatef(0.0F, -0.05F, 0.0F);
            } else {
                GL11.glScalef(0.5F, 0.5F, 0.5F);
            }

            for (int pass = 0; pass < stack.getItem()
                .getRenderPasses(stack.getItemDamage()); pass++) {
                this.random.setSeed(187L);
                IIcon icon = stack.getItem()
                    .getIcon(stack, pass);
                int color = renderWithColor ? stack.getItem()
                    .getColorFromItemStack(stack, pass) : 0xFFFFFF;

                render2DItemAs3D(
                    entity,
                    icon,
                    miniCount,
                    partialTicks,
                    ((color >> 16) & 255) / 255F,
                    ((color >> 8) & 255) / 255F,
                    (color & 255) / 255F,
                    pass);
            }
            return;
        }

        if (stack != null && stack.getItem() instanceof ItemCloth) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        if (renderInFrame) {
            GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
            // GL11.glTranslatef(0.0F, -0.05F, 0.0F);
        } else {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
        }

        IIcon icon = stack.getIconIndex();
        int color = renderWithColor ? stack.getItem()
            .getColorFromItemStack(stack, 0) : 0xFFFFFF;

        render2DItemAs3D(
            entity,
            icon,
            miniCount,
            partialTicks,
            ((color >> 16) & 255) / 255F,
            ((color >> 8) & 255) / 255F,
            (color & 255) / 255F,
            0);

        if (stack != null && stack.getItem() instanceof ItemCloth) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    /**
     * Vanilla name: renderDroppedItem
     */
    private void render2DItemAs3D(EntityItem entity, IIcon icon, int renderCount, float partialTicks, float red,
        float green, float blue, int pass) {
        Tessellator tessellator = Tessellator.instance;

        if (icon == null) {
            TextureManager textureManager = Minecraft.getMinecraft()
                .getTextureManager();
            ResourceLocation atlas = textureManager.getResourceLocation(
                entity.getEntityItem()
                    .getItemSpriteNumber());
            icon = ((TextureMap) textureManager.getTexture(atlas)).getAtlasSprite("missingno");
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        final float halfW = 0.45F;
        final float halfH = 0.45F; // Changed from 0.25F to 0.45F for true center

        if (renderManager.options.fancyGraphics) {
            GL11.glPushMatrix();
            if (renderInFrame) {
                GL11.glRotatef(180.0F, 0F, 1F, 0F);
            } else {
                float rotation = (((float) entity.age + partialTicks) / 20.0F + entity.hoverStart)
                    * (180F / (float) Math.PI);
                GL11.glRotatef(rotation, 0F, 1F, 0F);
            }

            final float thickness = 0.0625F;
            final float zOffset = 0.021875F;

            ItemStack stack = entity.getEntityItem();
            byte miniCount = getMiniItemCount(stack);

            float totalStackDepth = (thickness + zOffset) * miniCount;
            GL11.glTranslatef(-halfW, -halfH, -(totalStackDepth / 2.0F));

            for (int i = 0; i < miniCount; i++) {

                if (i > 0 && shouldSpreadItems()) {
                    GL11.glTranslatef(
                        (random.nextFloat() * 2 - 1) * 0.3F / 0.5F,
                        (random.nextFloat() * 2 - 1) * 0.3F / 0.5F,
                        thickness + zOffset);
                } else {
                    GL11.glTranslatef(0F, 0F, thickness + zOffset);
                }

                bindTexture(
                    stack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture
                        : TextureMap.locationItemsTexture);

                GL11.glColor4f(red, green, blue, 1.0F);

                ItemRenderer.renderItemIn2D(
                    tessellator,
                    maxU,
                    minV,
                    minU,
                    maxV,
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    thickness);

                renderEnchantmentGlint(stack, pass, thickness);
            }

            GL11.glPopMatrix();
            return;
        }

        for (int i = 0; i < renderCount; i++) {
            GL11.glPushMatrix();

            if (i > 0) {
                GL11.glTranslatef(
                    (random.nextFloat() * 2 - 1) * 0.3F,
                    (random.nextFloat() * 2 - 1) * 0.3F,
                    (random.nextFloat() * 2 - 1) * 0.3F);
            }

            if (!renderInFrame) {
                GL11.glRotatef(180.0F - renderManager.playerViewY, 0F, 1F, 0F);
            }

            GL11.glColor4f(red, green, blue, 1.0F);

            // --- DRAW FRONT FACE ---
            tessellator.startDrawingQuads();
            tessellator.setNormal(0F, 0F, 1F); // Normal pointing forward
            tessellator.addVertexWithUV(-halfW, -halfH, 0.0D, minU, maxV);
            tessellator.addVertexWithUV(halfW, -halfH, 0.0D, maxU, maxV);
            tessellator.addVertexWithUV(halfW, halfH, 0.0D, maxU, minV);
            tessellator.addVertexWithUV(-halfW, halfH, 0.0D, minU, minV);
            tessellator.draw();

            // --- DRAW BACK FACE ---
            // We reverse the vertex order (Counter-Clockwise) so it is visible from behind
            tessellator.startDrawingQuads();
            tessellator.setNormal(0F, 0F, -1F); // Normal pointing backward
            tessellator.addVertexWithUV(-halfW, halfH, 0.0D, minU, minV);
            tessellator.addVertexWithUV(halfW, halfH, 0.0D, maxU, minV);
            tessellator.addVertexWithUV(halfW, -halfH, 0.0D, maxU, maxV);
            tessellator.addVertexWithUV(-halfW, -halfH, 0.0D, minU, maxV);
            tessellator.draw();

            GL11.glPopMatrix();
        }
    }

    private void renderEnchantmentGlint(ItemStack stack, int pass, float thickness) {
        if (!stack.hasEffect(pass)) return;

        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        renderManager.renderEngine.bindTexture(RES_ITEM_GLINT);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);

        float intensity = 0.76F;
        GL11.glColor4f(0.5F * intensity, 0.25F * intensity, 0.8F * intensity, 1.0F);

        GL11.glMatrixMode(GL11.GL_TEXTURE);

        renderGlintLayer(thickness, 3000L, 8.0F, -50.0F);
        renderGlintLayer(thickness, 4873L, -8.0F, 10.0F);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    private void renderGlintLayer(float thickness, long timeMod, float scroll, float rotation) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glPushMatrix();
        float scale = 0.125F;
        GL11.glScalef(scale, scale, scale);

        float offset = (Minecraft.getSystemTime() % timeMod) / (float) timeMod * scroll;
        GL11.glTranslatef(offset, 0F, 0F);
        GL11.glRotatef(rotation, 0F, 0F, 1F);

        ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, thickness);

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        if (renderManager.renderEngine == null) return null;
        return renderManager.renderEngine.getResourceLocation(
            entity.getEntityItem()
                .getItemSpriteNumber());
    }

    @Override
    public boolean shouldSpreadItems() {
        return true;
    }

    @Override
    public boolean shouldBob() {
        return true;
    }

    public byte getMiniBlockCount(ItemStack stack) {
        byte ret = 1;
        if (stack.stackSize > 1) ret = 2;
        if (stack.stackSize > 5) ret = 3;
        if (stack.stackSize > 20) ret = 4;
        if (stack.stackSize > 40) ret = 5;
        return ret;
    }

    /**
     * Allows for a subclass to override how many rendered items appear in a "mini item 3d stack"
     *
     * @param stack
     * @return
     */
    public byte getMiniItemCount(ItemStack stack) {
        byte ret = 1;
        if (stack.stackSize > 1) ret = 2;
        if (stack.stackSize > 15) ret = 3;
        if (stack.stackSize > 31) ret = 4;
        return ret;
    }
}
