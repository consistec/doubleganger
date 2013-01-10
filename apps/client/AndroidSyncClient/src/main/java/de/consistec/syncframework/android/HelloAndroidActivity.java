package de.consistec.syncframework.android;

import de.consistec.syncframework.android.adapter.GingerbreadSQLiteDatabaseAdapter;
import de.consistec.syncframework.android.adapter.ICSSQLiteDatabaseAdapter;
import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.ISyncProgressListener;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import de.mindpipe.android.logging.log4j.LogConfigurator;
import java.io.File;
import java.util.Properties;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HelloAndroidActivity.
 * Entrypoint for syncframework Application
 */
public class HelloAndroidActivity extends Activity {

    private static final Logger LOG;
    private static final Config CONF = Config.getInstance();
    private TextView tv;
    private EditText et;

    // configuring log4j logger
    static {
        final LogConfigurator logConfigurator = new LogConfigurator();

        logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "syncframework.log");
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel("syncframework", Level.ALL);
        logConfigurator.configure();
        LOG = LoggerFactory.getLogger("syncframework");
    }

    /**
     * Called when the activity is first created.
     * <p/>
     * @param savedInstanceState
     * If the activity is being re-initialized after previously being
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
        tv = (TextView) findViewById(R.id.statusTextView);
        et = (EditText) findViewById(R.id.urlEditText);
        syncButton.setOnClickListener(new SyncButtonClickListener());
    }

    private void logAndShowErrorToast(Exception e) {
        Toast.makeText(getBaseContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        LOG.error(e.getLocalizedMessage(), e);
    }

    private class HelloAndroidActivityProgressListener implements ISyncProgressListener {

        @Override
        public void syncFinished() {
            HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText("Sync finished!");
                }
            });
        }

        @Override
        public void progressUpdate(final String message) {
            HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(message);
                }
            });
        }
    }

    private class SyncButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View arg0) {

            RadioButton gingerbreadRb = (RadioButton) findViewById(R.id.osGingerbreadRadioButton);
            RadioButton icsRb = (RadioButton) findViewById(R.id.osICSRadioButton);
            if (!gingerbreadRb.isChecked() && !icsRb.isChecked()) {
                Toast.makeText(HelloAndroidActivity.this, "Please choose an OS", Toast.LENGTH_LONG).show();
                return;
            }

            CONF.addSyncTable("categories", "items");

            Properties p = new Properties();
            p.setProperty(GenericDatabaseAdapter.PROPS_URL, "jdbc:sqlite:/mnt/sdcard/client.sl3");

            if (icsRb.isChecked()) {

                p.setProperty(GenericDatabaseAdapter.PROPS_DRIVER_NAME, "org.sqldroid.SQLDroidDriver");
                CONF.setClientDatabaseAdapter(ICSSQLiteDatabaseAdapter.class);

            } else if (gingerbreadRb.isChecked()) {

                p.setProperty(GenericDatabaseAdapter.PROPS_DRIVER_NAME, "SQLite.JDBCDriver");
                CONF.setClientDatabaseAdapter(GingerbreadSQLiteDatabaseAdapter.class);
            } else {
                CONF.setClientDatabaseAdapter(GenericDatabaseAdapter.class);
            }

            try {
                final SyncContext.ClientContext clientCtx = SyncContext.ClientContext.create();
                clientCtx.addProgressListener(new HelloAndroidActivityProgressListener());

                AsyncTask t = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            clientCtx.synchronize();
                        } catch (final SyncException ex) {
                            HelloAndroidActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText(ex.getLocalizedMessage());
                                }
                            });
                        }
                        return new Object();
                    }
                };
                t.execute(new Object());

            } catch (ContextException ex) {
                tv.setText(ex.getLocalizedMessage());
            }
        }
    }
}
