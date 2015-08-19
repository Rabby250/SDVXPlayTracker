package com.rabby250.sdvxtracker.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class MusicDataDownloadService extends IntentService {

    public static void startActionFoo(Context context) {
        Intent intent = new Intent(
                context, MusicDataDownloadService.class);
        context.startService(intent);
    }

    public MusicDataDownloadService() {
        super("MusicDataDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    private void downloadMusicData() {
    }
}