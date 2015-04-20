package com.google.android.stardroid.activities;

import java.io.File;
import java.io.IOException;

import com.google.android.stardroid.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class CameraDemoActivity extends Activity {
int TAKE_PHOTO_CODE = 0;
public static int count=0;

String dir;
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.imagedisplay);

//here,we are making a folder named picFolder to store pics taken by the camera using this application
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
        File newdir = new File(dir); 
        newdir.mkdirs();
        
        click();

   
}

void click(){
	
    // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
    count++;
    String file = dir+count+".jpg";
    File newfile = new File(file);
    try {
        newfile.createNewFile();
    } catch (IOException e) {}       

    Uri outputFileUri = Uri.fromFile(newfile);

    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
    cameraIntent.putExtra("uri", outputFileUri);

    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
       // Log.d("CameraDemo", "Pic saved");
    	Toast.makeText(getApplicationContext(), "Snap clicked.", Toast.LENGTH_SHORT).show();
    	Intent i=new Intent(getApplicationContext(), DynamicStarMapActivity.class);
    	startActivity(i);

    }
}

}