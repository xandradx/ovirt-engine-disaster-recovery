package drp.objects;

/**
 * Created by jandrad on 23/01/16.
 */
public interface OperationListener {

    void onMessage(String message);
    void onFinished(String message, boolean success);
    void onError(Exception e, String error);

}
