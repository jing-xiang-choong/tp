package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.Week;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;

/**
 * Marks attendance for a student identified using their displayed index or student ID.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks attendance for a student identified by the index number, student ID, or all students.\n"
            + "Parameters: INDEX " + PREFIX_WEEK + "WEEK present|absent|unmark OR "
            + PREFIX_STUDENT_ID + "STUDENT_ID " + PREFIX_WEEK + "WEEK present|absent|unmark OR "
            + "all " + PREFIX_WEEK + "WEEK present|absent|unmark\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_WEEK + "1 present OR "
            + COMMAND_WORD + " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_WEEK + "1 absent OR "
            + COMMAND_WORD + " all " + PREFIX_WEEK + "1 unmark";

    public static final String MESSAGE_MARK_ATTENDANCE_SUCCESS = "Marked attendance for %1$s: Week %2$s - %3$s";
    public static final String MESSAGE_MARK_ALL_SUCCESS = "Marked attendance for all students: "
            + "Week %1$s - %2$s (%3$d students)";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student found with ID: %1$s";

    private final Index targetIndex;
    private final StudentId targetStudentId;
    private final Week week;
    private final AttendanceStatus status;

    /**
     * Creates an AttendanceCommand to mark the attendance of the student at the specified index.
     */
    public AttendanceCommand(Index targetIndex, Week week, AttendanceStatus status) {
        requireNonNull(targetIndex);
        requireNonNull(week);
        requireNonNull(status);
        this.targetIndex = targetIndex;
        this.targetStudentId = null;
        this.week = week;
        this.status = status;
    }

    /**
     * Creates an AttendanceCommand to mark the attendance of the specified student by student ID.
     * If targetStudentId is null, marks attendance for all students.
     */
    public AttendanceCommand(StudentId targetStudentId, Week week, AttendanceStatus status) {
        requireNonNull(week);
        requireNonNull(status);
        this.targetIndex = null;
        this.targetStudentId = targetStudentId; // Can be null for "mark all"
        this.week = week;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // Check if this is a "mark all" command (targetStudentId is null in both constructors for mark all)
        if (targetIndex == null && targetStudentId == null) {
            return executeMarkAll(model);
        } else if (targetIndex != null) {
            return executeMarkByIndex(model);
        } else {
            return executeMarkByStudentId(model);
        }
    }

    /**
     * Executes the command to mark attendance for a single student by index.
     */
    private CommandResult executeMarkByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person studentToMarkAttendance = lastShownList.get(targetIndex.getZeroBased());

        return markAttendanceForStudent(model, studentToMarkAttendance);
    }

    /**
     * Executes the command to mark attendance for a single student by student ID.
     */
    private CommandResult executeMarkByStudentId(Model model) throws CommandException {
        Person studentToMarkAttendance = model.findPersonByStudentId(targetStudentId);

        if (studentToMarkAttendance == null) {
            throw new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, targetStudentId));
        }

        return markAttendanceForStudent(model, studentToMarkAttendance);
    }

    /**
     * Marks attendance for the given student and returns the command result.
     */
    private CommandResult markAttendanceForStudent(Model model, Person student) {
        // Check if attendance already exists for this week
        AttendanceStatus previousStatus = student.getAttendanceRecord().getAllAttendances().get(week);

        // Handle unmark operation differently
        AttendanceRecord updatedAttendanceRecord;
        if (status == AttendanceStatus.UNMARK) {
            updatedAttendanceRecord = student.getAttendanceRecord().unmarkAttendance(week);
        } else {
            updatedAttendanceRecord = student.getAttendanceRecord().markAttendance(week, status);
        }

        // Create updated person with new attendance record
        Person updatedStudent = new Person(
                student.getName(),
                student.getStudentId(),
                student.getEmail(),
                student.getModuleCodes(),
                student.getTags(),
                updatedAttendanceRecord,
                student.getGrades(),
                student.getConsultations(),
                student.getRemark()
        );

        // Update the student in the model
        model.setPerson(student, updatedStudent);

        // Generate appropriate message based on whether attendance was updated or newly marked
        String message;
        if (status == AttendanceStatus.UNMARK) {
            message = String.format("✓ Unmarked attendance for %s: Week %s", updatedStudent.getName(), week.value);
        } else if (previousStatus != null) {
            // Attendance was updated
            message = String.format("✓ Updated attendance for %s: Week %s (was: %s, now: %s)",
                    updatedStudent.getName(), week.value, previousStatus, status);
        } else {
            // Attendance was newly marked
            message = String.format("✓ " + MESSAGE_MARK_ATTENDANCE_SUCCESS,
                    updatedStudent.getName(), week.value, status);
        }

        return new CommandResult(message);
    }

    /**
     * Executes the command to mark attendance for all students.
     */
    private CommandResult executeMarkAll(Model model) throws CommandException {
        List<Person> allStudents = model.getAddressBook().getPersonList();
        int markedCount = 0;

        for (Person student : allStudents) {
            // Handle unmark operation differently
            AttendanceRecord updatedAttendanceRecord;
            if (status == AttendanceStatus.UNMARK) {
                updatedAttendanceRecord = student.getAttendanceRecord().unmarkAttendance(week);
            } else {
                updatedAttendanceRecord = student.getAttendanceRecord().markAttendance(week, status);
            }

            // Create updated person with new attendance record
            Person updatedStudent = new Person(
                    student.getName(),
                    student.getStudentId(),
                    student.getEmail(),
                    student.getModuleCodes(),
                    student.getTags(),
                    updatedAttendanceRecord,
                    student.getGrades(),
                    student.getConsultations(),
                    student.getRemark()
            );

            // Update the student in the model
            model.setPerson(student, updatedStudent);
            markedCount++;
        }

        String message = status == AttendanceStatus.UNMARK
                ? String.format("✓ Unmarked attendance for all students: Week %s (%d students)",
                        week.value, markedCount)
                : String.format("✓ " + MESSAGE_MARK_ALL_SUCCESS, week.value, status, markedCount);

        return new CommandResult(message);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AttendanceCommand)) {
            return false;
        }

        AttendanceCommand otherAttendanceCommand = (AttendanceCommand) other;

        // Check if both use index-based marking
        if (targetIndex != null && otherAttendanceCommand.targetIndex != null) {
            return targetIndex.equals(otherAttendanceCommand.targetIndex)
                    && week.equals(otherAttendanceCommand.week)
                    && status.equals(otherAttendanceCommand.status);
        }

        // Check if both use student ID-based marking (or mark all when both are null)
        if (targetIndex == null && otherAttendanceCommand.targetIndex == null) {
            boolean studentIdsEqual = (targetStudentId == null && otherAttendanceCommand.targetStudentId == null)
                    || (targetStudentId != null && targetStudentId.equals(otherAttendanceCommand.targetStudentId));
            return studentIdsEqual
                    && week.equals(otherAttendanceCommand.week)
                    && status.equals(otherAttendanceCommand.status);
        }

        // One uses index, the other uses student ID
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("targetStudentId", targetStudentId)
                .add("week", week)
                .add("status", status)
                .toString();
    }
}
