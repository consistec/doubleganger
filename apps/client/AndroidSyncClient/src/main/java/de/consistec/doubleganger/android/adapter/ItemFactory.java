package de.consistec.doubleganger.android.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 25.03.13 15:52
 */
public final class ItemFactory {

    private ItemFactory() {

    }

    public static Item createDeletedItem() {
        Item deletedItem = new Item();
        deletedItem.setItemName("change deleted !!!");
        deletedItem.setItemValue("deleted");
        return deletedItem;
    }

    public static Item[] createChangeItems(final Map<String, Object> rowData) {
        List<Item> items = new ArrayList<Item>(rowData.size());
        boolean dataExists = false;
        for (String column : rowData.keySet()) {
            if (rowData.get(column) != null) {
                dataExists = true;
                Item item = new Item();
                item.setItemName(column);
                item.setItemDesc(rowData.get(column).toString());
                item.setItemValue(rowData.get(column));
                items.add(item);
            }
        }

        if (!dataExists) {
            items.clear();
        }
        return items.toArray(new Item[0]);
    }
}
