package dto.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jandrad on 27/01/16.
 */
public class StatusDto {

    protected StatusCount statusCount = new StatusCount();
    protected Object list;

    public void addToStatusCount(String key) {
        statusCount.addToStatusCount(key);
    }

    public Object getList() {
        return list;
    }

    public void setList(Object list) {
        this.list = list;
    }

    public static class StatusCount {

        protected long up;
        protected long problematic;
        protected long maintenance;
        protected long nonResponsive;
        protected long total;

        public void addToStatusCount(String key) {

            if ("up".equals(key)) {
                up++;
            } else if ("problematic".equals(key)) {
                problematic++;
            } else if ("maintenance".equals(key)) {
                maintenance++;
            } else if ("non_responsive".equals(key)) {
                nonResponsive++;
            }

            total ++;
        }
    }
}
