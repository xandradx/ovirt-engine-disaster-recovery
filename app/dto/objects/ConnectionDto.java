package dto.objects;

/**
 * Created by jandrad on 26/01/16.
 */
public class ConnectionDto {

    protected String ipAddress;
    protected String iqn;

    public ConnectionDto(String ipAddress, String iqn) {
        this.ipAddress = ipAddress;
        this.iqn = iqn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIqn() {
        return iqn;
    }

    public void setIqn(String iqn) {
        this.iqn = iqn;
    }
}
