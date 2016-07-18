/*
 *   Copyright 2016 ITM, S.A.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * `*   `[`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
        protected long maintenance;
        protected long nonResponsive;
        protected long total;
        protected long other;

        public void addToStatusCount(String key) {

            if ("up".equals(key)) {
                up++;
            } else if ("maintenance".equals(key)) {
                maintenance++;
            } else if ("non_responsive".equals(key)) {
                nonResponsive++;
            } else {
                other++;
            }

            total ++;
        }
    }
}
