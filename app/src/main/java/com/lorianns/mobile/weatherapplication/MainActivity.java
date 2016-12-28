package com.lorianns.mobile.weatherapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lorianns.mobile.weatherapplication.rest.Utils;
import com.lorianns.mobile.weatherapplication.ui.InputLocationDialogFragment;
import com.lorianns.mobile.weatherapplication.ui.WeatherFragment;

public class MainActivity extends AppCompatActivity implements InputLocationDialogFragment.InputLocationDialogListener{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String INPUT_DIALOG_TAG = "input_dialog_tag";
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation = Utils.getPreferredLocation(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_zipCode) {
            btnShowLocationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSubmitLocation(String zipCode) {
        mLocation = zipCode;

        String prefLocation = Utils.getPreferredLocation(this);

        if (prefLocation != null && !prefLocation.equals(mLocation)) {
            Utils.setPreferredLocation(this,mLocation );
            WeatherFragment frag = (WeatherFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if ( null != frag )
                frag.onLocationChanged();

            mLocation = prefLocation;
        }
    }

    public void btnShowLocationDialog() {
        showInputNameDialog();
    }

    private void showInputNameDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        InputLocationDialogFragment inputNameDialog = new InputLocationDialogFragment();
        inputNameDialog.setCancelable(true);
        inputNameDialog.setDialogTitle(getString(R.string.lbl_zip_code_title));
        inputNameDialog.show(fragmentManager, INPUT_DIALOG_TAG);
    }
}
