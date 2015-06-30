package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

public class MainActivity extends ActionBarActivity implements ForecastFragment.OnItemSelectedListenerCallback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mLocation;
    private String mUnits;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        mUnits = Utility.getPreferredMetric(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        if (null != ff) {
            ff.setUseTodayLayout(!mTwoPane);
        }
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                    ff.onLocationChanged();
                }

            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }

//        String units = Utility.getPreferredMetric( this );
//        // update the location in our second pane using the fragment manager
//        if (units != null && !units.equalsIgnoreCase(mUnits)) {
//            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
//            if ( null != ff ) {
//                ff.onUnitsChanged();
//            }
//            mUnits = units;
//        }

    }

    public void onItemSelected(Uri dateUri) {
        if (mTwoPane) {

            DetailFragment df = new DetailFragment();
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);
            df.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.weather_detail_container, df, DETAILFRAGMENT_TAG);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }
}