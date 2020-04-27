package fr.glog.aourir_infos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fr.glog.aourir_infos.model.User;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class sign_up extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private EditText txtusername, txtemail, txtpassword, txtconfirmpassword;
    private Button signup;
    private ProgressBar progressbar;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtusername=(EditText)findViewById(R.id.username);
        txtemail=(EditText)findViewById(R.id.email);
        txtpassword =(EditText)findViewById(R.id.password);
        txtconfirmpassword = (EditText)findViewById(R.id.password2);
        signup = (Button)findViewById(R.id.signup);
        progressbar = findViewById(R.id.progressBarsignup);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = txtusername.getText().toString();
                final String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();
                String confirmpassword = txtconfirmpassword.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(sign_up.this, "Please enter your username",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(sign_up.this, "Please enter E-mail adress",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(sign_up.this, "Please enter password",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmpassword)){
                    Toast.makeText(sign_up.this, "Please enter confirmation password",Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length()<6){
                    Toast.makeText(sign_up.this, "Password too short",Toast.LENGTH_LONG).show();
                    return;
                }
                // progressbar.setVisibility(View. VISIBLE);

                if(password.equals(confirmpassword))
                {
                    progressbar.setVisibility(VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    String userId = mAuth.getCurrentUser().getUid();
                                                    User user = new User(userId,username,email,"default", "Offline", username.toLowerCase(), "No", "False");

                                                    mDatabase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressbar.setVisibility(GONE);

                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "Email sent.");
                                                                startActivity(new Intent(sign_up.this, Login.class));
                                                                Toast.makeText(sign_up.this, "Registred successfully, Please check your E-mail for verification",Toast.LENGTH_LONG).show();

                                                            }else{
                                                                Toast.makeText(sign_up.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                                            }

                                                        }
                                                    });

                                                } else{
                                                    Toast.makeText(sign_up.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                                    } else {
                                        progressbar.setVisibility(GONE);
                                        Toast.makeText(sign_up.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                }
            }
        });

    }

    public void onBackPressed()
    {
        finish();
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);

    }
}
