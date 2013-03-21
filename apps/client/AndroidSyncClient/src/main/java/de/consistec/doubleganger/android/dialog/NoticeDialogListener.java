package de.consistec.doubleganger.android.dialog;

import de.consistec.doubleganger.common.conflict.UserDecision;

/**
 * The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 21.03.13 10:15
 */
public interface NoticeDialogListener {
    void onDialogPositiveClick(UserDecision userDecision);

    void onDialogNegativeClick(UserDecision userDecision);
}

