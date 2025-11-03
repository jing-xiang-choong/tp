package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code RemarkCommand}.
 */
public class RemarkCommandTest {

    private static final String REMARK_STUB = "Some remark";

    private Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    public void execute_addRemarkUnfilteredList_success() throws Exception {
        Person student = new PersonBuilder()
                .withName("John Doe")
                .withStudentId("A0123456X")
                .withEmail("john@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();
        model.addPerson(student);

        StudentId studentId = student.getStudentId();
        Remark remark = new Remark(REMARK_STUB);
        RemarkCommand remarkCommand = new RemarkCommand(studentId, remark);

        CommandResult result = remarkCommand.execute(model);

        Person updatedStudent = model.findPersonByStudentId(studentId);
        Person expectedPerson = new PersonBuilder(student).withRemark(REMARK_STUB).build();

        assertTrue(result.getFeedbackToUser().contains("Added remark"));
        assertEquals(expectedPerson.getRemark(), updatedStudent.getRemark());
    }

    @Test
    public void execute_invalidStudentId_throwsCommandException() {
        StudentId invalidStudentId = new StudentId("A9999999Z");
        Remark remark = new Remark(REMARK_STUB);
        RemarkCommand remarkCommand = new RemarkCommand(invalidStudentId, remark);

        assertCommandFailure(remarkCommand, model,
                String.format(RemarkCommand.MESSAGE_STUDENT_NOT_FOUND, invalidStudentId));
    }

    @Test
    public void execute_multiLineRemark_success() throws Exception {
        Person student = new PersonBuilder()
                .withName("Jane Smith")
                .withStudentId("A1234567Y")
                .withEmail("jane@u.nus.edu")
                .withModuleCodes("CS2103T")
                .build();
        model.addPerson(student);

        StudentId studentId = student.getStudentId();
        String multiLineRemarkText = "Line 1\nLine 2\nLine 3";
        Remark multiLineRemark = new Remark(multiLineRemarkText);

        RemarkCommand remarkCommand = new RemarkCommand(studentId, multiLineRemark);

        CommandResult result = remarkCommand.execute(model);

        Person updatedStudent = model.findPersonByStudentId(studentId);

        assertTrue(result.getFeedbackToUser().contains("Added remark"));
        assertEquals(multiLineRemarkText, updatedStudent.getRemark().value);
    }

    @Test
    public void execute_replaceExistingRemark_success() throws Exception {
        Person student = new PersonBuilder()
                .withName("Bob Lee")
                .withStudentId("A2345678Z")
                .withEmail("bob@u.nus.edu")
                .withModuleCodes("CS2103T")
                .withRemark("Initial remark")
                .build();
        model.addPerson(student);

        StudentId studentId = student.getStudentId();
        Remark newRemark = new Remark("New remark");

        RemarkCommand remarkCommand = new RemarkCommand(studentId, newRemark);

        CommandResult result = remarkCommand.execute(model);

        Person updatedStudent = model.findPersonByStudentId(studentId);

        assertTrue(result.getFeedbackToUser().contains("Added remark"));
        assertEquals("New remark", updatedStudent.getRemark().value);
    }

    @Test
    public void equals() {
        StudentId studentId1 = new StudentId("A0123456X");
        StudentId studentId2 = new StudentId("A0234567Y");
        Remark remark1 = new Remark("First remark");
        Remark remark2 = new Remark("Second remark");

        RemarkCommand remarkCommand1 = new RemarkCommand(studentId1, remark1);
        RemarkCommand remarkCommand2 = new RemarkCommand(studentId1, remark1);
        RemarkCommand remarkCommand3 = new RemarkCommand(studentId2, remark1);
        RemarkCommand remarkCommand4 = new RemarkCommand(studentId1, remark2);

        // same object -> returns true
        assertTrue(remarkCommand1.equals(remarkCommand1));

        // same values -> returns true
        assertTrue(remarkCommand1.equals(remarkCommand2));

        // different types -> returns false
        assertFalse(remarkCommand1.equals(1));

        // null -> returns false
        assertFalse(remarkCommand1.equals(null));

        // different student ID -> returns false
        assertFalse(remarkCommand1.equals(remarkCommand3));

        // different remark -> returns false
        assertFalse(remarkCommand1.equals(remarkCommand4));
    }

    @Test
    public void toStringMethod() {
        StudentId studentId = new StudentId("A0123456X");
        Remark remark = new Remark("Test remark");
        RemarkCommand remarkCommand = new RemarkCommand(studentId, remark);
        String expected = RemarkCommand.class.getCanonicalName()
                + "{studentId=" + studentId + ", remark=" + remark + "}";
        assertEquals(expected, remarkCommand.toString());
    }
}
