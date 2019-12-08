package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText emailId,password,name;
    Button btnSignup;
    TextView tvSignin;
    FirebaseAuth mFirebaseAuth;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId=findViewById(R.id.regem);
        password=findViewById(R.id.regpw);
        btnSignup=findViewById(R.id.signupbut);
        tvSignin=findViewById(R.id.textView);
        name=findViewById(R.id.name);
        mProgressBar=findViewById(R.id.progressBar);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailId.getText().toString();
                String pwd=password.getText().toString();
                final String nm=name.getText().toString();

                if(nm.isEmpty())
                {
                    name.setError("Please enter your name.");
                    name.requestFocus();
                }
                else if(email.isEmpty())
                {
                    emailId.setError("Please enter your email.");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty())
                {
                    password.setError("Please enter your password.");
                    password.requestFocus();
                }
                else if(nm.isEmpty() || email.isEmpty() || pwd.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please fill up accordingly.",Toast.LENGTH_SHORT).show();
                }
                else if(!(nm.isEmpty() && email.isEmpty() && pwd.isEmpty()))
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(RegisterActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful())
                                    {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this,"Sign up failed, Please try again.",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        {
                                            String user_id=mFirebaseAuth.getCurrentUser().getUid();
                                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                            Map newPost = new HashMap();
                                            newPost.put("name",nm);
                                            current_user_db.setValue(newPost);
                                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            finish();
                                        }
                                }
                            });
                }
                else
                    {
                        Toast.makeText(RegisterActivity.this,"Error occured.",Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
            }
        });
        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                mProgressBar.setVisibility(View.INVISIBLE);
                finish();
            }
        });
    }
}
