package ar.edu.itba.cep.evaluations_service.services;

import ar.edu.itba.cep.evaluations_service.models.Exam;

/**
 * Wrapper of {@link Exam} that allows exposing only certain fields by extensions of this class.
 */
/* package */ class ExamWrapper {

    /**
     * The {@link Exam} being wrapped.
     */
    private final Exam exam;


    /**
     * Constructor.
     *
     * @param exam The {@link Exam} being wrapped.
     */
    /* package */ ExamWrapper(final Exam exam) {
        this.exam = exam;
    }


    /**
     * @return The {@link Exam} being wrapped.
     */
    /* package */ Exam getExam() {
        return exam;
    }
}
