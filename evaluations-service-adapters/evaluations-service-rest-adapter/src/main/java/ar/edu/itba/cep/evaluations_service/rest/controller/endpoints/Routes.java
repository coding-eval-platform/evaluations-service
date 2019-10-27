package ar.edu.itba.cep.evaluations_service.rest.controller.endpoints;

/**
 * Class holding the routes of the API.
 */
public class Routes {

    public static final String EXAMS = "/exams";

    public static final String MY_EXAMS = "/exams/mine";

    public static final String EXAM = "/exams/{examId : \\d+}";

    public static final String EXAM_START = "/exams/{examId : \\d+}/start";

    public static final String EXAM_FINISH = "/exams/{examId : \\d+}/finish";

    public static final String EXAM_OWNER = "/exams/{examId : \\d+}/owners/{owner : .+}";

    public static final String EXAM_EXERCISES = "/exams/{examId : \\d+}/exercises";

    public static final String EXERCISES = "/exercises";

    public static final String EXERCISE = "/exercises/{exerciseId : \\d+}";

    public static final String EXERCISE_TEST_CASES = "/exercises/{exerciseId : \\d+}/test-cases";

    public static final String EXERCISE_PUBLIC_TEST_CASES = "/exercises/{exerciseId : \\d+}/test-cases/public";

    public static final String EXERCISE_PRIVATE_TEST_CASES = "/exercises/{exerciseId : \\d+}/test-cases/private";

    public static final String TEST_CASES = "/test-cases";

    public static final String TEST_CASE = "/test-cases/{testCaseId : \\d+}";

    public static final String EXAM_SOLUTIONS_SUBMISSIONS_BY_EXAM = "/exams/{examId : \\d+}/solutions-submissions";

    public static final String EXAM_SOLUTIONS_SUBMISSIONS = "/solutions-submissions";

    public static final String EXAM_SOLUTIONS_SUBMISSION = "/solutions-submissions/{submissionId : \\d+}";

    public static final String SUBMIT_SOLUTION = "/solutions-submissions/{submissionId : \\d+}/submit";

    public static final String SCORE_SOLUTION = "/solutions-submissions/{submissionId : \\d+}/score";

    public static final String SOLUTIONS = "/solutions-submissions/{submissionId : \\d+}/solutions";

    public static final String SOLUTION = "/solutions/{solutionId : \\d+}";

    public static final String SOLUTION_RESULTS = "/solutions/{solutionId : \\d+}/results";

    public static final String SOLUTION_TEST_CASE_RESULT = "/solutions/{solutionId : \\d+}/results/test-case/{testCaseId : \\d+}";

    public static final String RETRY_SOLUTION_EXECUTION = "/solutions/{solutionId : \\d+}/retry";

    public static final String RETRY_SOLUTION_TEST_CASE_EXECUTION = "/solutions/{solutionId : \\d+}/retry/test-case/{testCaseId : \\d+}";


    public static final String EXAM_INTERNAL = "/internal/exams/{examId : \\d+}";
}
