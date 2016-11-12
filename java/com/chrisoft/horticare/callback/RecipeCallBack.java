package com.chrisoft.horticare.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Callback interface implemented by main activity used by recipe add/edit screen
 * Created by Dahye on 20/08/2015.
 */
public interface RecipeCallBack {
    /**
     * Call back to load the photo gallery
     * @param imageView target ImageView component
     * @return Bitmap
     */
    public void loadImageFromGallery(ImageView imageView);

    /**
     * Used to get the filename selected in image gallery
     * @return
     */
    public String getImageFileName();

    /**
     * Used to get the original bitmap selected in image gallery
     * @return
     */
    public Bitmap getSelectedBitmap();
}