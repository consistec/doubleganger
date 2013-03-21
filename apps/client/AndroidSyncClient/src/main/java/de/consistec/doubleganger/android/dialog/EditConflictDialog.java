package de.consistec.doubleganger.android.dialog;

import de.consistec.doubleganger.android.HelloAndroidActivity;
import de.consistec.doubleganger.android.R;
import de.consistec.doubleganger.android.ThreadEvent;
import de.consistec.doubleganger.android.adapter.Item;
import de.consistec.doubleganger.android.adapter.ItemArrayAdapter;
import de.consistec.doubleganger.android.conflict.ConflictResolver;
import de.consistec.doubleganger.common.conflict.UserDecision;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 10:18
 */
public class EditConflictDialog extends Dialog {

    private EditText mEditText;
    private UserDecision decision;
    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;
    private ThreadEvent threadEvent;
    private ItemArrayAdapter selectedChangeAdapter;
    private Item[] selectedChangeItems;


    public EditConflictDialog(final Context context, ConflictResolver conflictResolver, Item[] selectedChangeItems
    ) {
        super(context);

        this.decision = UserDecision.SERVER_CHANGE;

        setContentView(R.layout.edit_conflict_dialog);
        setTitle("edit your selected change ...");

        ListView selectedChangeListView = (ListView) findViewById(R.id.selectedChangeListView);

        Button selectedChangeButton = (Button) findViewById(R.id.useSelectedChangeBtn);

        selectedChangeButton.setOnClickListener(new SelectedChangeButtonClickListener());

        selectedChangeAdapter = new ItemArrayAdapter(context,
            ((HelloAndroidActivity) context).getLayoutResourceId(), selectedChangeItems);

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

            for (Item item : selectedChangeItems) {
                System.out.println(item);
            }
            mListener.onDialogPositiveClick(UserDecision.CLIENT_CHANGE);
            if (threadEvent != null) {
                threadEvent.signal();
            }
            EditConflictDialog.this.cancel();
        }
    }
}
