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
public class ConflictDialog extends Dialog {

    private EditText mEditText;
    private UserDecision decision;
    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;
    private ThreadEvent threadEvent;


    public ConflictDialog(final Context context, ConflictResolver conflictResolver, Item[] clientValues,
                          Item[] serverValues
    ) {
        super(context);

        this.decision = UserDecision.SERVER_CHANGE;

        setContentView(R.layout.conflict_dialog);
        setTitle("Resolve following conflicts ...");

        ListView clientListView = (ListView) findViewById(R.id.clientListView);
        ListView serverListView = (ListView) findViewById(R.id.serverListView);

        Button useClientButton = (Button) findViewById(R.id.useClientBtn);
        Button useServerButton = (Button) findViewById(R.id.useServerBtn);

        useClientButton.setOnClickListener(new UseClientButtonClickListener());
        useServerButton.setOnClickListener(new UseServerButtonClickListener());

        ItemArrayAdapter customClientAdapter = new ItemArrayAdapter(context,
            ((HelloAndroidActivity) context).getLayoutConflictResourceId(), clientValues);

        ItemArrayAdapter customServerAdapter = new ItemArrayAdapter(context,
            ((HelloAndroidActivity) context).getLayoutConflictResourceId(), serverValues);

        clientListView.setAdapter(customClientAdapter);
        serverListView.setAdapter(customServerAdapter);


        mListener = conflictResolver;
    }

    public UserDecision getDecision() {
        return decision;
    }

    public void setThreadEvent(final ThreadEvent threadEvent) {
        this.threadEvent = threadEvent;
    }

    private class UseClientButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {

            mListener.onDialogPositiveClick(UserDecision.CLIENT_CHANGE);
            if (threadEvent != null) {
                threadEvent.signal();
            }
            ConflictDialog.this.cancel();
        }
    }

    private class UseServerButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {

            mListener.onDialogPositiveClick(decision = UserDecision.SERVER_CHANGE);
            if (threadEvent != null) {
                threadEvent.signal();
            }
            ConflictDialog.this.cancel();
        }
    }
}
