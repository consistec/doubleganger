package de.consistec.doubleganger.android.dialog;

import de.consistec.doubleganger.android.HelloAndroidActivity;
import de.consistec.doubleganger.android.R;
import de.consistec.doubleganger.android.ThreadEvent;
import de.consistec.doubleganger.android.adapter.Item;
import de.consistec.doubleganger.android.adapter.ItemArrayAdapter;
import de.consistec.doubleganger.android.adapter.ItemFactory;
import de.consistec.doubleganger.android.conflict.ConflictResolver;
import de.consistec.doubleganger.common.conflict.UserDecision;
import de.consistec.doubleganger.common.data.ResolvedChange;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.HashMap;
import java.util.Map;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 10:18
 */
public class EditConflictDialog extends Dialog {

    // Use this instance of the interface to deliver action events
    private NoticeEditConflictDialogListener mListener;
    private ThreadEvent threadEvent;
    private ItemArrayAdapter selectedChangeAdapter;
    private Item[] selectedChangeItems;


    public EditConflictDialog(final Context context, ConflictResolver conflictResolver, Item[] selectedChangeItems
    ) {
        super(context);

        setContentView(R.layout.edit_conflict_dialog);
        setTitle("edit your selected change ...");

        ListView selectedChangeListView = (ListView) findViewById(R.id.selectedChangeListView);


        Button selectedChangeButton = (Button) findViewById(R.id.useSelectedChangeBtn);

        selectedChangeButton.setOnClickListener(new SelectedChangeButtonClickListener());

        final Item[] selectedItems;
        if (selectedChangeItems.length == 0) {
            selectedItems = new Item[1];
            selectedItems[0] = ItemFactory.createDeletedItem();
        } else {
            selectedItems = selectedChangeItems;
        }

        selectedChangeAdapter = new ItemArrayAdapter(context,
            ((HelloAndroidActivity) context).getLayoutEditConflictResourceId(), selectedItems);

        selectedChangeListView.setAdapter(selectedChangeAdapter);

        mListener = conflictResolver;
        this.selectedChangeItems = selectedChangeItems;
    }

    public void setThreadEvent(final ThreadEvent threadEvent) {
        this.threadEvent = threadEvent;
    }

    private class SelectedChangeButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {

            Map<String, Object> rowData = new HashMap<String, Object>();
            for (int i = 0; i < selectedChangeAdapter.getCount(); i++) {
                Item item = selectedChangeAdapter.getItem(i);
                rowData.put(item.getItemName(), item.getItemValue());
            }

            ResolvedChange resolvedChange = new ResolvedChange(UserDecision.USER_EDIT);
            resolvedChange.setRowData(rowData);

            mListener.onEditConflictDialogPositiveClick(resolvedChange);
            if (threadEvent != null) {
                threadEvent.signal();
            }
            EditConflictDialog.this.cancel();
        }
    }
}
