package de.consistec.doubleganger.android.dialog;

import de.consistec.doubleganger.common.data.ResolvedChange;

/**
 * The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 21.03.13 11:12
 */
public interface NoticeEditConflictDialogListener {

    void onEditConflictDialogPositiveClick(ResolvedChange resolvedChange);

    void onEditConflictDialogNegativeClick(ResolvedChange resolvedChange);
}

