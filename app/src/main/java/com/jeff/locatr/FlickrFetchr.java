package com.jeff.locatr;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to download JSON data from Flickr
 * @author Jeffrey Trotz
 * @version 1.0
 * Date: 5/1/18
 */
public class FlickrFetchr
{
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "81081bb696b492004599691158dc188e";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s,geo")
            .build();

    /**
     * Opens input and output streams and stores the collected data in a byte array
     * @param urlSpec Flickr URL
     * @return Returns a byte array containing the downloaded data
     * @throws IOException Throws an exception if there's an IO error
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException
    {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            return outputStream.toByteArray();
        }

        finally
        {
            connection.disconnect();
        }
    }

    /**
     * Converts the image URL into a byte array and returns it as a String
     * @param urlSpec Image URL
     * @return Returns the byte array it converted the URL to as a String
     * @throws IOException Throws an exception if there's an IO error
     */
    public String getUrlString(String urlSpec) throws IOException
    {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * Searches through photos to find one taken at the device's current location
     * @param location Location of the device
     * @return Returns an array list of images
     */
    public List<GalleryItem> searchPhotos(Location location)
    {
        String url = buildUrl(location);
        return downloadGalleryItems(url);
    }

    /**
     * Downloads the JSON data and feeds it to the parseItems method to sift through it
     * @return Returns an array list of downloaded images
     */
    private List<GalleryItem> downloadGalleryItems(String url)
    {
        List<GalleryItem> galleryItems = new ArrayList<>();

        try
        {
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(galleryItems, jsonBody);
        }

        catch (IOException ioException)
        {
            Log.e(TAG, "Failed to fetch items", ioException);
        }

        catch (JSONException jsonException)
        {
            Log.e(TAG, "Failed to parse JSON", jsonException);
        }

        return galleryItems;
    }

    /**
     * Builds the URL with query parameters to get an image taken
     * at the current device location
     * @param location Device location
     * @return Returns the completed URL as a String
     */
    private String buildUrl(Location location)
    {
        return ENDPOINT.buildUpon()
                .appendQueryParameter("method", SEARCH_METHOD)
                .appendQueryParameter("lat", "" + location.getLatitude())
                .appendQueryParameter("lon", "" + location.getLongitude())
                .build()
                .toString();
    }

    /**
     * Sifts through the JSON data and pulls out the information that we're interested in
     * @param galleryItems Array list of images to show in the gallery
     * @param jsonBody Raw JSON data to be parsed
     * @throws JSONException Throws a JSON exception if there's an issue with the JSON data
     */
    private void parseItems(List<GalleryItem> galleryItems, JSONObject jsonBody) throws JSONException
    {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int index = 0; index < photoJsonArray.length(); index++)
        {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(index);

            GalleryItem galleryItem = new GalleryItem();
            galleryItem.setId(photoJsonObject.getString("id"));
            galleryItem.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s"))
            {
                continue;
            }

            galleryItem.setUrl(photoJsonObject.getString("url_s"));
            galleryItem.setOwner(photoJsonObject.getString("owner"));
            galleryItem.setLatitude(photoJsonObject.getDouble("latitude"));
            galleryItem.setLongitude(photoJsonObject.getDouble("longitude"));
            galleryItems.add(galleryItem);
        }
    }
}