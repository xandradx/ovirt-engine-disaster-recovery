package dto.objects;

/**
 * Created by jandrad on 27/01/16.
 */
public class DataCenterDto {

    protected String name;
    protected String status;

    public DataCenterDto(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
