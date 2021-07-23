package io.geewit.data.jpa.essential.exception;

/**
 * Created on 27/11/16.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class RepositoryEntityGraphException extends RuntimeException {

    public RepositoryEntityGraphException(String message) {
        super(message);
    }
}
