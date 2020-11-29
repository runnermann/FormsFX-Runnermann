package fmexception;

/**
 * If no files are found. Throw this exception
 * @author Lowell Stadelman
 */
public class NoFilesFoundException extends Exception
{
    public NoFilesFoundException(String message)
    {
        super(message);
    }
}
