package com.rabby250.sdvxtracker.developer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.rabby250.sdvxtracker.R;
import com.rabby250.sdvxtracker.content.MusicData;
import com.rabby250.sdvxtracker.content.MusicList;

import java.util.HashSet;

public class LogActivity extends Activity {

    private static final int MESSAGE_MAIN_HANDLER = 0x100;
    private static final int MESSAGE_TOGGLE_SYNC
            = MESSAGE_MAIN_HANDLER | 1;
    private static final int MESSAGE_FETCH_HANDLER = 0x200;
    private static final int MESSAGE_PRINT_LOG
            = MESSAGE_FETCH_HANDLER | 1;
    private static final int MESSAGE_CONTINUE_SYNC
            = MESSAGE_FETCH_HANDLER | 2;

    private ScrollView mLogScroller;
    private TextView mLogWindow;
    private Button mSyncButton;
    private Handler mMainHandler, mFetchHandler;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_log);
        mLogScroller = (ScrollView) findViewById(R.id.log_scroller);
        mLogWindow = (TextView)
                mLogScroller.findViewById(R.id.log_window);
        mSyncButton = (Button) findViewById(R.id.sync_button);
        mSyncButton.setOnClickListener(new SyncTrigger());
        mMainHandler = new Handler(
                getMainLooper(), mUiCallback);
        final HandlerThread fetchThread
                = new HandlerThread("DataFetchThread");
        fetchThread.start();
        mFetchHandler = new Handler(
                fetchThread.getLooper(), mFetchCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFetchHandler.getLooper().quit();
    }

    private Handler.Callback mUiCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_PRINT_LOG) {
                final String logText = "\n" + msg.obj;
                if (mLogWindow != null) {
                    mLogWindow.append(logText);
                    mLogScroller.fullScroll(View.FOCUS_DOWN);
                    return true;
                }
            }
            return false;
        }
    };

    private Handler.Callback mFetchCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOGGLE_SYNC:
                    if (mFetchHandler.hasMessages(
                            MESSAGE_CONTINUE_SYNC)) {
                        mFetchHandler.removeMessages(
                                MESSAGE_CONTINUE_SYNC);
                        printLog("Stopped syncing.");
                        return true;
                    }
                    printLog("Start syncing!");
                    fetchPlayData();
                    mFetchHandler.sendMessageDelayed(
                            mFetchHandler.obtainMessage(
                                    MESSAGE_CONTINUE_SYNC), 5000);
                    return true;
                case MESSAGE_CONTINUE_SYNC:
                    printLog("Continue syncing...");
                    mFetchHandler.sendMessageDelayed(
                            mFetchHandler.obtainMessage(
                                    MESSAGE_CONTINUE_SYNC), 5000);
                    return true;
            }
            return false;
        }
    };

    private void printLog(final String text) {
        mMainHandler.sendMessage(mMainHandler
                .obtainMessage(MESSAGE_PRINT_LOG, text));
    }

    private class SyncTrigger implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            mFetchHandler.sendMessage(
                    mFetchHandler.obtainMessage(MESSAGE_TOGGLE_SYNC));
        }
    }

    private void fetchPlayData() {
        for (int sortIndex = 1; sortIndex <= 10; sortIndex++) {
            printLog("Fetching music list with sort index = "
                    + sortIndex);
            int pageCount = MusicList.countPages(sortIndex);
            for (int pageIndex = 1; pageIndex <= pageCount; pageIndex++) {
                printLog("\tFetching page " + pageIndex);
                HashSet<MusicData> list = MusicList.parseMusicList(
                        sortIndex, pageIndex);
                for (MusicData data : list) {
                    printLog("\t\tFound " + data.musicId);
                    printLog("\t\t\tTitle: " + data.title);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
        try {
        } catch (IOException | InterruptedException e) {
            printLog(e.getMessage());
        }
        */
    }
}
