package de.consistec.doubleganger.android.adapter;

import de.consistec.doubleganger.android.HelloAndroidActivity;
import de.consistec.doubleganger.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 13:55
 */
public class ItemArrayAdapter extends ArrayAdapter<Item> {

    private final int resourceId;
//    private Item[] items;
//    private Context context;

//    public ItemArrayAdapter(final Context context, final int textViewResourceId, Item[] items) {
//
//        this.resourceId = textViewResourceId;
//        this.items = items;
//        this.context = context;
//    }

    public ItemArrayAdapter(final Context context, final int textViewResourceId) {
        super(context, textViewResourceId);

        resourceId = textViewResourceId;
    }

    public ItemArrayAdapter(final Context context, final int textViewResourceId, Item[] items) {
        super(context, textViewResourceId, items);

        resourceId = textViewResourceId;
    }

//    @Override
//    public int getCount() {
//        return items.length;
//    }
//
//    @Override
//    public Object getItem(final int i) {
//        return items[i];
//    }
//
//    @Override
//    public long getItemId(final int i) {
//        return i;
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Item t = (Item) getItem(position);

        if (t == null) {
            return null;
        }

        final ViewHolder holder;

        View view = null;
        if (convertView == null) {
            HelloAndroidActivity activity = ((HelloAndroidActivity) getContext());
            LayoutInflater mInflater = activity.getLayoutInflater();
            view = mInflater.inflate(resourceId, null);
            holder = new ViewHolder();
            holder.columnView = (TextView) view.findViewById(R.id.column);
            holder.columnValueView = (TextView) view.findViewById(R.id.columnValue);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.columnView.setText(t.getItemName());
        holder.columnValueView.setText(t.getItemDesc());

        //we need to update adapter once we finish with editing
        holder.columnValueView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
//                    final int position = v.getId();
                    final TextView columnValue = (TextView) v;

                    System.out.println("Position: " + position);
                    System.out.println("holder value: " + holder.columnValueView.getText());
                    t.setItemDesc(columnValue.getText().toString());

//                    System.out.println("Items-length: " + items.length);
//                    items[position].setItemDesc(columnValue.getText().toString());
                }
            }
        });

        return view;
    }

    private static class ViewHolder {
        private TextView columnView;
        private TextView columnValueView;
    }
}
