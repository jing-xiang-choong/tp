package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.Week;
import seedu.address.model.consultation.Consultation;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final int TOTAL_WEEKS = 13;
    private static final double RECTANGLE_WIDTH = 16;
    private static final double RECTANGLE_HEIGHT = 16;

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private FlowPane tags;
    @FXML
    private Label studentId;
    @FXML
    private Label moduleCode;
    @FXML
    private FlowPane grades;
    @FXML
    private Label consultations;
    @FXML
    private HBox weekNumbersRow;
    @FXML
    private HBox attendanceRow;
    @FXML
    private Label remark;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(String.valueOf(displayedIndex));
        name.setText(person.getName().fullName);
        studentId.setText(person.getStudentId() != null ? person.getStudentId().value : "N/A");
        if (!person.getModuleCodes().isEmpty()) {
            String moduleCodesText = person.getModuleCodes().stream()
                    .map(mc -> mc.value)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("No Modules Recorded");
            moduleCode.setText(moduleCodesText);
        } else {
            moduleCode.setText("No Modules Recorded");
        }

        // Populate attendance grid
        populateAttendanceGrid(person);

        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        if (person.getGrades().isEmpty()) {
            Label noGradesLabel = new Label("No Grades Recorded");
            noGradesLabel.getStyleClass().add("empty-label");
            grades.getChildren().add(noGradesLabel);
        } else {
            person.getGrades().stream()
                    .sorted(Comparator.comparing(grade -> grade.assignmentName))
                    .forEach(grade -> {
                        Label gradeLabel = new Label(grade.toString());
                        gradeLabel.getStyleClass().add("grade-label");
                        grades.getChildren().add(gradeLabel);
                    });
        }
        consultations.setText(getConsultationsSummary(person));
        remark.setText(person.getRemark() != null ? person.getRemark().value : "No remarks");
    }

    /**
     * Populates the attendance grid with 13 weeks, showing colored rectangles.
     * Green rectangle = Present, Red rectangle = Absent, Gray rectangle = No record.
     */
    private void populateAttendanceGrid(Person person) {
        Map<Week, AttendanceStatus> attendances = person.getAttendanceRecord().getAllAttendances();

        for (int week = 1; week <= TOTAL_WEEKS; week++) {
            // Create week number label
            Label weekLabel = new Label(String.valueOf(week));
            weekLabel.getStyleClass().add("week-number");
            weekLabel.setPrefWidth(18);
            weekLabel.setAlignment(Pos.CENTER);
            weekNumbersRow.getChildren().add(weekLabel);

            // Create attendance rectangle
            Rectangle attendanceRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            attendanceRectangle.getStyleClass().add("attendance-rectangle");

            Week weekObj = new Week(week);
            if (attendances.containsKey(weekObj)) {
                AttendanceStatus status = attendances.get(weekObj);
                if (status == AttendanceStatus.PRESENT) {
                    attendanceRectangle.setStyle("-fx-fill: #4CAF50;"); // Green
                    attendanceRectangle.getStyleClass().add("attendance-present");
                } else {
                    attendanceRectangle.setStyle("-fx-fill: #F44336;"); // Red
                    attendanceRectangle.getStyleClass().add("attendance-absent");
                }
            } else {
                attendanceRectangle.setStyle("-fx-fill: #CCCCCC;"); // Gray for no record
                attendanceRectangle.getStyleClass().add("attendance-no-record");
            }

            // Wrap rectangle in HBox for alignment
            HBox rectangleBox = new HBox();
            rectangleBox.setPrefWidth(18);
            rectangleBox.setAlignment(Pos.CENTER);
            rectangleBox.getChildren().add(attendanceRectangle);
            attendanceRow.getChildren().add(rectangleBox);
        }
    }

    private String getConsultationsSummary(Person person) {
        List<Consultation> consultationList = person.getConsultations();

        if (consultationList.isEmpty()) {
            return "No consultations";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy h:mm a");
        return consultationList.stream()
                .map(c -> c.getDateTime().format(formatter))
                .collect(Collectors.joining("\n"));
    }
}
