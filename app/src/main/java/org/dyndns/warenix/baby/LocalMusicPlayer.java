package org.dyndns.warenix.baby;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by warenix on 1/6/18.
 */

public class LocalMusicPlayer {

    int mCurrentSongIndex = 0;
    private ArrayList<Song> mSongList;
    private MediaPlayer mMediaPlayer;

    private Song getNextSong() {
        mCurrentSongIndex++;
        if (mCurrentSongIndex == mSongList.size() - 1) {
            mCurrentSongIndex = 0;
        }
        // mCurrentSongIndex is the next Song index
        return mSongList.get(mCurrentSongIndex);
    }

    public void setSongList(ArrayList<Song> songList) {
        mSongList = songList;
    }

    public void playSong() throws IOException {
        mMediaPlayer.reset();
        Song song = getNextSong();
        mMediaPlayer.setDataSource(song.getLocalPath());
        mMediaPlayer.prepareAsync();
    }

}
