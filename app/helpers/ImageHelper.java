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
