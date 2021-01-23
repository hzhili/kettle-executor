package top.heyx.kettle.exception;

/**
 * @author hzl
 */
public class ExecutorException extends RuntimeException {
    private static final long serialVersionUID = 1950743775280327107L;
    public ExecutorException(){};

    public ExecutorException(String msg){
        super(msg);
    }
}
