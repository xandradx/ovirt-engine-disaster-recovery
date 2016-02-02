package dto.objects;

/**
 * Created by jandrad on 26/01/16.
 */
public class ConnectionDto {

    protected String ipAddress;
    protected String iqn;

    protected String destinationAddress;
    protected String destinationIqn;

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

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationIqn() {
        return destinationIqn;
    }

    public void setDestinationIqn(String destinationIqn) {
        this.destinationIqn = destinationIqn;
    }
}
