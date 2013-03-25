package de.consistec.doubleganger.android.adapter;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 13:57
 */
public class Item {

    private String itemName = "";

    private String itemDesc = "";


    public String getItemName() {

        return itemName;

    }

    public void setItemName(String itemName) {

        this.itemName = itemName;

    }

    public String getItemDesc() {

        return itemDesc;

    }

    public void setItemDesc(String itemDesc) {

        this.itemDesc = itemDesc;

    }

    @Override
    public String toString() {
        return "Item{"
            + "itemName='" + itemName + '\''
            + ", itemDesc='" + itemDesc + '\''
            + '}';
    }
}
