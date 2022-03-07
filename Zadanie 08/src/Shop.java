import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Shop implements ShopInterface {
    Map<String, Integer> inStock = new HashMap<>();
    final Map<String, Object> blockMap = new HashMap<>();

    @Override
    public void delivery( Map<String, Integer> goods ) {
        goods.keySet().stream().takeWhile(Objects::nonNull).forEach(keys -> {
            synchronized (blockMap) {
                if (!inStock.containsKey(keys)) {
                    inStock.put(keys, goods.get(keys));
                    if (!blockMap.containsKey(keys)) {
                        blockMap.put(keys, new Object());
                    }
                } else {
                    inStock.put(keys, goods.get(keys) + inStock.get(keys));
                }
            }
            synchronized (blockMap.get(keys)) {
                blockMap.get(keys).notifyAll();
            }
        });
    }

    @Override
    public boolean purchase( String productName, int quantity ) {
        blockMap.computeIfAbsent(productName, k -> new Object());
        synchronized (blockMap.get( productName ) ) {
            if (!inStock.containsKey(productName)) {
                try {
                    blockMap.get(productName).wait();
                } catch (InterruptedException ignored) {
                }

                if (inStock.containsKey(productName) && quantity <= inStock.get(productName)) {
                    inStock.put(productName, inStock.get(productName) - quantity);
                    return true;
                }
            } else {
                if (quantity <= inStock.get(productName)) {
                    inStock.put(productName, inStock.get(productName) - quantity);
                    return true;
                }
                try {
                    blockMap.get(productName).wait();
                } catch (InterruptedException ignored) {
                }

                if (inStock.containsKey(productName) && quantity <= inStock.get(productName)) {
                    inStock.put(productName, inStock.get(productName) - quantity);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Map<String, Integer> stock() {
        return inStock;
    }
}