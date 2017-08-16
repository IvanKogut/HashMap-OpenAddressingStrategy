package ua.kyiv.kpi.fpm.km32.kohut.exception;

/**
 * Created by i.kohut on 8/16/2017.
 */
public class NotExistKeyException extends RuntimeException{

    public NotExistKeyException(String message) {
        super(message);
    }
}