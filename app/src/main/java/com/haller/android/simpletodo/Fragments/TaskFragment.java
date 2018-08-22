package com.haller.android.simpletodo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haller.android.simpletodo.R;
import com.haller.android.simpletodo.Utilities.Task;
import com.haller.android.simpletodo.Utilities.TaskListManager;
import com.haller.android.simpletodo.Views.TaskEditText;

public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";
    private static final String ARG_TASK = "task";

    private Task mTask;
    private TextView mDateTextView;
    private TaskEditText mTaskEditText;
    private TaskListManager mTaskListManager;
    private LinearLayout mLinearLayout;

    public static TaskFragment newInstance(Task task) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK, task);

        TaskFragment frag = new TaskFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTask = (Task) getArguments().getSerializable(ARG_TASK);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_fragment, container, false);

        mDateTextView = (TextView) view.findViewById(R.id.due_date_text_view);

        mTaskEditText = (TaskEditText) view.findViewById(R.id.task_edit_text);
        mTaskEditText.setText(mTask.getTaskDetails());
        mTaskEditText.setSelection(mTaskEditText.getText().length());
        mTaskEditText.setDefaultText(mTask.getTaskDetails());

        mTaskEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (mTaskEditText.hasText()) {
                        mTask.setTaskDetails(mTaskEditText.getText().toString());
                        mTaskListManager.updateTaskDetails(mTask);
                    }

                    mTaskEditText.resetState();
                    mTaskEditText.hideKeyboard(getActivity());
                    return true;
                } else {
                    return false;
                }
            }
        });

        mLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_date);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    public void setTaskListManager(TaskListManager taskListMangager) {
        mTaskListManager = taskListMangager;
    }
}
