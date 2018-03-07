package com.mobiledi.earnit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.mobiledi.earnit.AppLockConstants;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.interfaces.ImageSelection;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.ScalingUtilities;
import com.mobiledi.earnit.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by mobile-di on 12/8/17.
 */

public class UploadRuntimePermission extends Activity {
    Button selectButton;
    public String UrlOfImage;

    private static final String MY_PICTURE_BUCKET = "mybucket";
    SparseIntArray sparseIntArray;
    public final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    public final int SELECTED_PICTURE = 2;
    public boolean cameraStatus = false;
    public boolean rLibraryStatus = false;
    public boolean wLibraryStatus = false;
    public String gFileName = null;
    public Intent in = null;
    Bundle savedInstanceState;
    public final String TAG = "UploadRuntimePermission";
    Uri uri;
    Intent CropIntent;
    public final int CROP_IMAGE = 3;
    public static UploadRuntimePermission runtimePermission;
    ImageView uploadView;
    public UploadRuntimePermission getRuntimePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        sparseIntArray = new SparseIntArray();
        runtimePermission = this;
    }

    public static UploadRuntimePermission getInstance() {
        return runtimePermission;
    }

    public void requestRequiredApplicationPermission(final String[] requestedPermissions, final int stringId, final int requestCode) {
        sparseIntArray.put(requestCode, stringId);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean showRequestPermission = false;
        for (String permission : requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            showRequestPermission = showRequestPermission || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (!showRequestPermission) {
                ActivityCompat.requestPermissions(UploadRuntimePermission.this, requestedPermissions, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck) {
        } else {
        }
    }

    public void showToast(String message) {
        Utils.showToast(UploadRuntimePermission.this, message);
    }


    public void lockScreen() {
        Utils.lockScreen(getWindow());
    }


    public void unLockScreen() {
        Utils.unLockScreen(getWindow());
    }

    public void josnError(JSONObject message) {
        Utils.JsonError(message, runtimePermission);
    }


    public void vRuntimePermission(ImageView view) {
        uploadView = view;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            rLibraryStatus = true;
        } else {
            rLibraryStatus = false;
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            wLibraryStatus = true;
        } else {
            wLibraryStatus = false;
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            cameraStatus = true;
        } else {
            cameraStatus = false;
        }
    }

    public void selectImage() {
        final CharSequence[] items = {AppConstant.GALLERY, AppConstant.LIBRARY, AppConstant.EXIT};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        SpannableStringBuilder dTitle = new SpannableStringBuilder();
        dTitle.append(AppConstant.VIA);
        dTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(runtimePermission, R.color.pink)), 0, 9, 0);
        builder.setTitle(dTitle);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(AppConstant.GALLERY)) {
                    if (cameraStatus && rLibraryStatus && wLibraryStatus)
                        fromCamera();
                    else {
                        showToast("Camera permission required");
                    }

                } else if (items[item].equals(AppConstant.LIBRARY)) {
                    if (rLibraryStatus && wLibraryStatus)
                        fromGallery();
                    else {
                        showToast("Storage permission required");
                    }

                } else if (items[item].equals(AppConstant.EXIT)) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

    }

    public void fromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "newTest.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, 1);
    }


    public void fromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTED_PICTURE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File newTest : f.listFiles()) {
                    if (newTest.getName().equals("newTest.jpg")) {
                        f = newTest;
                        break;
                    }
                }
                try {
                    Bitmap bitmap = null;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    try {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                        Log.e(TAG, "Bitmap: "+bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                   // AppConstant.bitmap = bitmap;
                  //  AppConstant.isImageCaptured = true;
                    uploadView.setImageBitmap(bitmap);

                    File path = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    f.delete();
                    OutputStream outFile = null;
                    final File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final String pat = getRealPathFromURI(Uri.fromFile(file).toString());
                    Log.e("PRINTPATH", pat);

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {

                                uploadImageToAWS(pat);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error: "+e.getLocalizedMessage());
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                final String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                uploadView.setImageBitmap(thumbnail);
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            uploadImageToAWS(picturePath);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        }
    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 4);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            File file = new File(Environment.getExternalStorageDirectory(),
                    "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");


            uri = Uri.fromFile(file);
            Log.e("tesitng file patrh", "" + uri.getPath());
            CropIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {

        }
    }
    //Image Crop Code End Here

    public String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    private void uploadImageToAWS(String selectedImagePath) {

        if (selectedImagePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();

// to make sure that file is not emapty or null
            return;
        }

        File file = new File(selectedImagePath);

        AmazonS3 s3Client = null;

        if (s3Client == null) {

            ClientConfiguration clientConfig = new ClientConfiguration();

            clientConfig.setProtocol(Protocol.HTTP);

            clientConfig.setMaxErrorRetry(0);

            clientConfig.setSocketTimeout(60000);

            BasicAWSCredentials credentials = new BasicAWSCredentials(AppConstant.ACCESS_KEY_KEY, AppConstant.SECRET_ACCESS_KEY);

            s3Client = new AmazonS3Client(credentials, clientConfig);

            s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
        }

        FileInputStream stream = null;

        try {

            stream = new FileInputStream(file);

            ObjectMetadata objectMetadata = new ObjectMetadata();

            Log.e("messge", "converting to bytes");

            objectMetadata.setContentLength(file.length());

            String[] s = selectedImagePath.split("\\.");

            String extenstion = s[s.length - 1];

            Log.e("messge", "set content length : " + file.length() + "sss" + extenstion);

            String fileName = UUID.randomUUID().toString();

            PutObjectRequest putObjectRequest = new PutObjectRequest(AppConstant.BUCKET_NAME, "new/" + fileName + "." + extenstion, stream, objectMetadata)

                    .withCannedAcl(CannedAccessControlList.PublicRead);
// above line is  making the request to the aws  server for the specific place to upload the image were aws_bucket is the main folder  name and inside that is the profiles folder and there the file will be get uploaded
            UrlOfImage = fileName + "." + extenstion;

            PutObjectResult result = s3Client.putObject(putObjectRequest);

// this will  add the image to the specified path in the aws bucket.

            if (result == null) {

                Log.e("RESULT", "NULL");

            } else {

                Log.e("RESULT", result.toString());

            }

        } catch (Exception e) {

            Log.d("ERRORR", " " + e.getMessage());

            e.printStackTrace();

//            Log.e("ERROR",e.getMessage());

        }

    }


    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }

    }
}


