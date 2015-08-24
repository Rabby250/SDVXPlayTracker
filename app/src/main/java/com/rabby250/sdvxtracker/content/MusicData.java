package com.rabby250.sdvxtracker.content;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.UUID;

public class MusicData {

    public final UUID musicId;
    public final String title;
    private String mArtist;
    private final HashMap<String, Effect> mEffects
            = new HashMap<>();

    public MusicData(
            @NonNull final UUID musicId,
            @NonNull final String title) {
        this.musicId = musicId;
        this.title = title;
    }

    public void setArtist(@NonNull final String artist) {
        mArtist = artist;
    }

    public String getArtist() {
        return mArtist;
    }

    public void addEffect(
            @NonNull final String difficulty, int level) {
        mEffects.put(difficulty,
                new Effect(musicId, difficulty, level));
    }

    public Effect getEffect(@NonNull final String difficulty) {
        return mEffects.get(difficulty);
    }

    public int getLevel(@NonNull final String difficulty) {
        if (mEffects.containsKey(difficulty)) {
            return mEffects.get(difficulty).level;
        }
        return 0;
    }

    public static class Effect {

        public final UUID musicId;
        public final String difficulty;
        public final int level;
        private String mIllustrator;
        private String mEffectEditor;
        // TODO: add cover

        public Effect(
                @NonNull final UUID musicId,
                @NonNull final String difficulty, int level) {
            this.musicId = musicId;
            this.difficulty = difficulty;
            this.level = level;
        }
    }
}
