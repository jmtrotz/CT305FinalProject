package com.jeff.locatr;

/**
 * Class for the GalleryItem object used in this application
 * @author Jeffrey Trotz
 * @version 1.0
 * Date: 5/1/18
 */
public class GalleryItem
{
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;
    private double mLatitude;
    private double mLongitude;

    /**
     * Setter for mCaption
     * @param caption Image caption
     */
    public void setCaption(String caption)
    {
        mCaption = caption;
    }

    /**
     * Getter for mCaption
     * @return Returns mCaption as a String
     */
    public String getCaption()
    {
        return mCaption;
    }

    /**
     * Setter for mId
     * @param id Image ID
     */
    public void setId(String id)
    {
        mId = id;
    }

    /**
     * Getter for mId
     * @return Returns mID as a String
     */
    public String getId()
    {
        return mId;
    }

    /**
     * Setter for mUrl
     * @param url Image URL
     */
    public void setUrl(String url)
    {
        mUrl = url;
    }

    /**
     * Getter for mURL
     * @return Returns mUrl as a String
     */
    public String getUrl()
    {
        return mUrl;
    }

    /**
     * Setter for mOwner
     * @param owner Image owner
     */
    public void setOwner(String owner)
    {
        mOwner = owner;
    }

    /**
     * Getter for mOwner
     * @return Returns mOwner as a String
     */
    public String getOwner()
    {
        return mOwner;
    }

    /**
     * Setter for mLatitude
     * @param latitude Latitude value of the device's location
     */
    public void setLatitude(double latitude)
    {
        mLatitude = latitude;
    }

    /**
     * Getter for mLatitude
     * @return Returns mLatitude as a double
     */
    public double getLatitude()
    {
        return mLatitude;
    }

    /**
     * Setter for mLongitude
     * @param longitude Longitude value of the device's location
     */
    public void setLongitude(double longitude)
    {
        mLongitude = longitude;
    }

    /**
     * Getter for mLogitude
     * @return Returns mLogitude as a double
     */
    public double getLongitude()
    {
        return mLongitude;
    }
}