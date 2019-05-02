package com.jeff.locatr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Class to create a new UI fragment for the map
 * @author Jeffrey Trotz
 * @version 1.0
 * Date: 5/1/18
 */
public class LocatrActivity extends SingleFragmentActivity
{
    private static final int REQUEST_ERROR = 0;

    /**
     * Creates a new instance of the fragment
     * @return Returns a new fragment
     */
    @Override
    protected Fragment createFragment()
    {
        return LocatrFragment.newInstance();
    }

    /**
     * Method to create a new instance of Google's API services
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS)
        {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode,
                    REQUEST_ERROR, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    // Leave if services are unavailable
                    finish();
                }
            });

            errorDialog.show();
        }
    }
}