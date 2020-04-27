package fr.glog.aourir_infos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class fgtPassword extends AppCompatActivity {
    private EditText txtemail;
    private Button reset;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgt_password);

        setContentView(R.layout.activity_fgt_password);
        txtemail = findViewById(R.id.email);
        reset = findViewById(R.id.Reset);
        progressbar = findViewById(R.id.progressBarReset);

        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressbar.setVisibility(VISIBLE);
                String email = txtemail.getText().toString().trim();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            progressbar.setVisibility(GONE);

                            Toast.makeText(fgtPassword.this, "Reset send to your E-mail",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(fgtPassword.this, Login.class));
                        }
                        else{
                            Toast.makeText(fgtPassword.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }


                    }
                });

            }
        });

    }

    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
        finish();

    }
}
