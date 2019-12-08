package student.inti.todolist;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public class aTaskAdapter extends FirebaseRecyclerAdapter<TaskModel,aTaskAdapter.TasksViewHolder> {

    private OnItemClickListener listener;

    public aTaskAdapter(@NonNull FirebaseRecyclerOptions<TaskModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TasksViewHolder tasksViewHolder, final int i, @NonNull TaskModel taskModel) {
        tasksViewHolder.mTitleView.setText(taskModel.getTitle());
        tasksViewHolder.mMessageView.setText(taskModel.getMessage());
        tasksViewHolder.mDateView.setText(taskModel.getDate());
        tasksViewHolder.mPriority.setText(taskModel.getPriority());
        tasksViewHolder.mTime.setText(taskModel.getTime());
        tasksViewHolder.mAssign.setText("Assigned By: "+taskModel.getAssignedby());
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_task_row,parent,false);
        return new TasksViewHolder(v);
    }
    public void deleteItem(int position)
    {
        getSnapshots().getSnapshot(position).getRef().setValue(null);
    }

    class TasksViewHolder extends RecyclerView.ViewHolder
    {
        TextView mTitleView, mMessageView, mDateView, mPriority, mTime, mAssign;
        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleView=itemView.findViewById(R.id.oTitle);
            mMessageView=itemView.findViewById(R.id.oMessage);
            mDateView=itemView.findViewById(R.id.oDate);
            mPriority=itemView.findViewById(R.id.oPriority);
            mTime=itemView.findViewById(R.id.oTime);
            mAssign=itemView.findViewById(R.id.oAssigner);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION&&listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DataSnapshot dataSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
