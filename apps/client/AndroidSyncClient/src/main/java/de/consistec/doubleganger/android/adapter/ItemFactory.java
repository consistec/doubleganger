package de.consistec.doubleganger.android.adapter;

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
}
