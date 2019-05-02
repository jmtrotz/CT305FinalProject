package com.jeff.locatr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;

/**
 * Class to get the device location and draw images on the map
 * @author Jeffrey Trotz
 * @version 1.0
 * Date: 5/1/18
 */
public class LocatrFragment extends SupportMapFragment
{
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Bitmap mMapImage;
    private GalleryItem mMapItem;
    private Location mCurrentLocation;
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final String TAG = "LocatrFragment";
    private static final String[] LOCATION_PERMISSIONS = new String[]
            {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    /**
     * Creates a new instance of the UI fragment
     * @return Returns the fragment
     */
    public static LocatrFragment newInstance()
    {
        return new LocatrFragment();
    }

    /**
     * Method to check for location permissions
     * @return Returns the result as a boolean
     */
    private boolean hasLocationPermission()
    {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Generates a location request and finds an image based on the device's location
     */
    private void findImage()
    {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.i(TAG, "Got a fix: " + location);
                new SearchTask().execute(location);
            }
        });
    }

    /**
     * Draws the red pin and the image from Flickr on the map
     */
    private void updateUI()
    {
        if (mMap == null || mMapImage == null)
        {
            return;
        }

        LatLng itemPoint = new LatLng(mMapItem.getLatitude(), mMapItem.getLongitude());
        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(mMapImage);
        MarkerOptions itemMarker = new MarkerOptions().position(itemPoint).icon(itemBitmap);
        MarkerOptions myMarker = new MarkerOptions().position(myPoint);
        mMap.clear();
        mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);

        LatLngBounds bounds = new LatLngBounds.Builder().include(itemPoint).include(myPoint).build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }

    /**
     * Creates a new connection to Google's location API and draws the map
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                {
                    @Override
                    public void onConnected(@Nullable Bundle bundle)
                    {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i)
                    {
                        // Not used
                    }
                })
                .build();

        getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                mMap = googleMap;
                updateUI();
            }
        });
    }

    /**
     * Connects to Google's API  services
     */
    @Override
    public void onStart()
    {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    /**
     * Disconnects from Google's API services
     */
    @Override
    public void onStop()
    {
        super.onStop();
        mClient.disconnect();
    }

    /**
     * Creates the options menu
     * @param menu Menu to be created
     * @param menuInflater Inflater for the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_locatr, menu);
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    /**
     * Chooses an action based on which menu item was selected
     * @param menuItem Menu item the user selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.action_locate:
                if (hasLocationPermission())
                {
                    findImage();
                }

                else
                {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Checks/asks for location permissions
     * @param requestCode Identifier for the rquest
     * @param permissions Permissions being requested
     * @param grantResults Permissions that were granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission())
                {
                    findImage();
                }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Runs a background thread to download images and sort through them
     */
    private class SearchTask extends AsyncTask<Location,Void,Void>
    {
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;

        /**
         * Downloads images from Flickr
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Location... params)
        {
            mLocation = params[0];
            FlickrFetchr fetchr = new FlickrFetchr();
            List<GalleryItem> items = fetchr.searchPhotos(params[0]);

            if (items.size() == 0)
            {
                return null;
            }

            mGalleryItem = items.get(0);

            try
            {
                byte[] bytes = fetchr.getUrlBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            catch (IOException ioException)
            {
                Log.e(TAG, "Unable to decode bitmap", ioException);
            }

            return null;
        }

        /**
         * Calls the method to draw the pin and the image on
         * the map after the location has been figured out and
         * the images have been downloaded/sorted through
         * @param result
         */
        @Override
        protected void onPostExecute(Void result)
        {
            mMapImage = mBitmap;
            mMapItem = mGalleryItem;
            mCurrentLocation = mLocation;
            updateUI();
        }
    }
}