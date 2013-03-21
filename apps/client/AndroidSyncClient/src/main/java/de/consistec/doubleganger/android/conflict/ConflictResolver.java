package de.consistec.doubleganger.android.conflict;

import de.consistec.doubleganger.android.HelloAndroidActivity;
import de.consistec.doubleganger.android.ThreadEvent;
import de.consistec.doubleganger.android.adapter.Item;
import de.consistec.doubleganger.android.dialog.ConflictDialog;
import de.consistec.doubleganger.android.dialog.EditConflictDialog;
import de.consistec.doubleganger.android.dialog.NoticeDialogListener;
import de.consistec.doubleganger.common.IConflictListener;
import de.consistec.doubleganger.common.conflict.UserDecision;
import de.consistec.doubleganger.common.data.ResolvedChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.03.13 10:13
 */
public class ConflictResolver implements IConflictListener, NoticeDialogListener {

    private HelloAndroidActivity ctx;
    private Map<String, Object> clientData;
    private Map<String, Object> serverData;
    private ResolvedChange resolvedChange;

    private ThreadEvent resultsReady = new ThreadEvent();


    public ConflictResolver(HelloAndroidActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    public ResolvedChange resolve(final Map<String, Object> serverData, final Map<String, Object> clientData
    ) {
        this.clientData = clientData;
        this.serverData = serverData;

        showDialog(clientData, serverData);

        try {
            resultsReady.await();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        showEditDialog(resolvedChange);

        try {
            resultsReady.await();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        return resolvedChange;
    }

    private void showEditDialog(final ResolvedChange selectedChange) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Item[] selectedChangeItems = createSelectedChangeItems(selectedChange);
                final EditConflictDialog dlg = new EditConflictDialog(ctx, ConflictResolver.this,
                    selectedChangeItems);
                dlg.setThreadEvent(resultsReady);
                dlg.show();
            }

            private Item[] createSelectedChangeItems(final ResolvedChange selectedChange) {
                List<Item> items = new ArrayList<Item>(selectedChange.getRowData().size());
                for (String column : selectedChange.getRowData().keySet()) {
                    Item item = new Item();
                    item.setItemName(column);
                    item.setItemDesc(selectedChange.getRowData().get(column).toString());
                    items.add(item);
                }
                return items.toArray(new Item[0]);
            }
        });
    }

    private void showDialog(final Map<String, Object> clientData, final Map<String, Object> serverData) {

        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Item[] clientItems = createClientItems(clientData);
                Item[] serverItems = createServerItems(serverData);
                final ConflictDialog dlg = new ConflictDialog(ctx, ConflictResolver.this,
                    clientItems, serverItems);
                dlg.setThreadEvent(resultsReady);
                dlg.show();
            }

            private Item[] createServerItems(final Map<String, Object> serverData) {
                List<Item> items = new ArrayList<Item>(serverData.size());
                for (String column : serverData.keySet()) {
                    Item item = new Item();
                    item.setItemName(column);
                    item.setItemDesc(serverData.get(column).toString());
                    items.add(item);
                }
                return items.toArray(new Item[0]);
            }

            private Item[] createClientItems(final Map<String, Object> clientData) {
                List<Item> items = new ArrayList<Item>(serverData.size());
                for (String column : clientData.keySet()) {
                    Item item = new Item();
                    item.setItemName(column);
                    item.setItemDesc(clientData.get(column).toString());
                    items.add(item);
                }
                return items.toArray(new Item[0]);
            }
        });
    }

    @Override
    public void onDialogPositiveClick(final UserDecision decision) {
        resolvedChange = new ResolvedChange(decision);
        if (decision == UserDecision.CLIENT_CHANGE) {
            resolvedChange.setRowData(clientData);
        } else {
            resolvedChange.setRowData(serverData);
        }
    }

    @Override
    public void onDialogNegativeClick(final UserDecision decision) {
        // default configuration for the case the user stops the conflict handling
        resolvedChange = new ResolvedChange(UserDecision.SERVER_CHANGE);
        resolvedChange.setRowData(serverData);
    }
}
