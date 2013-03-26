package de.consistec.doubleganger.android.dialog;

import de.consistec.doubleganger.android.HelloAndroidActivity;
import de.consistec.doubleganger.android.R;
import de.consistec.doubleganger.android.ThreadEvent;
import de.consistec.doubleganger.android.adapter.Item;
import de.consistec.doubleganger.android.adapter.ItemArrayAdapter;
import de.consistec.doubleganger.android.adapter.ItemFactory;
import de.consistec.doubleganger.android.conflict.ConflictResolver;
import de.consistec.doubleganger.common.conflict.UserDecision;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 10:18
 */
public class ConflictDialog extends Dialog {

    // Use this instance of the interface to deliver action events
    private NoticeConflictDialogListener mListener;
    private ThreadEvent threadEvent;

    private Button useClientButton;
    private Button useServerButton;

    public ConflictDialog(final Context context, ConflictResolver conflictResolver, Item[] clientValues,
                          Item[] serverValues
    ) {
        super(context);

        setContentView(R.layout.conflict_dialog);
        setTitle("Resolve following conflicts ...");

        ListView clientListView = (ListView) findViewById(R.id.clientListView);
        ListView serverListView = (ListView) findViewById(R.id.serverListView);

        useClientButton = (Button) findViewById(R.id.useClientBtn);
        useServerButton = (Button) findViewById(R.id.useServerBtn);

        useClientButton.setOnClickListener(new UseClientButtonClickListener());
        useServerButton.setOnClickListener(new UseServerButtonClickListener());

        Button editAnduseClientButton = (Button) findViewById(R.id.editAnduseClientBtn);
        Button editAnduseServerButton = (Button) findViewById(R.id.editAnduseServerBtn);

        editAnduseClientButton.setOnClickListener(new UseClientButtonClickListener());
        editAnduseServerButton.setOnClickListener(new UseServerButtonClickListener());

        final Item[] clientItems;
        if (clientValues.length == 0) {
            clientItems = new Item[1];
            clientItems[0] = ItemFactory.createDeletedItem();
            editAnduseClientButton.setEnabled(false);
            editAnduseClientButton.setClickable(false);
        } else {
            clientItems = clientValues;
        }

        ItemArrayAdapter customClientAdapter = new ItemArrayAdapter(context,
            ((HelloAndroidActivity) context).getLayoutConflictResourceId(), clientItems);


        final Item[] serverItems;
        if (serverValues.length == 0) {
            serverItems = new Item[1];
            serverItems[0] = ItemFactory.createDeletedItem();
            editAnduseServerButton.setEnabled(false);
            editAnduseServerButton.setClickable(false);
        } else {
            serverItems = serverValues;
        }

        ItemArrayAdapter customServerAdapter = new ItemArrayAdapter(context,
            ((HelloAndroidActivity) context).getLayoutConflictResourceId(), serverItems);

        clientListView.setAdapter(customClientAdapter);
        serverListView.setAdapter(customServerAdapter);


        mListener = conflictResolver;
    }

    public void setThreadEvent(final ThreadEvent threadEvent) {
        this.threadEvent = threadEvent;
    }

    private class UseClientButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {

            if (arg0 == useClientButton) {
                mListener.onConflictDialogPositiveClick(UserDecision.CLIENT_CHANGE);
            } else {
                mListener.onConflictDialogPositiveClick(UserDecision.EDIT_CLIENT_CHANGE);
            }

            if (threadEvent != null) {
                threadEvent.signal();
            }
            ConflictDialog.this.cancel();
        }
    }

    private class UseServerButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {

            if (arg0 == useServerButton) {
                mListener.onConflictDialogPositiveClick(UserDecision.SERVER_CHANGE);
            } else {
                mListener.onConflictDialogPositiveClick(UserDecision.EDIT_SERVER_CHANGE);
            }

            if (threadEvent != null) {
                threadEvent.signal();
            }
            ConflictDialog.this.cancel();
        }
    }
}
