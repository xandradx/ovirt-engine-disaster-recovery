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
package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by jandrad on 23/01/16.
 */

@Entity
public class OperationLog extends Model {

    public enum MessageType {
        MESSAGE,
        ERROR
    }

    public OperationLog(MessageType type) {
        this.date = new Date();
        this.type = type;
    }

    public MessageType type;

    public Date date;

    @ManyToOne
    public DisasterRecoveryOperation operation;

    public String message;
}
