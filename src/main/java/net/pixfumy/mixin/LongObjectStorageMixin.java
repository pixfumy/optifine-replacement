package net.pixfumy.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.util.collection.LongObjectStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LongObjectStorage.class)
public class LongObjectStorageMixin {
    @Unique
    private Long2ObjectLinkedOpenHashMap<Object> underLyingMap = new Long2ObjectLinkedOpenHashMap<>();

    @Overwrite
    public Object get(long key) {
        return underLyingMap.get(key);
    }

    @Overwrite
    public boolean contains(long key) {
        return underLyingMap.containsKey(key);
    }

    @Overwrite
    public void set(long key, Object value) {
        underLyingMap.put(key, value);
    }

    @Overwrite
    public Object remove(long key) {
        return underLyingMap.remove(key);
    }
}
