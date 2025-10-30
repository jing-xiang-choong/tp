package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.Week;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.PersonBuilder;

public class AttendanceCommandTest {

    private Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    public void constructor_validParameters_success() {
        StudentId studentId = new StudentId("A0123456X");
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;

        AttendanceCommand command = new AttendanceCommand(studentId, week, status);
        // Test that the command was created successfully
        assertTrue(command instanceof AttendanceCommand);
    }

    @Test
    public void constructor_markAllCommand_success() {
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;

        StudentId nullStudentId = null;
        AttendanceCommand command = new AttendanceCommand(nullStudentId, week, status);
        // Test that the command was created successfully
        assertTrue(command instanceof AttendanceCommand);
    }

    @Test
    public void execute_markSingleStudent_success() throws Exception {
        Person student = new PersonBuilder()
                .withName("John Doe")
                .withStudentId("A0123456X")
                .withEmail("john@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();
        model.addPerson(student);

        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        AttendanceCommand command = new AttendanceCommand(new StudentId("A0123456X"), week, status);

        CommandResult result = command.execute(model);

        assertEquals(String.format("✓ " + AttendanceCommand.MESSAGE_MARK_ATTENDANCE_SUCCESS,
                student.getName(), week.value, status), result.getFeedbackToUser());

        // Verify attendance was marked
        Person updatedStudent = model.findPersonByStudentId(new StudentId("A0123456X"));
        assertEquals(AttendanceStatus.PRESENT, updatedStudent.getAttendanceRecord().getAllAttendances().get(week));
    }

    @Test
    public void execute_markAllStudents_success() throws Exception {
        Person student1 = new PersonBuilder()
                .withName("John Doe")
                .withStudentId("A0123456X")
                .withEmail("john@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();
        Person student2 = new PersonBuilder()
                .withName("Jane Smith")
                .withStudentId("A1234567Y")
                .withEmail("jane@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();

        model.addPerson(student1);
        model.addPerson(student2);

        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        StudentId nullStudentId = null;
        AttendanceCommand command = new AttendanceCommand(nullStudentId, week, status);

        CommandResult result = command.execute(model);

        assertEquals(String.format("✓ " + AttendanceCommand.MESSAGE_MARK_ALL_SUCCESS,
                week.value, status, 2), result.getFeedbackToUser());

        // Verify attendance was marked for both students
        Person updatedStudent1 = model.findPersonByStudentId(new StudentId("A0123456X"));
        Person updatedStudent2 = model.findPersonByStudentId(new StudentId("A1234567Y"));
        assertEquals(AttendanceStatus.PRESENT, updatedStudent1.getAttendanceRecord().getAllAttendances().get(week));
        assertEquals(AttendanceStatus.PRESENT, updatedStudent2.getAttendanceRecord().getAllAttendances().get(week));
    }

    @Test
    public void execute_studentNotFound_throwsCommandException() {
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        AttendanceCommand command = new AttendanceCommand(new StudentId("A9999999Z"), week, status);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_updateExistingAttendance_success() throws Exception {
        Person student = new PersonBuilder()
                .withName("John Doe")
                .withStudentId("A0123456X")
                .withEmail("john@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();
        model.addPerson(student);

        // First mark attendance as present
        Week week = new Week(1);
        AttendanceCommand firstCommand = new AttendanceCommand(new StudentId("A0123456X"), week,
                AttendanceStatus.PRESENT);
        firstCommand.execute(model);

        // Then update to absent
        AttendanceStatus newStatus = AttendanceStatus.ABSENT;
        AttendanceCommand command = new AttendanceCommand(new StudentId("A0123456X"), week, newStatus);

        CommandResult result = command.execute(model);

        assertEquals(String.format("✓ Updated attendance for %s: Week %s (was: %s, now: %s)",
                student.getName(), week.value, AttendanceStatus.PRESENT, newStatus), result.getFeedbackToUser());

        // Verify attendance was updated
        Person updatedStudent = model.findPersonByStudentId(new StudentId("A0123456X"));
        assertEquals(AttendanceStatus.ABSENT, updatedStudent.getAttendanceRecord().getAllAttendances().get(week));
    }

    @Test
    public void constructor_indexBasedCommand_success() {
        Index index = INDEX_FIRST_PERSON;
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;

        AttendanceCommand command = new AttendanceCommand(index, week, status);
        // Test that the command was created successfully
        assertTrue(command instanceof AttendanceCommand);
    }

    @Test
    public void execute_markByIndex_success() throws Exception {
        Person student = new PersonBuilder()
                .withName("John Doe")
                .withStudentId("A0123456X")
                .withEmail("john@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();
        model.addPerson(student);

        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, week, status);

        CommandResult result = command.execute(model);

        assertEquals(String.format("✓ " + AttendanceCommand.MESSAGE_MARK_ATTENDANCE_SUCCESS,
                student.getName(), week.value, status), result.getFeedbackToUser());

        // Verify attendance was marked
        Person updatedStudent = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(AttendanceStatus.PRESENT, updatedStudent.getAttendanceRecord().getAllAttendances().get(week));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AttendanceCommand command = new AttendanceCommand(outOfBoundIndex, week, status);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ()
                -> command.execute(model));
    }

    @Test
    public void equals() {
        StudentId studentId1 = new StudentId("A0123456X");
        StudentId studentId2 = new StudentId("A1234567Y");
        Week week1 = new Week(1);
        Week week2 = new Week(2);
        AttendanceStatus status1 = AttendanceStatus.PRESENT;
        AttendanceStatus status2 = AttendanceStatus.ABSENT;

        AttendanceCommand command1 = new AttendanceCommand(studentId1, week1, status1);
        AttendanceCommand command2 = new AttendanceCommand(studentId1, week1, status1);
        AttendanceCommand command3 = new AttendanceCommand(studentId2, week1, status1);
        AttendanceCommand command4 = new AttendanceCommand(studentId1, week2, status1);
        AttendanceCommand command5 = new AttendanceCommand(studentId1, week1, status2);
        StudentId nullStudentId = null;
        AttendanceCommand command6 = new AttendanceCommand(nullStudentId, week1, status1);

        // Index-based commands
        AttendanceCommand command7 = new AttendanceCommand(INDEX_FIRST_PERSON, week1, status1);
        AttendanceCommand command8 = new AttendanceCommand(INDEX_FIRST_PERSON, week1, status1);
        AttendanceCommand command9 = new AttendanceCommand(INDEX_SECOND_PERSON, week1, status1);

        // Same object
        assertTrue(command1.equals(command1));

        // Same parameters
        assertTrue(command1.equals(command2));

        // Different student ID
        assertFalse(command1.equals(command3));

        // Different week
        assertFalse(command1.equals(command4));

        // Different status
        assertFalse(command1.equals(command5));

        // One null student ID
        assertFalse(command1.equals(command6));

        // Index-based: same parameters
        assertTrue(command7.equals(command8));

        // Index-based: different index
        assertFalse(command7.equals(command9));

        // Index-based vs student ID-based
        assertFalse(command1.equals(command7));

        // Different type
        assertFalse(command1.equals(week1));

        // Null
        assertFalse(command1.equals(null));
    }

    @Test
    public void toString_singleStudent() {
        StudentId studentId = new StudentId("A0123456X");
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        AttendanceCommand command = new AttendanceCommand(studentId, week, status);

        String actual = command.toString();
        assertTrue(actual.contains("targetStudentId=A0123456X"));
        assertTrue(actual.contains("week=Week 1"));
        assertTrue(actual.contains("status=present"));
    }

    @Test
    public void toString_markAll() {
        Week week = new Week(1);
        AttendanceStatus status = AttendanceStatus.PRESENT;
        StudentId nullStudentId = null;
        AttendanceCommand command = new AttendanceCommand(nullStudentId, week, status);

        String actual = command.toString();
        assertTrue(actual.contains("targetStudentId=null"));
        assertTrue(actual.contains("week=Week 1"));
        assertTrue(actual.contains("status=present"));
    }
}
