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
package helpers;

import play.Logger;
import play.db.jpa.Blob;
import play.libs.MimeTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by jandrad on 25/10/15.
 */
public class ImageHelper {

    public static Blob blobFromFile(File file) {
        Blob blob = new Blob();

        try {
            String fileName = file.getName();
            InputStream is = new FileInputStream(file);
            blob.set(is, MimeTypes.getContentType(fileName));
        } catch (Exception e) {
            Logger.error("Could not save file");
        }

        return blob;
    }

    public static boolean isValidImage(Blob blob) {
        if (blob!=null) {
            String mimeType = blob.type();
            return mimeType.startsWith("img/");
        }

        return false;
    }

    public static boolean isValidImage(File file) {
        if (file!=null) {
            String fileName = file.getName();
            String mimeType = MimeTypes.getContentType(fileName);
            return mimeType.startsWith("img/");
        }

        return false;
    }
}
