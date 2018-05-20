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
        private String url;

        public TrackData(){};
        public TrackData(String dataArg, String urlArg) {
            setInfo(dataArg);
            setUrl(urlArg);
        }

        public void setInfo(String inf) {
            info = inf;
        }

        public void setUrl(String ur) {
            url = ur;
        }

        public String getInfo() {
            return info;
        }

        public String getUrl() {
            return url;
        }
    }
