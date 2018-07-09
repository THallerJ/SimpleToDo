package com.halller.android.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private static final String TAG = "TaskAdapter";
    private Context mContext;
    private List<Task> mTaskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        mContext = context;
        mTaskList = taskList;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskAdapter.TaskHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TaskHolder holder) {
        holder.mItemCheckBox.setVisibility(View.VISIBLE);
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskHolder holder, int position) {
        Log.d(TAG, "{onBindViewHolder: called ");
        Task item = mTaskList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class TaskHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private Task mTask;
        private CheckBox mItemCheckBox;
        private TextView mItemTextView;
        private boolean onBind;

        public TaskHolder(View itemView) {
            super(itemView);
            mItemCheckBox = (CheckBox) itemView.findViewById(R.id.task_item_check_box);
            mItemTextView = (TextView) itemView.findViewById(R.id.task_item_text_view);
            mItemCheckBox.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = TaskActivity.newIntent(mContext, mTask.getId());
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(Task item) {
            mTask = item;
            mItemTextView.setText(mTask.getItemDetails());
            onBind = true;
            mItemCheckBox.setChecked(mTask.isCompleted());
            onBind = false;
        }


        public void removeItem(int position) {
            mTaskList.remove(position);
            notifyItemRangeChanged(position, mTaskList.size());

            // without this line in conjunction with the overloaded detach method, the checkbox leaves a dark circle when check
            mItemCheckBox.setVisibility(View.INVISIBLE);
            notifyItemRemoved(position);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!onBind) {
                removeItem(getAdapterPosition());
                mTask.setCompleted(true);
                }
        }
    }
}
