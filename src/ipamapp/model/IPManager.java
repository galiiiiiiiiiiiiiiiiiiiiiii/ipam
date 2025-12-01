package ipamapp.model;

public class IPManager {
    private IPEntry[] entries;
    private int count;

    public IPManager(int capacity) {
        entries = new IPEntry[capacity];
        count = 0;
    }

    public boolean addEntry(IPEntry entry) {
        if (count >= entries.length) return false;
        entries[count++] = entry;
        return true;
    }

    public boolean removeAt(int index) {
        if (index < 0 || index >= count) return false;
        for (int i = index; i < count - 1; i++) {
            entries[i] = entries[i + 1];
        }
        entries[count - 1] = null;
        count--;
        return true;
    }

    public IPEntry get(int index) {
        if (index < 0 || index >= count) return null;
        return entries[index];
    }

    public IPEntry[] getAll() {
        IPEntry[] out = new IPEntry[count];
        for (int i = 0; i < count; i++) out[i] = entries[i];
        return out;
    }

    public int size() { return count; }

    public void clear() {
        for (int i = 0; i < count; i++) entries[i] = null;
        count = 0;
    }
}
