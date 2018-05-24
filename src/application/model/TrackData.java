/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.model;

/**
 *
 * @author Grzepa
 */
    public class TrackData {

        private String info;
        private String albumCoverURL;
        private String artistImage;

        public TrackData(){};
        public TrackData(String dataArg, String urlArg, String artistIm) {
            setInfo(dataArg);
            setAlbumCoverURL(urlArg);
            setArtistImage(artistIm);
        }

        public void setInfo(String inf) {
            info = inf;
        }

        public void setAlbumCoverURL(String ur) {
            albumCoverURL = ur;
        }
        
        public void setArtistImage(String im){
            artistImage=im;
        }
        
        public String getInfo() {
            return info;
        }

        public String getAlbumCoverURL() {
            return albumCoverURL;
        }
        
        public String getArtistImage() {
            return artistImage;
        }
    }
