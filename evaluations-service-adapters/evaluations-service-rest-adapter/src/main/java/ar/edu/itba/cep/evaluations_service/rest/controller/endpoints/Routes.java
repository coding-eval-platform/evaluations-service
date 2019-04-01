package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

/**
 * Class holding the routes of the API.
 */
public class Routes {

    public static final String EXAMS = "/exams";

    public static final String EXAM = "/exams/{examId : \\d+}";

    public static final String EXAM_START = "/exams/{examId : \\d+}/start";

    public static final String EXAM_FINISH = "/exams/{examId : \\d+}/finish";

    public static final String EXAM_EXERCISES = "/exams/{examId : \\d+}/exercises";

    public static final String EXERCISES = "/exercises";

    public static final String EXERCISE = "/exercises/{exerciseId : \\d+}";

    public static final String EXERCISE_QUESTION = "/exercises/{exerciseId : \\d+}/question";

    public static final String EXERCISE_TEST_CASES = "/exercises/{exerciseId : \\d+}/test-cases";

    public static final String EXERCISE_PUBLIC_TEST_CASES = "/exercises/{exerciseId : \\d+}/test-cases/public";

    public static final String EXERCISE_PRIVATE_TEST_CASES = "/exercises/{exerciseId : \\d+}/test-cases/private";

    public static final String EXERCISE_SOLUTIONS = "/exercises/{exerciseId : \\d+}/solutions";

    public static final String TEST_CASES = "test-cases";

    public static final String TEST_CASE = "test-cases/{testCaseId : \\d+}";

    public static final String TEST_CASE_VISIBILITY = "test-cases/{testCaseId : \\d+}/visibility";

    public static final String TEST_CASE_INPUTS = "test-cases/{testCaseId : \\d+}/inputs";

    public static final String TEST_CASE_EXPECTED_OUTPUTS = "test-cases/{testCaseId : \\d+}/expected-outputs";
}
