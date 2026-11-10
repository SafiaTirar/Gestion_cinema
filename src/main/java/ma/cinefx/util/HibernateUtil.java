package ma.cinefx.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    // SessionFactory unique pour tout le projet
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Hibernate va chercher automatiquement hibernate.cfg.xml dans resources
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Échec de la création de la SessionFactory : " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Méthode pour récupérer la SessionFactory
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Méthode pour fermer la SessionFactory à la fin du projet
    public static void shutdown() {
        getSessionFactory().close();
    }
}
