package fr.glog.aourir_infos.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.glog.aourir_infos.R;
import fr.glog.aourir_infos.model.User;


import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class profileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private ProgressBar progressbar;
    StorageReference storageReference;

    String userId;
    CircleImageView imageProfile;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView emailProfile = root.findViewById(R.id.Email_profile);
        final TextView usernameProfile = root.findViewById(R.id.username_profile);
        final  Button confirm = root.findViewById(R.id.confirmSets);

        final TextView txtNewUsername = root.findViewById(R.id.setUsername_profil);
        final TextView txtNewPassword = root.findViewById(R.id.setPassword_profile);
        final TextView txtNewPassword2 = root.findViewById(R.id.setPassword_profile_cofirmation);
        final TextView txtOldPassword = root.findViewById(R.id.oldPassword_profile);
        imageProfile = root.findViewById(R.id.avatar_profile);
        progressbar = root.findViewById(R.id.progressBarProfile);
        progressbar.setVisibility(VISIBLE);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getActivity() == null) {
                    return;
                }
                if(isAdded()) {
                    emailProfile.setText(dataSnapshot.child("email").getValue().toString());
                    usernameProfile.setText(dataSnapshot.child("username").getValue().toString());

                    String imageUrl = dataSnapshot.child("ImageURL").getValue().toString();
                    if (imageUrl.equals("default")) {
                        imageProfile.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getActivity()).load(imageUrl).into(imageProfile);
                    }
                }

                progressbar.setVisibility(GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String newUsername= txtNewUsername.getText().toString();
                final String newPassword = txtNewPassword.getText().toString().trim();
                final String newPassword2 = txtNewPassword2.getText().toString().trim();
                final String oldPassword = txtOldPassword.getText().toString().trim();

                if(firebaseUser!=null && firebaseUser.getEmail()!= null && !oldPassword.isEmpty()){
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),oldPassword);
                    progressbar.setVisibility(VISIBLE);


                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                if(!TextUtils.isEmpty(newUsername) && newUsername.length()>3){

                                    mDatabase.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            HashMap<String , Object> hashMap = new HashMap<>();
                                            hashMap.put("username" ,newUsername);
                                            dataSnapshot.getRef().updateChildren(hashMap);
                                            Toast.makeText(getActivity(), "Username is update",Toast.LENGTH_LONG).show();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                                if(!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newPassword2) && !TextUtils.isEmpty(oldPassword) ) {
                                    if (newPassword.equals(newPassword2) && newPassword.length()>=6) {
                                        progressbar.setVisibility(GONE);


                                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getActivity(), "password is update", Toast.LENGTH_LONG).show();


                                                }else{
                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getActivity(), "The password do not correspond or is too short", Toast.LENGTH_LONG).show();
                                        return;

                                    }
                                }else{
                                    if(newUsername.isEmpty()) {
                                        progressbar.setVisibility(GONE);
                                        Toast.makeText(getActivity(), "Please enter all the fields", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                }

                            }else{
                                progressbar.setVisibility(GONE);
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }else{
                    progressbar.setVisibility(GONE);
                    Toast.makeText(getActivity(), " Please make sure that all the specifics fields are filled out properly", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });


        return root;
    }

    private void openImage() {

        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }

    private  void uploadImage(){
        final ProgressDialog pd  = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri!=null){
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri> () {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("ImageURL", mUri);
                        mDatabase.updateChildren(hashMap);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(),"Failed", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(),"No image seected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_REQUEST && resultCode== RESULT_OK && data != null && data.getData()!= null){
            imageUri = data.getData();

            if (uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Upload in progress", Toast.LENGTH_LONG).show();
            }else {
                uploadImage();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glide.with(getContext()).pauseRequests();

    }
}
