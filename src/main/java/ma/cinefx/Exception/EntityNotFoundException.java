package ma.cinefx.Exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, String details) {
        super(entity + " non trouvé : " + details);
    }
}
