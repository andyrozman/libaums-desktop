package me.jahnen.libaums.libusbcommunication;

//import me.jahnen.libaums.core.ErrNo
import java.io.IOException;

/**
 * IOException that captures the errno and errstr of the current thread.
 */
public class ErrNoIOException extends IOException {
    int errno;
    String errstr;
    public ErrNoIOException(String message, Throwable cause) {
        super(message, cause);
  //      errno = ErrNo.
    }


}


//    : IOException(message, cause) {
//    val errno = ErrNo.errno
//    val errstr = ErrNo.errstr
//}