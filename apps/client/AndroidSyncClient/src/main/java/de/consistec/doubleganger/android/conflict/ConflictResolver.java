package de.consistec.doubleganger.android.conflict;

import de.consistec.doubleganger.android.HelloAndroidActivity;
import de.consistec.doubleganger.android.ThreadEvent;
import de.consistec.doubleganger.android.adapter.Item;
import de.consistec.doubleganger.android.adapter.ItemFactory;
import de.consistec.doubleganger.android.dialog.ConflictDialog;
import de.consistec.doubleganger.android.dialog.EditConflictDialog;
import de.consistec.doubleganger.android.dialog.NoticeConflictDialogListener;
import de.consistec.doubleganger.android.dialog.NoticeEditConflictDialogListener;
import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.conflict.UserDecision;
import de.consistec.doubleganger.common.data.ResolvedChange;

import java.util.Map;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 10:13
 */
public class ConflictResolver
    implements IConflictListener, NoticeConflictDialogListener, NoticeEditConflictDialogListener {

    private HelloAndroidActivity ctx;
    private Map<String, Object> clientData;
    private Map<String, Object> serverData;
    private ResolvedChange resolvedChange;
    private boolean userWantsToEdit = false;

    private ThreadEvent resultsReady = new ThreadEvent();


    public ConflictResolver(HelloAndroidActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    public ResolvedChange resolve(final Map<String, Object> serverData, final Map<String, Object> clientData
    ) {
        this.clientData = clientData;
        this.serverData = serverData;

        showConflictDialog(clientData, serverData);

        try {
            resultsReady.await();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        if (userWantsToEdit) {
            showEditConflictDialog();

            try {
                resultsReady.await();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }

        return resolvedChange;
    }

    private void showEditConflictDialog() {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Item[] selectedChangeItems = ItemFactory.createChangeItems(resolvedChange.getRowData());

                final EditConflictDialog dlg = new EditConflictDialog(ctx, ConflictResolver.this,
                    selectedChangeItems);
                dlg.setThreadEvent(resultsReady);
                dlg.show();
            }
        });
    }

    private void showConflictDialog(final Map<String, Object> clientData, final Map<String, Object> serverData) {

        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Item[] clientItems = ItemFactory.createChangeItems(clientData);
                Item[] serverItems = ItemFactory.createChangeItems(serverData);

                final ConflictDialog dlg = new ConflictDialog(ctx, ConflictResolver.this,
                    clientItems, serverItems);
                dlg.setThreadEvent(resultsReady);
                dlg.show();
            }
        });
    }

    @Override
    public void onConflictDialogPositiveClick(final UserDecision decision) {
        resolvedChange = new ResolvedChange(decision);

        switch (decision) {
            case CLIENT_CHANGE:
                resolvedChange.setRowData(clientData);
                userWantsToEdit = false;
                break;
            case SERVER_CHANGE:
                resolvedChange.setRowData(serverData);
                userWantsToEdit = false;
                break;
            case EDIT_CLIENT_CHANGE:
                resolvedChange.setRowData(clientData);
                userWantsToEdit = true;
                break;
            case EDIT_SERVER_CHANGE:
                resolvedChange.setRowData(serverData);
                userWantsToEdit = true;
                break;
            default:
                resolvedChange.setRowData(serverData);
                break;
        }
    }

    @Override
    public void onConflictDialogNegativeClick(final UserDecision decision) {
        // default configuration for the case the user stops the conflict handling
        resolvedChange = new ResolvedChange(UserDecision.SERVER_CHANGE);
        resolvedChange.setRowData(serverData);
    }

    @Override
    public void onEditConflictDialogPositiveClick(final ResolvedChange resolvedChange) {
        UserDecision selectedDecision = this.resolvedChange.getDecision();
        this.resolvedChange = resolvedChange;
        this.resolvedChange.setSelectedDecision(selectedDecision);
    }

    @Override
    public void onEditConflictDialogNegativeClick(final ResolvedChange resolvedChange) {
        // default configuration for the case the user stops the conflict handling
        this.resolvedChange = new ResolvedChange(UserDecision.SERVER_CHANGE);
        this.resolvedChange.setRowData(serverData);
    }
}
