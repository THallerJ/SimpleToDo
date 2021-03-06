package com.haller.android.simpletodo.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";
    private static final String ARG_TASK = "task";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;

    private Task mTask;
    private TextView mDateTextView;
    private TaskEditText mTaskEditText;
    private TaskEditText mNoteEditText;
    private TaskListManager mTaskListManager;
    private LinearLayout mDateButton;
    private LinearLayout mButtonPanel;
    private LinearLayout mDiscardButton;
    private LinearLayout mDoneButton;

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

        if (!mTask.hasDueDate()) {
            mDateTextView.setText(R.string.no_date);
        } else {
            mDateTextView.setText(mTask.getDueDate());
        }

        mNoteEditText = (TaskEditText) view.findViewById(R.id.task_note_text);
        mNoteEditText.setClearOnBack(false);

        mNoteEditText.setText(mTask.getNote());

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
                    TaskEditText.hideKeyboard(getActivity());
                    return true;
                } else {
                    return false;
                }
            }
        });

        mDateButton = (LinearLayout) view.findViewById(R.id.linear_layout_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    DatePickerFragment datePicker = new DatePickerFragment();
                    datePicker.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                    datePicker.show(getFragmentManager(), DIALOG_DATE);
                }
            }
        });

        mButtonPanel = (LinearLayout) view.findViewById(R.id.button_panel);

        mDiscardButton = (LinearLayout) view.findViewById(R.id.discard_button);
        mDiscardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    mTask.setNote(null);
                    mTaskListManager.updateNote(mTask);

                    mNoteEditText.setText("");
                    mNoteEditText.clearFocus();
                    TaskEditText.hideKeyboard(getActivity());
                }
            }
        });

        mDoneButton = (LinearLayout) view.findViewById(R.id.done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    if (mNoteEditText.hasText()) {
                        mTask.setNote(mNoteEditText.getText().toString());
                        mTaskListManager.updateNote(mTask);
                    } else {
                        mNoteEditText.setText("");
                    }

                    mNoteEditText.clearFocus();
                    TaskEditText.hideKeyboard(getActivity());
                }
            }
        });

        mNoteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mButtonPanel.setVisibility(View.VISIBLE);
                } else {
                    mButtonPanel.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            String dateString = getDateString(date, "MMM d, yyyy");

            Calendar cal = Calendar.getInstance();
            cal.set(0, 0, 0);
            String nullDateString = getDateString(cal.getTime(), "MMM d, yyyy");

            if (nullDateString.equals(dateString)) {
                mTask.setDueDate(null);
                mDateTextView.setText(R.string.no_date);
            } else {
                mTask.setDueDate(dateString);
                mDateTextView.setText(dateString);
            }

            mTaskListManager.updateDueDate(mTask);
        }
    }

    public String getDateString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public void setTaskListManager(TaskListManager taskListManager) {
        mTaskListManager = taskListManager;
    }
}

