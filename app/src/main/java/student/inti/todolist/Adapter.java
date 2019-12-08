package student.inti.todolist;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Adapter extends FirebaseRecyclerAdapter<Model,Adapter.ListHolder>{


    public Adapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListHolder listHolder, int i, @NonNull Model model) {
        listHolder.mTitleView.setText(model.getTitle());
        listHolder.mMessageView.setText(model.getMessage());
        listHolder.mDateView.setText(model.getDate());
        listHolder.mPriority.setText(model.getPriority());
        listHolder.mTime.setText(model.getTime());
        listHolder.mStatus.setText(model.getStatus());
        listHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ListHolder(v);
    }

    public void deleteItem(int position)
    {
        //getting the data of the position selected in recyclerview
        //delete data
        getSnapshots().getSnapshot(position).getRef().setValue(null);
    }

    class ListHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnCreateContextMenuListener
    {
        TextView mTitleView, mMessageView, mDateView, mPriority, mTime, mStatus;

        ////////////////////////////////////
        private ItemClickListener itemClickListener;
        ///////////////

        public ListHolder(View itemView)
        {
            super(itemView);
            mTitleView=itemView.findViewById(R.id.rTitle);
            mMessageView=itemView.findViewById(R.id.rMessage);
            mDateView=itemView.findViewById(R.id.rDate);
            mPriority=itemView.findViewById(R.id.rPriority);
            mTime=itemView.findViewById(R.id.rTime);
            mStatus=itemView.findViewById(R.id.rStatus);
            ////////////////////
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);

        }
        //////////////////////////

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0,0,getAdapterPosition(), Common.UPDATE);
            menu.add(0,1,getAdapterPosition(),Common.DONE);
        }
        /////////////////////////////
    }
}
