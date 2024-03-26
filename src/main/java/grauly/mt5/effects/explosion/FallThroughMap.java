package grauly.mt5.effects.explosion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class FallThroughMap<T> {
    protected ArrayList<StorageElement<T>> internalStorage = new ArrayList<>();
    protected T defaultElement;

    public T get(int value) {
        for (StorageElement<T> element : internalStorage) {
            if(value >= element.key) return element.value;
        }
        return defaultElement;
    }

    public void add(int value, T object) {
        for (StorageElement<T> element : internalStorage) {
            if(element.key == value) throw new IllegalArgumentException("Attempting to add element with already existing index.");
        }
        internalStorage.add(new StorageElement<>(value, object));
        Collections.sort(internalStorage);
        Collections.reverse(internalStorage);
    }

    public boolean hasDefaultElement() {
        return defaultElement != null;
    }

    public void setDefaultElement(T defaultElement) {
        this.defaultElement = defaultElement;
    }
    protected record StorageElement<T>(int key, T value) implements Comparable<StorageElement<T>> {
        @Override
        public int compareTo(@NotNull FallThroughMap.StorageElement<T> o) {
            return Integer.compare(key, o.key);
        }
    }
}

