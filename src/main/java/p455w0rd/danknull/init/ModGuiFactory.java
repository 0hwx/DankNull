package p455w0rd.danknull.init;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import p455w0rd.danknull.DankNull;

/**
 * @author p455w0rd
 *
 */
public class ModGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(final Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ModGuiConfig.class;
    }

    @Nullable
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class ModGuiConfig extends GuiConfig {

        public ModGuiConfig(GuiScreen parent) {
            super(
                parent,
                ModConfig.getClientConfigElements(),
                DankNull.MODID,
                false,
                false,
                DankNull.NAME + " Config"
            );
        }
    }

}
