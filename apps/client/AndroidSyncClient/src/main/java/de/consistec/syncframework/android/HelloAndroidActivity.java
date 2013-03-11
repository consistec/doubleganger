package de.consistec.syncframework.android;

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
import de.consistec.syncframework.android.adapter.GingerbreadSQLiteDatabaseAdapter;
import de.consistec.syncframework.android.adapter.ICSSQLiteDatabaseAdapter;
import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.ISyncProgressListener;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HelloAndroidActivity.
 * Entrypoint for syncframework Application
 */
public class HelloAndroidActivity extends Activity {

    private static final Logger LOG;
    private static final Config CONF = Config.getInstance();
    private TextView textView;
    private EditText editText;

    // configuring log4j logger
    static {
        LOG = LoggerFactory.getLogger("syncframework");
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

    private class HelloAndroidActivityProgressListener implements ISyncProgressListener {

        @Override
        public void progressUpdate(final String message) {
            HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(message);
                }
            });
        }

        @Override
        public void syncFinished() {
            HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Sync finished!");
                }
            });
        }
    }

    private class SyncButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            textView.setText("");
            RadioButton gingerbreadRb = (RadioButton) findViewById(R.id.osGingerbreadRadioButton);
            RadioButton icsRb = (RadioButton) findViewById(R.id.osICSRadioButton);

            if (icsRb.isChecked()) {
                initializeConfigICS();
            } else if (gingerbreadRb.isChecked()) {
                initializeConfigGingerbread();
            } else {
                Toast.makeText(HelloAndroidActivity.this, "Please choose an OS", Toast.LENGTH_LONG).show();
                return;
            }

            synchronize();
        }

        private void initializeConfigICS() {
            readConfigFile("ics.properties");
            CONF.setClientDatabaseAdapter(ICSSQLiteDatabaseAdapter.class);

        }

        private void initializeConfigGingerbread() {
            readConfigFile("gingerbread.properties");
            CONF.setClientDatabaseAdapter(GingerbreadSQLiteDatabaseAdapter.class);
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
                        clientCtx.synchronize();

                        HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("Synchronization finished!");
                            }
                        });

                    } catch (final SyncException ex) {
                        LOG.error(null, ex);
                        HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(ex.getLocalizedMessage());
                            }
                        });
                    } catch (final ContextException ex) {
                        LOG.error(null, ex);
                        HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(ex.getLocalizedMessage());
                            }
                        });
                    }
                    return new Object();
                }
            };
            t.execute(new Object());

        }
    }
}
