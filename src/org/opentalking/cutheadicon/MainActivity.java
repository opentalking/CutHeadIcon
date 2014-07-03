package org.opentalking.cutheadicon;

import org.opentalking.cutheadicon.util.IconUtils;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private final static int REQEST_CODE_CAMERA = 100; //拍照
	private final static int REQEST_CODE_ALBUM = 101; //相册
	private final static int REQEST_CODE_CROP = 102;//剪裁结果
	private final static int REQEST_CODE_CROP_RESULT = 103;//剪裁结果
	
	private final static String HEAD_ICON_FILENAME = "tifen-head_icon.png";
	
	private ImageView imageView;
	private Button start_crop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation_bar_color));
		
		imageView = (ImageView)findViewById(R.id.imageView);
		Bitmap bitmapFromName = IconUtils.getBitmapFromName(this, HEAD_ICON_FILENAME);
		imageView.setImageBitmap(bitmapFromName);
		
		start_crop = (Button) findViewById(R.id.start_crop);
		start_crop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cropCamera(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			}
		});
	}
	
	public void cropCamera(Uri uri){
		  //call the standard crop action intent (the user device may not support it)
		Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
		    //indicate image type and Uri
		cropIntent.setDataAndType(uri, "image/*");
		    //set crop properties
		cropIntent.putExtra("crop", "true");
		    //indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		    //indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		    //retrieve data on return
		cropIntent.putExtra("return-data", true);
		    //start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, REQEST_CODE_CROP);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			switch (requestCode) {
			case REQEST_CODE_CAMERA:
				Log.d("onActivityResult", "REQEST_CODE_CAMERA");
				Uri pictureUri = IconUtils.resizeIcon(this, HEAD_ICON_FILENAME);;
				cropCamera(pictureUri);
				
//				Intent clipIntent = new Intent(this,ClipActivity.class);
//				clipIntent.putExtra("fileName", HEAD_ICON_FILENAME);
//				startActivityForResult(clipIntent,REQEST_CODE_CROP_RESULT);
				break;
			case REQEST_CODE_ALBUM:
				String filePath = null;
				if(data!=null){
					Uri originalUri = data.getData();
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null, null);
					if(cursor.moveToFirst()){
						do{  
							filePath= cursor.getString(cursor.getColumnIndex("_data"));
						}while (cursor.moveToNext());
					}
					
//					IconUtils.copyFile(this, filePath,HEAD_ICON_FILENAME);
//					
//					Intent albumIntent = new Intent(this, ClipActivity.class);
//					albumIntent.putExtra("fileName",HEAD_ICON_FILENAME);
//					startActivityForResult(albumIntent,REQEST_CODE_CROP_RESULT);
				}
				break;
			case REQEST_CODE_CROP:
				//get the returned data
				Bundle extras = data.getExtras();
				//get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				imageView.setImageBitmap(thePic);
				break;
			case REQEST_CODE_CROP_RESULT:
				
				
				
				Bitmap bitmapFromName = IconUtils.getBitmapFromName(this, HEAD_ICON_FILENAME);
				imageView.setImageBitmap(bitmapFromName);
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST+1, 1, "拍照").setIcon(R.drawable.camera).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, Menu.FIRST, 2, "相册").setIcon(R.drawable.file).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case Menu.FIRST:
			Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			albumIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(albumIntent,REQEST_CODE_ALBUM);
			break;
		case Menu.FIRST+1:
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri pictureUri = Uri.fromFile(IconUtils.getFileByName(this, HEAD_ICON_FILENAME));
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
			startActivityForResult(cameraIntent,REQEST_CODE_CAMERA);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
