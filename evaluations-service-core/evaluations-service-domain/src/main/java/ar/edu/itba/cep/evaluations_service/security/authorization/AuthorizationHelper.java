package ar.edu.itba.cep.evaluations_service.security.authorization;

import ar.edu.itba.cep.evaluations_service.models.Exam;

import java.util.Optional;

/**
 * Helper class for authorization tasks.
 */
/* package */ class AuthorizationHelper {


    /**
     * Private constructor to avoid instantiation.
     */
    private AuthorizationHelper() {
    }


    /**
     * Indicates whether the given {@code exam} is owned by the given {@code principal}.
     *
     * @param exam      The {@link Exam} to be checked.
     * @param principal The username of the user used to check ownership.
     * @return {@code true} if the user with the username that matches the given {@code principal} is owner of the
     * given {@code exam}, or {@code false} otherwise.
     */
    /* package */
    static boolean isExamOwner(final Exam exam, final String principal) {
        return Optional.ofNullable(exam)
                .map(Exam::getOwners)
                .filter(owners -> owners.contains(principal))
                .isPresent()
                ;
    }
}
