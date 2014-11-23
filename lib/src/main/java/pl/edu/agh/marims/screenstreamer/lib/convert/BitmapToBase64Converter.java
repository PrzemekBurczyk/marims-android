package pl.edu.agh.marims.screenstreamer.lib.convert;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapToBase64Converter extends BitmapConverter<String> {

    @Override
    public String convert(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            float scale = 0.7f;
            int quality = 50;
            Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), false).compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] byteBuffer = baos.toByteArray();

            return Base64.encodeToString(byteBuffer, Base64.NO_WRAP);
        } else {
            return null;
        }
    }
}
