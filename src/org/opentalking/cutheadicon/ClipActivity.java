package org.opentalking.cutheadicon;


import org.opentalking.cutheadicon.util.IconUtils;
import org.opentalking.cutheadicon.view.ClipImageView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ClipActivity extends Activity {

	private ClipImageView mClipImageView;
	private String fileName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip);
		mClipImageView = (ClipImageView) findViewById(R.id.ready_clip);
		// 设置需要裁剪的图片
		fileName = this.getIntent().getStringExtra("fileName");
		Bitmap resBitmap = IconUtils.getRawBitmapFromName(this, fileName);
		if(resBitmap!=null){
//			mClipImageView.setBackgroundDrawable(new BitmapDrawable(getResources(), resBitmap));
			mClipImageView.setImageBitmap(resBitmap);
		}else{
			Toast.makeText(this, "头像生成失败了", Toast.LENGTH_SHORT).show();
		}
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation_bar_color));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST, 1, "保存").setIcon(R.drawable.file).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case Menu.FIRST:
			
			Bitmap clipImage = mClipImageView.clip();
//			Bitmap clipImage = mClipImageView.getClipImage();
			
			IconUtils.saveIconBitmap(this, fileName, clipImage);
			
			setResult(RESULT_OK);
			
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
