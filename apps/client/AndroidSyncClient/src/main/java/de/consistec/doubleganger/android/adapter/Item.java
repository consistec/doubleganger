package de.consistec.doubleganger.android.adapter;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 13:57
 */
public class Item {

    private String itemName = "";

    private String itemDesc = "";
    private Object itemValue;


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

    public Object getItemValue() {
        return itemValue;
    }

    public void setItemValue(final Object itemValue) {
        this.itemValue = itemValue;
    }

    @Override
    public String toString() {
        return "Item{"
            + "itemName='" + itemName + '\''
            + ", itemDesc='" + itemDesc + '\''
            + '}';
    }
}
