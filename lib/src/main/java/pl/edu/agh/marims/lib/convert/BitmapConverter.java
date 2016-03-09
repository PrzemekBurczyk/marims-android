package pl.edu.agh.marims.lib.convert;

import android.graphics.Bitmap;

public abstract class BitmapConverter<TO> {
    public abstract TO convert(Bitmap bitmap);
}
