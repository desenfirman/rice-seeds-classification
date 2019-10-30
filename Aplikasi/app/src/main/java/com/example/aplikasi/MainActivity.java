package com.example.aplikasi;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private AlertDialog dialog;
    private TextView textInformation;
    private Button buttonClassify, buttonUploadImg, buttonLoadConfig, buttonLoadModel;
    int scale;
    private PanjangLebar hitungPanjangLebar;
    private Dilation hitungDilation;
    private String mCurrentPhotoPath;

    private Bitmap loadedImg;
    private Uri mModelUri, mConfigUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int LOAD_CONFIGS = 4;
    private static final int LOAD_MODEL = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureImageInitialization();

        hitungPanjangLebar = new PanjangLebar();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buttonUploadImg = (Button) findViewById(R.id.SelectImageBtn);
        buttonClassify = (Button) findViewById(R.id.ClassifyBtn);
        buttonLoadConfig = (Button) findViewById(R.id.LoadConfigsBtn);
        buttonLoadModel = (Button) findViewById(R.id.LoadModelBtn);
        mImageView = (ImageView) findViewById(R.id.ProfilePicIV);
        textInformation = (TextView)findViewById(R.id.textViewInformation);

        buttonLoadConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/octet-stream");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), LOAD_CONFIGS);
            }
        });

        buttonLoadModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("file/*.json");
                intent.setType("application/octet-stream");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                startActivityForResult(Intent.createChooser(intent, "Complete action using"), LOAD_MODEL);
            }
        });

        buttonUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        buttonClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyImage();
            }
        });

    }

    private void captureImageInitialization() {
        /**
         * a selector dialog to display two image source options, from camera
         * ‘Take from camera’ and from existing files ‘Select from gallery’
         */
        final String[] items = new String[] { "Take from camera", "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // pick from
                // camera
                if (item == 0) {
                    /**
                     * To take a photo from camera, pass intent action
                     * ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera app.
                     */
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    /**
                     * Also specify the Uri to save the image on specified path
                     * and file name. Note that this Uri variable also used by
                     * gallery app to hold the selected image path.
                     */
//                    File photofile = null;
//                    try {
//                        photofile = createImageFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    if (photofile != null){
//                        try {
//                            mImageCaptureUri = Uri.fromFile(createImageFile());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    try {
                        mImageCaptureUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID
                                + ".fileprovider", createImageFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("0return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    // pick from file
                    /**
                     * To select an image from existing files, use
                     * Intent.createChooser to open image chooser. Android will
                     * automatically display a list of supported applications,
                     * such as image gallery or file manager.
                     */
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

        dialog = builder.create();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);

            mOptions = options;

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);

                return convertView;
            }

            return null;
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    public void classifyImage(){
        if (mConfigUri == null || mModelUri == null || loadedImg == null){
            textInformation.setText("Error: Please check for all config, classifier model and image are already loaded.");
            return;
        }

        BufferedReader config_reader = null;
        try {
            config_reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(mConfigUri)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ImageObj imageObj = new ImageObj(loadedImg);
        imageObj.importConfiguration(config_reader);

        Map<String, Integer> w_h = imageObj.getObjectDimension();
        int width = w_h.get("width");
        int height = w_h.get("height");

        BufferedReader model_reader = null;
        try {
            model_reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(mModelUri)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        NaiveBayes nv = new NaiveBayes(model_reader);
        ArrayList<String> result = nv.predict(new ArrayList<Double>(Arrays.asList(((double) width), ((double) height))));

        textInformation.setText("Width: " + width + "Height: " + height +  "\nImage classified as " + result.get(0) + " with probability value " + result.get(1));



        int gaussFactor, gaussOffset, cannyThreshold;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                /**
                 * After taking a picture, do the crop
                 */
                doCrop();
                break;

            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                loadImage();
                break;
            case LOAD_CONFIGS:
                mConfigUri = data.getData();
                break;
            case LOAD_MODEL:
                mModelUri = data.getData();
                break;
        }

    }


    private void loadImage(){
        /**
         * After cropping the image, get the bitmap of the cropped image and
         * display it on imageview.
         */
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bmp != null) {
//            ImageObj imageObj = new ImageObj(bmp);
//            /*--------------------------*/
//            Bitmap grayscaleBitmap = Grayscale.grayScaleImage(photo);
//            Bitmap gaussianblurBitmap = new GaussianBlur(grayscaleBitmap).doGaussianBlur( 16, 0);
//            Bitmap dilationGrayscaleBmp = Dilation.grayscaleImage(gaussianblurBitmap);
//            Bitmap cannyBitmap = CannyEdgeDetector.process(dilationGrayscaleBmp, 50);
//            Bitmap dilationBinaryBmp = Dilation.binaryImage(cannyBitmap, true);
//                    Bitmap erosionBitmap = erosionBitmap(dilationBmp);

//            Bitmap finalBitmap = Bitmap.createBitmap(dilationBinaryBmp, photo.getWidth() * 1/7, photo.getHeight() * 1/7,  photo.getWidth() * 5/7, photo.getHeight() * 5/7);
            loadedImg = bmp;
            bmp = Bitmap.createBitmap(bmp, bmp.getWidth() * 1 / 5, bmp.getHeight() * 1 / 3, bmp.getWidth() * 3 / 5, bmp.getHeight() / 3);
            bmp = Bitmap.createScaledBitmap(bmp, ((int) (bmp.getWidth() * 0.25)), ((int) (bmp.getHeight() * 0.25)), false);
            bmp = Bitmap.createBitmap(bmp, bmp.getWidth() * 1 / 7, bmp.getHeight() * 1 / 7, bmp.getWidth() * 4 / 7, bmp.getHeight() * 5 / 7);


            /*---------------------------*/
            mImageView.setImageBitmap(bmp);
//            Log.d("PanjangLebar", "panjang sesudah crop -> "+finalBitmap.getWidth()+" lebar sesudah crop -> "+finalBitmap.getHeight());
        }

        File f = new File(mImageCaptureUri.getPath());
        /**
         * Delete the temporary image
         */
        if (f.exists())
            f.delete();
    }

    private void doCrop() {
        /**
         * Open image crop app by starting an intent
         * ‘com.android.camera.action.CROP‘.
         */

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
//        compress_ratio = 4
//        compress = bitmap.compress(compress_ratio / 4)
//        cropped = bitmap.crop(compress, 1/3, 1/7)
//
//        panjang, lebar = hitungpanjanlebar(cropped_process)
//
//        panjang * 3 * 4
//        lebar * 7 * 4

//        cropped = Bitmap.createBitmap(bmp, bmp.getWidth() / 3, bmp.getHeight() * 3 / 7,  bmp.getWidth() / 3, bmp.getHeight() * 1 / 7);
//        cropped = Bitmap.createScaledBitmap(cropped, bmp.getScaledWidth(25), bmp.getScaledHeight(25), false);

    }





    //dilate.java
    private Bitmap dilationBitmap (Bitmap srcBitmap) {
        return Dilation.binaryImage(srcBitmap, false);
    }


}

