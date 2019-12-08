package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView mUserListView;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_brown);
        setTitle("Users List");

        //get the reference(Table) from firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserListView = findViewById(R.id.usersl);
        mUserListView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Retrieving data from firebase
        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                        .setQuery(mDatabaseReference, UserModel.class)
                        .build();
        FirebaseRecyclerAdapter<UserModel, UsersViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserModel, UsersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, final int i, @NonNull UserModel userModel) {
                        //Displaying the username in recycler view
                        usersViewHolder.userName.setText(userModel.getName());
                        //clicking the row of recycler view, then execute a command on click listener
                        usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //getting the uniqid of the user selected
                                String other_uid = getRef(i).getKey();
                                //going to sendactivity page
                                Intent iToSendMsg = new Intent(v.getContext(), SendActivity.class);
                                //bringing the data to sendactivity page
                                iToSendMsg.putExtra("otheruid", other_uid);
                                startActivity(iToSendMsg);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_row, parent, false);
                        UsersViewHolder viewHolder = new UsersViewHolder(view);
                        return viewHolder;
                    }
                };
        mUserListView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_list_name);
        }
    }
}
