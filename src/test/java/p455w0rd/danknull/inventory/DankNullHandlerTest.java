package p455w0rd.danknull.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;

public class DankNullHandlerTest {

    private DankNullHandler dankNull;
    private ItemStack dummyDank;
    private World mockWorld;

    @BeforeEach
    public void setup() {
        mockWorld = Mockito.mock(World.class);
        mockWorld.isRemote = false; // Ensure we are on "server side" for saving logic

        dummyDank = new ItemStack(new Item(), 1);

        dankNull = new DankNullHandler(mockWorld, dummyDank, null);
    }

    @Test
    public void testInsertionAndExtraction() {
        ItemStack stack = new ItemStack(new Item(), 64);

        // Set mode to KEEP_NONE (Extract everything)
        dankNull.setExtractionMode(0, ItemExtractionMode.KEEP_NONE);

        // Test Insertion (insertItem returns the remainder)
        ItemStack remainder = dankNull.insertItem(0, stack.copy(), false);
        assertNull(remainder, "Stack should be fully inserted");

        assertEquals(64, dankNull.getStackInSlot(0).stackSize);

        // Test Extraction
        ItemStack extracted = dankNull.extractItem(0, 32, false);
        assertNotNull(extracted);
        assertEquals(32, extracted.stackSize);
        assertEquals(32, dankNull.getStackInSlot(0).stackSize);
    }

    @Test
    public void testKeepModeExtraction() {
        ItemStack stack = new ItemStack(new Item(), 64);

        // Set mode to KEEP_16
        dankNull.setExtractionMode(0, ItemExtractionMode.KEEP_16);
        dankNull.insertItem(0, stack, false);

        // Try to extract all 64. Logic should only allow 48 (64 - 16 = 48)
        ItemStack extracted = dankNull.extractItem(0, 64, false);

        assertNotNull(extracted);
        assertEquals(48, extracted.stackSize);
        assertEquals(16, dankNull.getStackInSlot(0).stackSize);
    }
}
