package ar.edu.itba.cep.evaluations_service.rest.controller.dtos;

/**
 * Class containing several constants for the DTOs package.
 */
/* package */ class Constants {

    /**
     * Pattern used to represent the moment at which {@link ar.edu.itba.cep.evaluations_service.models.Exam} starts
     * (stipulated and actual).
     */
    /* package */ static final String STARTING_AT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    /**
     * Timezone for the moment at which {@link ar.edu.itba.cep.evaluations_service.models.Exam} starts
     * (stipulated and actual).
     */
    /* package */ static final String STARTING_AT_TIME_ZONE = "UTC";


    /**
     * Private constructor to avoid instantiation.
     */
    private Constants() {
    }
}
