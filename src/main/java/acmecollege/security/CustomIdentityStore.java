package acmecollege.security;

import static java.util.Collections.emptySet;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

@ApplicationScoped
@Typed(CustomIdentityStore.class)
public class CustomIdentityStore implements IdentityStore {

    @Inject
    protected CustomIdentityStoreJPAHelper jpaHelper;

    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    @Override
    public CredentialValidationResult validate(Credential credential) {

        CredentialValidationResult result = INVALID_RESULT;

        if (credential instanceof UsernamePasswordCredential) {
            String callerName = ((UsernamePasswordCredential)credential).getCaller();
            String credentialPassword = ((UsernamePasswordCredential)credential).getPasswordAsString();
            SecurityUser user = jpaHelper.findUserByName(callerName);
            if (user != null) {
                String pwHash = user.getPwHash();
                try {
                    boolean verified = pbAndjPasswordHash.verify(credentialPassword.toCharArray(), pwHash);
                    if (verified) {
                        Set<String> rolesForUser = jpaHelper.findRoleNamesForUser(callerName);
                        result = new CredentialValidationResult(new WrappingCallerPrincipal(user), rolesForUser);
                    }
                }
                catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
        else if (credential instanceof CallerOnlyCredential) {
            String callerName = ((CallerOnlyCredential)credential).getCaller();
            SecurityUser user = jpaHelper.findUserByName(callerName);
            if (user != null) {
                result = new CredentialValidationResult(callerName);
            }
        }

        return result;
    }

    protected Set<String> getRolesNamesForSecurityRoles(Set<SecurityRole> roles) {
        Set<String> roleNames = emptySet();
        if (!roles.isEmpty()) {
            roleNames = roles
                    .stream()
                    .map(s -> s.getRoleName())
                    .collect(Collectors.toSet());
        }
        return roleNames;
    }

}
