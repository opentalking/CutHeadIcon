package org.opentalking.cutheadicon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

/**
 * just use to control icon,because all resource save in package cache
 * 
 * @author haoxiqiang
 * 
 */

public class IconUtils {

	// get icon by name
	public static File getFileByName(Context context, String iconName) {
		File iconFile = new File(
				context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
				iconName);
		return iconFile;
	}

	// save bitmap
	public static boolean saveIconBitmap(Context context, String fileName,
			Bitmap iconBitmap) {
		File saveFile = getFileByName(context, fileName);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(saveFile);
			iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
			Log.d("saveIconBitmap", "save fileName success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	//copy file to the same path
	public static boolean copyFile(Context context, String oldPath,
			String newFileName) {
		try {
			File newfile = getFileByName(context, newFileName);
			File oldfile = new File(oldPath);
			Log.d("copyFile", "oldfile is " + oldfile.getPath());
			Log.d("copyFile", "newfile is " + newfile.getPath());
			if (newfile.exists()) {

				FileInputStream fis = new FileInputStream(oldfile);
				FileOutputStream fos = new FileOutputStream(newfile.getPath());

				byte[] buffer = new byte[1024];
				int length;
				while ((length = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				fis.close();
				fos.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	//clip this bitmap to a circle bitmap
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);

		int halfWidth = bitmap.getWidth() / 2;
		int halfHeight = bitmap.getHeight() / 2;

		canvas.drawCircle(halfWidth, halfHeight,
				Math.max(halfWidth, halfHeight), paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	//get a bitmap by name
	public static Bitmap getBitmapFromName(Context context, String pictureName) {
		
		File fileByName = getFileByName(context,pictureName);
		
		Bitmap decodeFile = 	BitmapFactory.decodeFile(fileByName.getAbsolutePath());
		
		if (decodeFile != null) {
			return decodeFile;
		} else {
			return null;
		}
	}
	//get a bitmap by name
	public static Bitmap getRawBitmapFromName(Context context, String pictureName) {
		
		File fileByName = getFileByName(context,pictureName);
		
		Bitmap decodeFile = 	autoRotation(fileByName.getAbsolutePath());
		
		if (decodeFile != null) {
			return decodeFile;
		} else {
			return null;
		}
	}
	
	public static Bitmap autoRotation(String path){
		
		Bitmap realImage = BitmapFactory.decodeFile(path);

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
			return realImage;
		}
		
		int exif_orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		Log.d("autoRotation", "exif_orientation vaule is "+exif_orientation);
		switch (exif_orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			realImage=rotate(realImage, 90);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			realImage=rotate(realImage, 180);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			realImage=rotate(realImage, 270);
			break;
		}
		return realImage;
	}
	
	public static Bitmap rotate(Bitmap bitmap, int degree) {
	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();
	    Matrix mtx = new Matrix();
	    mtx.postRotate(degree);
	    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	public static void resizeIcon(Context context,String filename) {
		String path = getFileByName(context, filename).getAbsolutePath();
		saveIconBitmap(context,filename,autoRotation(path));
	}
}
