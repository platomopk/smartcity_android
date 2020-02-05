package smartcity.com.smartcity.util;

public class Util {
    public String baseIP= "http://smartcityapplications.com.pk/scripts/";//"http://pdttests.com/newscripts/";
    public String weatherIP = "https://api.darksky.net/forecast/5bf250c03466a8aaf440be51e90690a8/";
    public String param=  "?units=si&exclude=hourly,flags,daily";

    public String getBaseIP() {
        return baseIP;
    }

    public String getWeatherIP() {
        return weatherIP;
    }

    public String getParam() {
        return param;
    }
}
