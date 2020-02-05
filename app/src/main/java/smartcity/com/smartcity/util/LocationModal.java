package smartcity.com.smartcity.util;

import java.util.List;

public class LocationModal {
    private List<LocationBean> location;

    public List<LocationBean> getLocation() {
        return location;
    }

    public void setLocation(List<LocationBean> location) {
        this.location = location;
    }

    public static class LocationBean {
        /**
         * id : 8400
         * TripID : Test_kkapooor_180810114615
         * Lat : 33.647864
         * Lng : 72.9633436
         * Speed : 0.0
         * Accelerate : 0.0
         * DateStamp : 2018-08-10
         * TimeStamp : 11:46:18
         * Altitude : 581.7535337232403
         */

        private String id;
        private String TripID;
        private String Lat;
        private String Lng;
        private String Speed;
        private String Accelerate;
        private String DateStamp;
        private String TimeStamp;
        private String Altitude;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTripID() {
            return TripID;
        }

        public void setTripID(String TripID) {
            this.TripID = TripID;
        }

        public String getLat() {
            return Lat;
        }

        public void setLat(String Lat) {
            this.Lat = Lat;
        }

        public String getLng() {
            return Lng;
        }

        public void setLng(String Lng) {
            this.Lng = Lng;
        }

        public String getSpeed() {
            return Speed;
        }

        public void setSpeed(String Speed) {
            this.Speed = Speed;
        }

        public String getAccelerate() {
            return Accelerate;
        }

        public void setAccelerate(String Accelerate) {
            this.Accelerate = Accelerate;
        }

        public String getDateStamp() {
            return DateStamp;
        }

        public void setDateStamp(String DateStamp) {
            this.DateStamp = DateStamp;
        }

        public String getTimeStamp() {
            return TimeStamp;
        }

        public void setTimeStamp(String TimeStamp) {
            this.TimeStamp = TimeStamp;
        }

        public String getAltitude() {
            return Altitude;
        }

        public void setAltitude(String Altitude) {
            this.Altitude = Altitude;
        }
    }
}
