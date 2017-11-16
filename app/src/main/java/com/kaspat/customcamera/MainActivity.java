package com.kaspat.customcamera;

import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Camera mCamera;
    private HorizontalScrollView horizontalScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView ivCapture = (ImageView) findViewById(R.id.ivCapture);
        ImageView ivFilter = (ImageView) findViewById(R.id.ivFilter);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hscFilterLayout);

        mCamera = getCameraInstance();
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout rlCameraPreview = (FrameLayout) findViewById(R.id.rlCameraPreview);
        if (rlCameraPreview != null) {
            rlCameraPreview.addView(mPreview);
        }

        ivCapture.setOnClickListener(this);
        ivFilter.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    public void colorEffectFilter(View v){
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            switch (v.getId()) {
                case R.id.rlNone:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlAqua:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlBlackBoard:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlMono:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlNegative:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlPosterized:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlSepia:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlSolarized:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.rlWhiteBoard:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
                    mCamera.setParameters(parameters);
                    break;
            }
        }catch (Exception ex){
            Log.d(TAG,ex.getMessage());
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[] { pictureFile.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            mCamera.startPreview();
                        }
                    });
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ArshadPhotos");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                System.out.println("Directory not created");
                return null;
            }
        }

        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        return new File(mediaStorageDir.getAbsolutePath() + File.separator +
                "IMG_"+ num + ".jpg");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivFilter:
                if(horizontalScrollView.getVisibility() == View.VISIBLE) {
                    horizontalScrollView.setVisibility(View.GONE);
                } else {
                    horizontalScrollView.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.ivCapture:
                mCamera.takePicture(null,null,mPicture);
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
        mCamera = null;
    }
}
