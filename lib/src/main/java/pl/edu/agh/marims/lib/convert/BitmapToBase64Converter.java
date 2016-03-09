package pl.edu.agh.marims.lib.convert;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapToBase64Converter extends BitmapConverter<String> {

    private static final float MAX_DIMENSION = 700.0f;

    @Override
    public String convert(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            float scale;
            if (bitmap.getWidth() < bitmap.getHeight()) {
                scale = MAX_DIMENSION / (float) bitmap.getHeight();
            } else {
                scale = MAX_DIMENSION / (float) bitmap.getWidth();
            }
            int quality = 50;
            Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), false).compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] byteBuffer = baos.toByteArray();

            return Base64.encodeToString(byteBuffer, Base64.NO_WRAP);
        } else {
            return null;
        }
    }
}
