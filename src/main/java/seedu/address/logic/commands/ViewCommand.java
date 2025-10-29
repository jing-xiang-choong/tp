package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.Week;
import seedu.address.model.consultation.Consultation;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;

/**
 * Views detailed information of a student identified using their displayed index or student ID.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Views the details of the student identified by the index number used in the displayed student list "
            + "or by their student ID.\n"
            + "Parameters: INDEX (must be a positive integer) or s/STUDENT_ID\n"
            + "Example: " + COMMAND_WORD + " 1 or " + COMMAND_WORD + " s/A0123456X";

    public static final String MESSAGE_VIEW_STUDENT_SUCCESS = "Viewing student: %1$s";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student found with ID: %1$s";

    private final Index targetIndex;
    private final StudentId studentId;

    /**
     * Creates a ViewCommand to view the student at the specified {@code Index}.
     */
    public ViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
        this.studentId = null;
    }

    /**
     * Creates a ViewCommand to view the student with the specified {@code StudentId}.
     */
    public ViewCommand(StudentId studentId) {
        this.targetIndex = null;
        this.studentId = studentId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToView;

        if (targetIndex != null) {
            // View by index
            List<Person> lastShownList = model.getFilteredPersonList();

            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            personToView = lastShownList.get(targetIndex.getZeroBased());
        } else {
            // View by student ID
            personToView = model.findPersonByStudentId(studentId);

            if (personToView == null) {
                throw new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId));
            }
        }

        // Filter the list to show only the selected student
        model.updateFilteredPersonList(person -> person.equals(personToView));

        // Create detailed view message with attendance information
        String detailedMessage = createDetailedViewMessage(personToView);

        return new CommandResult(detailedMessage);
    }

    /**
     * Creates a detailed view message with student information and attendance record.
     */
    private String createDetailedViewMessage(Person person) {
        StringBuilder sb = new StringBuilder();

        // Basic student information
        sb.append("=== STUDENT DETAILS ===\n");
        sb.append("Name: ").append(person.getName().fullName).append("\n");
        sb.append("Student ID: ")
                .append(person.getStudentId() != null ? person.getStudentId().value : "N/A")
                .append("\n");
        sb.append("Email: ").append(person.getEmail().value).append("\n");

        // Module codes
        if (!person.getModuleCodes().isEmpty()) {
            String moduleCodesText = person.getModuleCodes().stream()
                    .map(mc -> mc.value)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");
            sb.append("Modules: ").append(moduleCodesText).append("\n");
        }

        // Tags
        if (!person.getTags().isEmpty()) {
            String tagsText = person.getTags().stream()
                    .map(tag -> tag.tagName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");
            sb.append("Tags: ").append(tagsText).append("\n");
        }

        // Grades
        if (!person.getGrades().isEmpty()) {
            String gradesText = person.getGrades().stream()
                    .sorted(Comparator.comparing(grade -> grade.assignmentName))
                    .map(grade -> grade.toString())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");
            sb.append("Grades: ").append(gradesText).append("\n");
        }

        // Consultations
        if (person.getConsultations() != null && !person.getConsultations().isEmpty()) {
            String consultationsText = person.getConsultations().stream()
                    .map(c -> c.getDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")))
                    .collect(Collectors.joining(", "));
            sb.append("Consultations: ").append(consultationsText).append("\n");
        }

        // Remark
        if (person.getRemark() != null && !person.getRemark().value.isEmpty()) {
            sb.append("Remark: ").append(person.getRemark().value).append("\n");
        }

        // Attendance record
        sb.append("\n=== ATTENDANCE RECORD ===\n");
        if (person.getAttendanceRecord().isEmpty()) {
            sb.append("No attendance recorded yet.\n");
        } else {
            Map<Week, AttendanceStatus> attendances = person.getAttendanceRecord()
                    .getAllAttendances();

            // Sort by week number and display vertically
            attendances.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(week -> week.value)))
                    .forEach(entry -> {
                        Week week = entry.getKey();
                        AttendanceStatus status = entry.getValue();
                        String symbol = status == AttendanceStatus.PRESENT ? "✓" : "✗";
                        String statusText = status == AttendanceStatus.PRESENT
                                ? "Present" : "Absent";
                        sb.append("Week ").append(week.value).append(": ")
                                .append(symbol).append(" ").append(statusText)
                                .append("\n");
                    });
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewCommand)) {
            return false;
        }

        ViewCommand otherViewCommand = (ViewCommand) other;

        // Both use index
        if (targetIndex != null && otherViewCommand.targetIndex != null) {
            return targetIndex.equals(otherViewCommand.targetIndex);
        }

        // Both use student ID
        if (studentId != null && otherViewCommand.studentId != null) {
            return studentId.equals(otherViewCommand.studentId);
        }

        return false;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (targetIndex != null) {
            builder.add("targetIndex", targetIndex);
        }
        if (studentId != null) {
            builder.add("studentId", studentId);
        }
        return builder.toString();
    }
}

