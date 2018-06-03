package application.model;

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
 *  This class provides funcionality of music player that is used by controllers.
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

    /**
     *Sets the volume of mediaplayer
     * @param newVolume should range from 0 to 100
     */
    public void setVolume(Double newVolume) {
        if (this.isSongLoaded()) {
            mediaPlayer.setVolume(newVolume / 100);
        }
    }

    /**
     *sets current time of mediaplayer
     * @param fraction
     */
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

    /**
     *Begins playing audio after all null checks, loads first track if needed
     */
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

        public void play(int index) {

                if (index>=currentPlaylist.getSize()||index<0)
                    return;
                mediaPlayer.pause();
                File newTrack = currentPlaylist.vector.get(index);
                loadTrack(newTrack);
                mediaPlayer.play();
                currentTrackIndex.set(index);
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

    /**
     * Creates a new mediaPlayer
     * @param newTrack
     */
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

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     *Adds tracks to playlist
     * @param newTracks
     */
    public void addPlaylist(List<File> newTracks) {
        if (newTracks != null) {
            for (File temp : newTracks) {
                String ext = temp.getName();
                int i = ext.lastIndexOf(".");
                ext = ext.substring(i+1);
                if (ext.equals("mp3") || ext.equals("wav")||ext.equals("aac") || ext.equals("aiff")||ext.equals("aif"))
                    currentPlaylist.add(temp);
            }
        }
    }

    public void removeTrack(int index) {
        if (index == currentTrackIndex.get()) {
            return;
        }

        if (index < currentTrackIndex.get()) {
            currentTrackIndex.set(currentTrackIndex.get() - 1);
        }
        currentPlaylist.remove(index);

    }

    public Playlist getPlaylist() {
        return currentPlaylist;
    }

    /**
     * Wraps index of current track so listeners can watch it. 
     * @return
     */
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

    public TrackData getTrackData(Media media) throws GracenoteException {
        
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
        TrackData data = new TrackData("", "", "");
        if (info.contains("album_coverart:")) {
            data.setAlbumCoverURL( info.substring(info.indexOf("album_coverart:") + 16, info.indexOf("\n", info.indexOf("album_coverart:"))) );
            info = info.replace(( info.substring(info.indexOf("album_coverart:") -6, info.indexOf("\n", info.indexOf("album_coverart:"))) ), "");
        }
        if (info.contains("artist_image_url:")) {
            data.setArtistImage( info.substring(info.indexOf("artist_image_url:") + 18, info.indexOf("\n", info.indexOf("artist_image_url:"))) );
            info = info.replace(( info.substring(info.indexOf("artist_image_url:") -6, info.indexOf("\n", info.indexOf("artist_image_url:"))) ), "");
        }
        if (info.contains("track_gn_id")) {
            info = info.replace(( info.substring(info.indexOf("track_gn_id") -6, info.indexOf("\n", info.indexOf("track_gn_id"))) ), "");
        }
        if (info.contains("album_gnid")) {
            info = info.replace(( info.substring(info.indexOf("album_gnid") -6, info.indexOf("\n", info.indexOf("album_gnid"))) ), "");
        }
        
        data.setInfo(info);
        return data;
    }
    
    /**
     *Creats the api object used to create queries.
     * @throws application.model.radams.gracenote.webapi.GracenoteException
     */
    public void registerGraceNote() throws GracenoteException
    {
                
                    if (userID.equals("")){
                        api = new GracenoteWebAPI("1338309575-CFEC05AC173B943409AECB08F7C86ACF", "CFEC05AC173B943409AECB08F7C86ACF");
                        userID = api.register();
                    }
                    else
                        api = new GracenoteWebAPI("1338309575-CFEC05AC173B943409AECB08F7C86ACF", "CFEC05AC173B943409AECB08F7C86ACF", userID);
                    
    } 

}
