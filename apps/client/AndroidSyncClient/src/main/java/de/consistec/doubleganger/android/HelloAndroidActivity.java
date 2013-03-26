package de.consistec.doubleganger.android;

/*
 * #%L
 * doppelganger
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import de.consistec.doubleganger.android.adapter.GingerbreadSQLiteDatabaseAdapter;
import de.consistec.doubleganger.android.adapter.ICSSQLiteDatabaseAdapter;
import de.consistec.doubleganger.android.conflict.ConflictResolver;
import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.ISyncProgressListener;
import de.consistec.doubleganger.common.SyncContext;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HelloAndroidActivity.
 * Entrypoint for doubleganger Application
 */
public class HelloAndroidActivity extends Activity {

    private static final String GINGERBREAD_PROPERTIES_FILE = "gingerbread.properties";
    private static final String ICS_PROPERTIES_FILE = "ics.properties";
    private static final Logger LOG;
    private static final Config CONF = Config.getInstance();
    private TextView textView;
    private EditText editText;

    // configuring log4j logger
    static {
        LOG = LoggerFactory.getLogger("doubleganger");
    }

    /**
     * Called when the activity is first created.
     * <p/>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     * is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOG.debug("onCreate");

        setContentView(R.layout.main);
        Button syncButton = (Button) findViewById(R.id.syncButton);
        textView = (TextView) findViewById(R.id.statusTextView);
        editText = (EditText) findViewById(R.id.urlEditText);

        syncButton.setOnClickListener(new SyncButtonClickListener());
    }

    private void displayText(final String message) {
        HelloAndroidActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });
    }

    public int getLayoutConflictResourceId() {
        return R.layout.conflict_row_view;
    }

    public int getLayoutEditConflictResourceId() {
        return R.layout.edit_conflict_row_view;
    }

    private class HelloAndroidActivityProgressListener implements ISyncProgressListener {

        @Override
        public void progressUpdate(final String message) {
            displayText(message);
        }

        @Override
        public void syncFinished() {
            displayText("Synchronization finished!");
        }
    }

    private class SyncButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            textView.setText("");

            if (isGingerbread()) {
                initializeConfigGingerbread();
            } else {
                initializeConfigICS();
            }

            synchronize();
        }

        private boolean isGingerbread() {
            return Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
        }

        private void initializeConfigGingerbread() {
            readConfigFile(GINGERBREAD_PROPERTIES_FILE);
            CONF.setClientDatabaseAdapter(GingerbreadSQLiteDatabaseAdapter.class);
        }

        private void initializeConfigICS() {
            readConfigFile(ICS_PROPERTIES_FILE);
            CONF.setClientDatabaseAdapter(ICSSQLiteDatabaseAdapter.class);
        }

        private void readConfigFile(String propFile) {
            InputStream in = null;
            try {
                in = getAssets().open(propFile);
                CONF.init(in);
            } catch (IOException e) {
                LOG.warn("Could not read " + propFile + " in!", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        LOG.warn("Unable to close input stream");
                    }
                }
            }
        }

        private void synchronize() {
            AsyncTask t = new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        final SyncContext.ClientContext clientCtx = SyncContext.client();
                        clientCtx.addProgressListener(new HelloAndroidActivityProgressListener());

                        clientCtx.setConflictListener(new ConflictResolver(HelloAndroidActivity.this));
                        clientCtx.synchronize();

                    } catch (final SyncException ex) {
                        LOG.error(null, ex);
                        displayText(ex.getLocalizedMessage());
                    } catch (final ContextException ex) {
                        LOG.error(null, ex);
                        displayText(ex.getLocalizedMessage());
                    }
                    return new Object();
                }
            };
            t.execute(new Object());

        }
    }
}
