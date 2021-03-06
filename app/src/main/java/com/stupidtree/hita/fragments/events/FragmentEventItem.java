package com.stupidtree.hita.fragments.events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.stupidtree.hita.R;
import com.stupidtree.hita.fragments.BaseFragment;
import com.stupidtree.hita.fragments.BaseOperationTask;
import com.stupidtree.hita.timetable.TimetableCore;
import com.stupidtree.hita.timetable.packable.EventItem;

import static com.stupidtree.hita.HITAApplication.HContext;
import static com.stupidtree.hita.HITAApplication.TPE;
import static com.stupidtree.hita.timetable.TimeWatcherService.TIMETABLE_CHANGED;
import static com.stupidtree.hita.timetable.TimetableCore.COURSE;
import static com.stupidtree.hita.timetable.TimetableCore.DDL;
import static com.stupidtree.hita.timetable.TimetableCore.EXAM;

public class FragmentEventItem extends BaseFragment {
    EventItem eventItem;
    PopupFragment popupRoot;

    public FragmentEventItem() {
    }

    public static FragmentEventItem newInstance(EventItem ei) {
        FragmentEventItem fe;
        switch (ei.getEventType()) {
            case COURSE:
                fe = new FragmentCourse();
                break;
            case EXAM:
                fe = new FragmentExam();
                break;
            case DDL:
                fe = new FragmentDDL();
                break;
            default:
                fe = new FragmentArrangement();
        }
        Bundle b = new Bundle();
        b.putSerializable("event", ei);
        fe.setArguments(b);
        return fe;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventItem = (EventItem) getArguments().getSerializable("event");
        }
    }


    public void setRoot(PopupFragment root) {
        this.popupRoot = root;
    }

    public EventItem getEventItem() {
        return eventItem;
    }

    @Override
    protected void stopTasks() {

    }

    @Override
    public void Refresh() {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }


    void deleteEvent() {
        new deleteTask(new BaseOperationTask.OperationListener() {
            @Override
            public void onOperationStart(String id, Boolean[] params) {

            }

            @Override
            public void onOperationDone(String id, BaseOperationTask task, Boolean[] params, Object result) {
                Toast.makeText(requireContext(), getString(R.string.notif_delete_success), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra("week", eventItem.week);
                i.setAction(TIMETABLE_CHANGED);
                //Intent i2 = new Intent();
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(i);
                // dialog.dismiss();
                if (popupRoot != null) popupRoot.callDismiss();
            }
        }, eventItem).executeOnExecutor(TPE);
    }

    public interface PopupFragment {
        void callDismiss();
    }


    static class deleteTask extends BaseOperationTask<Boolean> {


        EventItem eventItem;

        deleteTask(OperationListener listRefreshedListener, EventItem eventItem) {
            super(listRefreshedListener);
            this.eventItem = eventItem;
        }

        @Override
        protected Boolean doInBackground(OperationListener<Boolean> listRefreshedListener, Boolean... booleans) {
            return TimetableCore.getInstance(HContext).deleteEvent(eventItem, eventItem.eventType == DDL);

        }


    }
}
