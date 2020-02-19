package com.example.notepad.util;

import android.graphics.Bitmap;

public class BitmapResizeUtils {

    public static Bitmap resizeBitmap(Bitmap original, int newWidth) {

        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (newWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(original, newWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }
}
