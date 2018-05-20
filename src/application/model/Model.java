package application.model;

import application.model.Playlist;
import application.model.DurationExtended;
import application.model.TrackData;

import java.io.File;
import java.util.List;
import javafx.util.Duration;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import application.model.radams.gracenote.webapi.GracenoteException;
import application.model.radams.gracenote.webapi.GracenoteMetadata;
import application.model.radams.gracenote.webapi.GracenoteWebAPI;



/**
 *  This class provides funcionality of media player that is used by controllers
 * @author Grzegorz Rypeść
 */
public class Model {

    private Media currentTrack;
    private MediaPlayer mediaPlayer;
    private final Playlist currentPlaylist;
    private final IntegerProperty currentTrackIndex;
    private GracenoteWebAPI api;
    private String userID;

    public Model() {
        currentPlaylist = new Playlist();
        currentTrackIndex = new SimpleIntegerProperty(0);
        userID="";
    }

    public boolean isSongLoaded() {
        return (currentTrack != null);
    }

    public boolean isPlaylistEmpty() {
        return currentPlaylist.isEmpty();
    }

    public void setVolume(Double newVolume) {
        if (this.isSongLoaded()) {
            mediaPlayer.setVolume(newVolume / 100);
        }
    }
    
    // sets elapsed time of mediaplayer
    public void setCurrentTime(double fraction) {
        mediaPlayer.stop();
        double mili2 = mediaPlayer.getStopTime().toMillis();
        if (fraction < 0) {
            fraction = 1 + fraction;
        }
        DurationExtended lol = new DurationExtended(fraction * mili2);
        mediaPlayer.setStartTime(lol);
        mediaPlayer.play();
    }
    
    //begins playing audio if all null checks, loads first track if needed
    public void play() {
        if (!this.currentPlaylist.isEmpty()) {
            if (!this.isSongLoaded()) {
                this.loadTrack(this.currentPlaylist.vector.firstElement());
            }
            Status status = mediaPlayer.getStatus();
            if (status != Status.PLAYING) {
                mediaPlayer.play();
            }
        }
    }

    public void pause() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setStartTime(mediaPlayer.getCurrentTime());
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setStartTime(new Duration(0));
        mediaPlayer.stop();
    }

    public void loadTrack(File newTrack) {
        MediaPlayer old = mediaPlayer;
        currentTrack = new Media(new File(newTrack.getAbsolutePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(currentTrack);
        if (old!=null){
            for (int i = 0; i < 10; i++)
                mediaPlayer.getAudioEqualizer().getBands().get(i).setGain(old.getAudioEqualizer().getBands().get(i).getGain());
            mediaPlayer.setVolume(old.getVolume());
            mediaPlayer.setBalance(old.getBalance());
        }        
    }

    public void loadNextTrack() {
        if (!isSongLoaded()) {
            return;
        }
        mediaPlayer.stop();
        if (currentTrackIndex.get() < currentPlaylist.vector.size() - 1)
            currentTrackIndex.set(currentTrackIndex.get() + 1);
        else
            currentTrackIndex.set(0);
        loadTrack(currentPlaylist.vector.get(currentTrackIndex.get()));
    }

    public void loadPrevTrack() {
        if (!isSongLoaded()) {
            return;
        }
        mediaPlayer.stop();
        if (currentTrackIndex.get() > 0) {
            currentTrackIndex.set(currentTrackIndex.get() - 1);
        }
        loadTrack(currentPlaylist.vector.get(currentTrackIndex.get()));
    }

    public void setPlaylist(List<File> newTracks) {
        if (newTracks != null) {
            for (File temp : newTracks) {
                currentPlaylist.add(temp);
            }
        }
    }

    public void removeTrack(String trackName) {
        if (currentPlaylist.getTracksIndex(trackName) == currentTrackIndex.get()) {
            return;
        }

        if (currentPlaylist.getTracksIndex(trackName) < currentTrackIndex.get()) {
            currentTrackIndex.set(currentTrackIndex.get() - 1);
        }
        currentPlaylist.remove(trackName);

    }

    public Playlist getPlaylist() {
        return currentPlaylist;
    }

    public IntegerProperty getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public ReadOnlyObjectProperty<Duration> getCurrentTimeProperty() {
        return mediaPlayer.currentTimeProperty();
    }

    public Duration getTrackDuration() {
        return mediaPlayer.getStopTime();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Media getMedia() {
        return currentTrack;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String s) {
        userID = s;
    }

    public TrackData getTrackData(Media media) {
        String trackTitle, trackAlbum = "", trackArtist = "", info;
        if (media.getMetadata().get("title") != null) {
            trackTitle = media.getMetadata().get("title").toString();
        } else {
            trackTitle = media.getSource().replace("%20", " ");

            trackTitle = trackTitle.substring(trackTitle.lastIndexOf('/') + 1, trackTitle.length());
            trackTitle = trackTitle.substring(0, trackTitle.lastIndexOf('.'));
        }

        if (media.getMetadata().get("artist") != null) {
            trackArtist = media.getMetadata().get("artist").toString();
        }

        if (media.getMetadata().get("album") != null) {
            trackAlbum = media.getMetadata().get("album").toString();
        }
        GracenoteMetadata results = api.searchTrack(trackArtist, trackAlbum, trackTitle);
        info = results.getData();
        TrackData data = new TrackData("", "");
        String url = "";
        if (info.contains("album_coverart:")) {
            url = info.substring(info.indexOf("album_coverart:") + 16, info.indexOf("\n", info.indexOf("album_coverart:")));
        }
        data.setUrl(url);
        data.setInfo(info);
        return data;
    }
    
    //creates Gracenote api by connecting to Gracenote database, retrieves userID if it wasn't found in settings file
    public int registerGraceNote()
    {
                try {
                    if (userID.equals("")){
                        api = new GracenoteWebAPI("1338309575-CFEC05AC173B943409AECB08F7C86ACF", "CFEC05AC173B943409AECB08F7C86ACF");
                        if (api==null)
                            return 0;
                        userID = api.register();
                    }
                    else
                        api = new GracenoteWebAPI("1338309575-CFEC05AC173B943409AECB08F7C86ACF", "CFEC05AC173B943409AECB08F7C86ACF", userID);
                        if (api==null)
                            return 0;
                    
        } catch (GracenoteException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

}
