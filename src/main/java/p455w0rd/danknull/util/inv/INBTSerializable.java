package p455w0rd.danknull.util.inv;

import net.minecraft.nbt.NBTBase;

public interface INBTSerializable<T extends NBTBase> {

    T serializeNBT();

    void deserializeNBT(T nbt);
}
