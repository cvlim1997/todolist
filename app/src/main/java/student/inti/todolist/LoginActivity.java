package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    EditText lemail,lpw;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth=FirebaseAuth.getInstance();
        lemail=findViewById(R.id.email);
        lpw=findViewById(R.id.password);
        btnSignIn=findViewById(R.id.logbutton);
        tvSignUp=findViewById(R.id.textViewsu);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null)
                {
                    Toast.makeText(LoginActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this,Home.class);
                    startActivity(i);
                }
                else
                    {
                        Toast.makeText(LoginActivity.this,"Please login.",Toast.LENGTH_SHORT).show();
                    }
            }
        };
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=lemail.getText().toString();
                String pwd=lpw.getText().toString();
                if(email.isEmpty())
                {
                    lemail.setError("Please enter your email.");
                    lpw.requestFocus();
                }
                else if(pwd.isEmpty())
                {
                    lpw.setError("Please enter your password.");
                    lpw.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Please fill up accordingly.",Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty()))
                {
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this,"Log In failed, Please try again.",Toast.LENGTH_SHORT).show();
                            }
                            else
                                {
                                    Intent iToHome=new Intent(LoginActivity.this,Home.class);
                                    startActivity(iToHome);
                                }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Error occured.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iToSignUp = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(iToSignUp);
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
