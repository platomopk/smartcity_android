package smartcity.com.smartcity.util;

import java.util.List;

public class TripModal {

    private List<TripsBean> trips;

    public List<TripsBean> getTrips() {
        return trips;
    }

    public void setTrips(List<TripsBean> trips) {
        this.trips = trips;
    }

    public static class TripsBean {
        /**
         * TripId : mohsin1234
         * StartTime : 1am
         * EndTime : 1am
         * TAdmin : mohsino
         * TDriver : qazo
         * TVehicle : 123456
         * StartLoc : nust
         * EndLoc : seecs
         * Status : fin
         */

        private String TripId;
        private String StartTime;
        private String EndTime;
        private String TAdmin;
        private String TDriver;
        private String TVehicle;
        private String StartLoc;
        private String EndLoc;
        private String Status;

        public String getTripId() {
            return TripId;
        }

        public void setTripId(String TripId) {
            this.TripId = TripId;
        }

        public String getStartTime() {
            return StartTime;
        }

        public void setStartTime(String StartTime) {
            this.StartTime = StartTime;
        }

        public String getEndTime() {
            return EndTime;
        }

        public void setEndTime(String EndTime) {
            this.EndTime = EndTime;
        }

        public String getTAdmin() {
            return TAdmin;
        }

        public void setTAdmin(String TAdmin) {
            this.TAdmin = TAdmin;
        }

        public String getTDriver() {
            return TDriver;
        }

        public void setTDriver(String TDriver) {
            this.TDriver = TDriver;
        }

        public String getTVehicle() {
            return TVehicle;
        }

        public void setTVehicle(String TVehicle) {
            this.TVehicle = TVehicle;
        }

        public String getStartLoc() {
            return StartLoc;
        }

        public void setStartLoc(String StartLoc) {
            this.StartLoc = StartLoc;
        }

        public String getEndLoc() {
            return EndLoc;
        }

        public void setEndLoc(String EndLoc) {
            this.EndLoc = EndLoc;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String Status) {
            this.Status = Status;
        }
    }
}
