package drp.objects;

/**
 * Created by jandrad on 23/01/16.
 */
public interface OperationListener {

    public enum MessageType {
        INFO,
        SUCCESS,
        ERROR
    }

    void onMessage(Exception e, String message, MessageType type);
    void onFinished(String message, boolean success);
    void onRefreshHosts();
    void onRefreshDatacenters();
}
