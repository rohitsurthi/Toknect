package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;

import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSettingsActivity extends AppCompatActivity {

    private DatabaseReference userDatabase,userOnlineRef;
    private FirebaseUser currentUser;
    private CircleImageView userImageView;
    private TextView userName , userStatus;
    private Button editImageBtn , editStatusBtn;

    private ProgressDialog progressDialog;

    private static final int GALLERY_PICK = 1;

    private StorageReference imageStorage;

    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        userImageView = findViewById(R.id.setting_image);
        userName = findViewById(R.id.setting_username);
        userStatus = findViewById(R.id.setting_user_status);
        editImageBtn = findViewById(R.id.edit_image_btn);
        editStatusBtn = findViewById(R.id.edit_status_btn);



        imageStorage = FirebaseStorage.getInstance().getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        String current_uid = currentUser.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        //offline enabled
        userDatabase.keepSynced(true);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumbs").getValue().toString();

                userName.setText(name);
                userStatus.setText(status);

                if(!image.equals("default")){
                    Picasso.with(AccountSettingsActivity.this).load(thumb_image).placeholder(R.drawable.user_default_circle_profile_2).into(userImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        editStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = userStatus.getText().toString();

                Intent statusIntent = new Intent(AccountSettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("status_value",status_value);
                startActivity(statusIntent);
            }
        });

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(AccountSettingsActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report){
                        if(report.areAllPermissionsGranted()){
                            selectImage();
                        }else{
                            Toast.makeText(AccountSettingsActivity.this,"please allow permissions",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();

            }
        });
    }

    private void selectImage(){

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("please wait..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri photoUri = result.getUri();
                String currentUserUid = currentUser.getUid();

                // Compressing the photo with compressor
                File thumb_file = new File(photoUri.getPath());
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(72)
                        .compressToBitmap(thumb_file);


                ByteArrayOutputStream imageBits = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,imageBits);
                final byte[] thumb_byte = imageBits.toByteArray();


                imageStorage = FirebaseStorage.getInstance().getReference();

                final StorageReference filePath = imageStorage.child("profiles").child(currentUserUid+".jpg");
                final StorageReference thumb_filePath = imageStorage.child("thumbs").child(currentUserUid+".jpg");

                filePath.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            progressDialog.dismiss();

                            //String downloadUrl = filePath.getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    if (task.isSuccessful()){

                                        //getting the profile download URL
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String downloadUrl = uri.toString();
                                                userDatabase.child("image").setValue(downloadUrl);
                                            }
                                        });

                                        //getting the thumbnail url
                                        thumb_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String thumbNailDownloadUrl = uri.toString();
                                                userDatabase.child("thumbs").setValue(thumbNailDownloadUrl);
                                                Toast.makeText(AccountSettingsActivity.this,"Thumbnail uploaded and url linl:"+thumbNailDownloadUrl,Toast.LENGTH_SHORT).show();


                                            }
                                        });

                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(AccountSettingsActivity.this,"error uploading thumbnail",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            Toast.makeText(AccountSettingsActivity.this,"Upload success",Toast.LENGTH_SHORT).show();

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(AccountSettingsActivity.this,"error uploading profile",Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AccountSettingsActivity.this,"something went wrong!",Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public static String random(){
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(10);
//        char tempChar;
//
//        for(int i=0;i<randomLength;i++){
//
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//
//    }


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null){
            userOnlineRef.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null){
            userOnlineRef.child("online").setValue(false);
        }

    }
}
