package smartcity.com.smartcity.util;

import java.util.List;

public class DriverModal {


    private List<DriversBean> drivers;

    public List<DriversBean> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<DriversBean> drivers) {
        this.drivers = drivers;
    }

    public static class DriversBean {
        /**
         * FirstName : Mohsin1
         * LastName : Qa1zi
         * Gender : Male
         * Dob : 23-3-3
         * Email : aboco2@ok.com
         * Username : mooo3hs3in
         * Password : 123
         * Country : PK
         * City : ISB
         * PhoneNumber : 090078601
         * DLNumber : 123324
         * DExperience : 12
         * DCompany : 3ok
         * DAdmin : mohsino
         */

        private String FirstName;
        private String LastName;
        private String Gender;
        private String Dob;
        private String Email;
        private String Username;
        private String Password;
        private String Country;
        private String City;
        private String PhoneNumber;
        private String DLNumber;
        private String DExperience;
        private String DCompany;
        private String DAdmin;

        public String getFirstName() {
            return FirstName;
        }

        public void setFirstName(String FirstName) {
            this.FirstName = FirstName;
        }

        public String getLastName() {
            return LastName;
        }

        public void setLastName(String LastName) {
            this.LastName = LastName;
        }

        public String getGender() {
            return Gender;
        }

        public void setGender(String Gender) {
            this.Gender = Gender;
        }

        public String getDob() {
            return Dob;
        }

        public void setDob(String Dob) {
            this.Dob = Dob;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
            this.Email = Email;
        }

        public String getUsername() {
            return Username;
        }

        public void setUsername(String Username) {
            this.Username = Username;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String Password) {
            this.Password = Password;
        }

        public String getCountry() {
            return Country;
        }

        public void setCountry(String Country) {
            this.Country = Country;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String City) {
            this.City = City;
        }

        public String getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(String PhoneNumber) {
            this.PhoneNumber = PhoneNumber;
        }

        public String getDLNumber() {
            return DLNumber;
        }

        public void setDLNumber(String DLNumber) {
            this.DLNumber = DLNumber;
        }

        public String getDExperience() {
            return DExperience;
        }

        public void setDExperience(String DExperience) {
            this.DExperience = DExperience;
        }

        public String getDCompany() {
            return DCompany;
        }

        public void setDCompany(String DCompany) {
            this.DCompany = DCompany;
        }

        public String getDAdmin() {
            return DAdmin;
        }

        public void setDAdmin(String DAdmin) {
            this.DAdmin = DAdmin;
        }
    }
}
