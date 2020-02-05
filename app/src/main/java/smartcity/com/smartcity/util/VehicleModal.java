package smartcity.com.smartcity.util;

import java.util.List;

public class VehicleModal {

    private List<VehiclesBean> vehicles;

    public List<VehiclesBean> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehiclesBean> vehicles) {
        this.vehicles = vehicles;
    }

    public static class VehiclesBean {
        private String Make;
        private String Model;
        private String Year;
        private String PlateNumber;
        private String Mileage;
        private String Power;
        private String Weight;
        private String vAdmin;
        private String vDriver;

        public String getMake() {
            return Make;
        }

        public void setMake(String Make) {
            this.Make = Make;
        }

        public String getModel() {
            return Model;
        }

        public void setModel(String Model) {
            this.Model = Model;
        }

        public String getYear() {
            return Year;
        }

        public void setYear(String Year) {
            this.Year = Year;
        }

        public String getPlateNumber() {
            return PlateNumber;
        }

        public void setPlateNumber(String PlateNumber) {
            this.PlateNumber = PlateNumber;
        }

        public String getMileage() {
            return Mileage;
        }

        public void setMileage(String Mileage) {
            this.Mileage = Mileage;
        }

        public String getPower() {
            return Power;
        }

        public void setPower(String Power) {
            this.Power = Power;
        }

        public String getWeight() {
            return Weight;
        }

        public void setWeight(String Weight) {
            this.Weight = Weight;
        }

        public String getVAdmin() {
            return vAdmin;
        }

        public void setVAdmin(String vAdmin) {
            this.vAdmin = vAdmin;
        }

        public String getVDriver() {
            return vDriver;
        }

        public void setVDriver(String vDriver) {
            this.vDriver = vDriver;
        }
    }
}
