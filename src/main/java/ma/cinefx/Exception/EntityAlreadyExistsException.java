package ma.cinefx.Exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String entity, String details) {
        super(entity + " existe déjà : " + details);
    }
}
