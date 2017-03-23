package de.dema.pd3.action;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by Ronny on 23.03.2017.
 */
public class Outcome implements Serializable {

    private Optional<Exception> exception;
    private String msg;
    private boolean success;

    private Outcome(String msg, boolean success) {
        this.msg = msg;
        this.success = success;
    }

    public static Outcome error(String msg) {
        return error(null, msg);
    }

    public static Outcome error(Exception e, String msg) {
        Outcome o = new Outcome(msg, false);
        o.exception = Optional.ofNullable(e);
        return o;
    }

    public static Outcome success(String msg) {
        Outcome o = new Outcome(msg, false);
        o.exception = Optional.empty();
        return o;
    }

    public boolean success() {
        return success;
    }

    public Exception getException() {
        return exception.orElse(null);
    }

    public String getMsg() {
        return msg;
    }
}
