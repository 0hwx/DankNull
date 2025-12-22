package p455w0rd.danknull.client;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

/**
 * @author p455w0rd
 *
 */
public class KeyBindings {

    private static final String CATEGORY = "key.categories.danknull";
    public static KeyBinding nextItem = new KeyBinding("key.next_item.desc", Keyboard.CHAR_NONE, CATEGORY);
    public static KeyBinding previousItem = new KeyBinding("key.previous_item.desc", Keyboard.CHAR_NONE, CATEGORY);
    public static KeyBinding openDankNull = new KeyBinding("key.open_danknull.desc", Keyboard.CHAR_NONE, CATEGORY);
    public static KeyBinding toggleHUDOverlay = new KeyBinding("key.togglehud.desc", Keyboard.CHAR_NONE, CATEGORY);

    public static void register() {
        ClientRegistry.registerKeyBinding(nextItem);
        ClientRegistry.registerKeyBinding(previousItem);
        ClientRegistry.registerKeyBinding(openDankNull);
        ClientRegistry.registerKeyBinding(toggleHUDOverlay);
    }

    public static boolean isAnyModKeybindPressed() {
        return nextItem.isPressed() || //@formatter:off
               previousItem.isPressed() ||
               openDankNull.isPressed() ||
               toggleHUDOverlay.isPressed();//@formatter:on
    }

}
