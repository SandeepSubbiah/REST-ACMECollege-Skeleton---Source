package acmecollege.security;

import acmecollege.entity.SecurityUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class CustomIdentityStoreJPAHelper {
    private static final Logger LOG = LogManager.getLogger();

    @PersistenceContext(name = "PU_NAME")
    protected EntityManager em;

    public SecurityUser findUserByName(String username) {
        LOG.debug("find a User By the Name={}", username);
        SecurityUser user = null;
        try {
            user = em.createNamedQuery("SecurityUser.findByUsername", SecurityUser.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOG.debug("No user found with username: {}", username);
        }
        return user;
    }

    public Set<String> findRoleNamesForUser(String username) {
        SecurityUser user = findUserByName(username);
        if (user != null) {
            return user.getRoles().stream()
                    .map(role -> role.getRoleName())
                    .collect(Collectors.toSet());
        }
        return null;
    }
}
