/**
 * @Author Anastazja Noemi Lisowska
 */

package IO;

import java.io.PrintStream;

/**
 * for Client or Server to send messages, Class was created to reduce code repeating
 */
public class SocketSender {
    protected PrintStream toSendMessage;

    //SocketSender constructor
    public SocketSender(PrintStream toSendMessage) {
        this.toSendMessage = toSendMessage;
    }

    /**
     * For commanding sending
     */
    public void send(String command) {
        toSendMessage.println(command);
    }
}
