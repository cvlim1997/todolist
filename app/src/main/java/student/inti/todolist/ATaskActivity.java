package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ATaskActivity extends AppCompatActivity {

    private ColorDrawable background = new ColorDrawable(Color.RED);
    private RecyclerView mAssignedList;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private aTaskAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atask);
        String uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Assigned Tasks");



        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        setTitle("Assigned Tasks");

        loadTaskLists();
    }

    private void loadTaskLists() {
        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(mDatabaseReference,TaskModel.class).build();
        mAdapter=new aTaskAdapter(options);
        mAssignedList=findViewById(R.id.orecyclerView);
        mAssignedList.setHasFixedSize(true);
        mAssignedList.setLayoutManager(new LinearLayoutManager(this));
        mAssignedList.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                viewHolder.getAdapterPosition();
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.deleteItem(viewHolder.getAdapterPosition());
                                Toast.makeText(ATaskActivity.this, "Task deleted.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;

                if (dX > 0) { // Swiping to the right
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                            itemView.getBottom());

                } else if (dX < 0) { // Swiping to the left
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
            }
        }).attachToRecyclerView(mAssignedList);
        mAdapter.setOnItemClickListener(new aTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                    TaskModel tm=dataSnapshot.getValue(TaskModel.class);
                    //getting value in variables
                    String id=dataSnapshot.getKey();
                    String title=tm.getTitle();
                    String message=tm.getMessage();
                    String priority=tm.getPriority();
                    String date=tm.getDate();
                    String time=tm.getTime();
                    String assignby=tm.getAssignedby();

                    Intent iToDetail=new Intent(ATaskActivity.this,DetailsActivity.class);
                    //intent put extra to next activity
                    iToDetail.putExtra("ttl",title);
                    iToDetail.putExtra("msg",message);
                    iToDetail.putExtra("dt",date);
                    iToDetail.putExtra("pty",priority);
                    iToDetail.putExtra("tm",time);
                    iToDetail.putExtra("assign",assignby);
                    //start new activity
                    startActivity(iToDetail);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mAdapter.startListening();
    }
}
