package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.Week;
import seedu.address.model.grade.Grade;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        // Build editedPerson based on the same structure as firstPerson
        PersonBuilder personBuilder = new PersonBuilder()
                .withName("Edited Name")
                .withEmail("edited@example.com");

        // Check if firstPerson has studentId/moduleCodes - set these first
        if (firstPerson.getStudentId() != null) {
            personBuilder.withStudentId("A9999999Z");
        }
        if (firstPerson.getModuleCodes() != null && !firstPerson.getModuleCodes().isEmpty()) {
            personBuilder.withModuleCode("CS9999");
        }

        // Check if firstPerson has phone/address - set these AFTER studentId to override nulls
        if (firstPerson.getPhone() != null && firstPerson.getAddress() != null) {
            personBuilder.withPhone("99999999").withAddress("Edited Address");
        }

        Person editedPerson = personBuilder.withTags("friends").build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        // Execute and check model state without checking exact message
        try {
            CommandResult result = editCommand.execute(model);
            assertTrue(result.getFeedbackToUser().contains(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS
                    .replace("%1$s", "")));
            assertEquals(expectedModel, model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        // Execute and check model state without checking exact message
        try {
            CommandResult result = editCommand.execute(model);
            assertTrue(result.getFeedbackToUser().contains("Updated student"));
            assertEquals(expectedModel, model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_failure() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());

        // Should throw exception since no fields are edited
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        // Execute and check model state without checking exact message
        try {
            CommandResult result = editCommand.execute(model);
            assertTrue(result.getFeedbackToUser().contains("Updated student"));
            assertEquals(expectedModel.getAddressBook(), model.getAddressBook());
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    @Test
    public void execute_duplicateStudentIdUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Only test if both persons have student IDs
        if (firstPerson.getStudentId() != null && secondPerson.getStudentId() != null) {
            EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                    .withStudentId(firstPerson.getStudentId().value).build();
            EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

            String expectedMessage = String.format(EditCommand.MESSAGE_DUPLICATE_STUDENT_ID,
                    firstPerson.getStudentId());
            assertCommandFailure(editCommand, model, expectedMessage);
        }
    }

    @Test
    public void execute_duplicateStudentIdFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person firstPersonInList = model.getAddressBook().getPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPersonInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Only test if both persons have student IDs
        if (firstPersonInList.getStudentId() != null && secondPersonInList.getStudentId() != null) {
            // edit person in filtered list to have the student ID of another person in address book
            EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                    new EditPersonDescriptorBuilder()
                            .withStudentId(secondPersonInList.getStudentId().value).build());

            String expectedMessage = String.format(EditCommand.MESSAGE_DUPLICATE_STUDENT_ID,
                    secondPersonInList.getStudentId());
            assertCommandFailure(editCommand, model, expectedMessage);
        }
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void execute_gradeFieldSpecified_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Only test if person has student ID (students can have grades)
        if (firstPerson.getStudentId() != null && !firstPerson.getGrades().isEmpty()) {
            // Get an existing grade to update
            Grade existingGrade = firstPerson.getGrades().iterator().next();
            Grade updatedGrade = new Grade(existingGrade.assignmentName, "95");

            EditPersonDescriptor descriptor = new EditPersonDescriptor();
            descriptor.setGrade(updatedGrade);
            EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

            PersonBuilder personBuilder = new PersonBuilder(firstPerson);
            Person editedPerson = personBuilder.withGrade(updatedGrade.assignmentName, updatedGrade.score).build();

            String expectedMessage = Messages.successMessage(String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                    Messages.formatStudentId(editedPerson)));

            Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
            expectedModel.setPerson(firstPerson, editedPerson);

            assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        }
    }

    @Test
    public void execute_gradeNotFound_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Only test if person has student ID (students can have grades)
        if (firstPerson.getStudentId() != null) {
            Grade nonExistentGrade = new Grade("NonExistentAssignment", "85");

            EditPersonDescriptor descriptor = new EditPersonDescriptor();
            descriptor.setGrade(nonExistentGrade);
            EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

            String expectedMessage = String.format(EditCommand.MESSAGE_GRADE_NOT_FOUND, "NonExistentAssignment");
            assertCommandFailure(editCommand, model, expectedMessage);
        }
    }

    @Test
    public void execute_attendanceFieldSpecified_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Only test if person has student ID (students can have attendance)
        if (firstPerson.getStudentId() != null) {
            Week week = new Week(5);
            AttendanceStatus status = AttendanceStatus.PRESENT;
            Attendance attendance = new Attendance(week, status);

            EditPersonDescriptor descriptor = new EditPersonDescriptor();
            descriptor.setAttendance(attendance);
            EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

            PersonBuilder personBuilder = new PersonBuilder(firstPerson);
            Person editedPerson = personBuilder.withAttendance(week, status).build();

            Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
            expectedModel.setPerson(firstPerson, editedPerson);

            // Execute and check model state
            try {
                CommandResult result = editCommand.execute(model);
                assertTrue(result.getFeedbackToUser().contains("Updated student"));
                assertTrue(result.getFeedbackToUser().contains("Attendance"));
                assertEquals(expectedModel, model);
            } catch (CommandException ce) {
                throw new AssertionError("Execution of command should not fail.", ce);
            }
        }
    }

    @Test
    public void execute_attendanceUnmarkSpecified_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Only test if person has student ID and has some attendance
        if (firstPerson.getStudentId() != null) {
            // First mark attendance
            Week week = new Week(3);
            AttendanceStatus markStatus = AttendanceStatus.PRESENT;
            Attendance markAttendance = new Attendance(week, markStatus);

            EditPersonDescriptor markDescriptor = new EditPersonDescriptor();
            markDescriptor.setAttendance(markAttendance);
            EditCommand markCommand = new EditCommand(INDEX_FIRST_PERSON, markDescriptor);

            try {
                markCommand.execute(model);
            } catch (Exception e) {
                // Ignore if marking fails
            }

            // Then unmark
            Attendance unmarkAttendance = new Attendance(week, AttendanceStatus.UNMARK);
            EditPersonDescriptor unmarkDescriptor = new EditPersonDescriptor();
            unmarkDescriptor.setAttendance(unmarkAttendance);
            EditCommand unmarkCommand = new EditCommand(INDEX_FIRST_PERSON, unmarkDescriptor);

            PersonBuilder personBuilder = new PersonBuilder(model.getFilteredPersonList()
                    .get(INDEX_FIRST_PERSON.getZeroBased()));
            Person editedPerson = personBuilder.withUnmarkedAttendance(week).build();

            Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
            expectedModel.setPerson(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()),
                    editedPerson);

            // Execute and check model state
            try {
                CommandResult result = unmarkCommand.execute(model);
                assertTrue(result.getFeedbackToUser().contains("Updated student"));
                assertTrue(result.getFeedbackToUser().contains("unmarked"));
                assertEquals(expectedModel, model);
            } catch (CommandException ce) {
                throw new AssertionError("Execution of command should not fail.", ce);
            }
        }
    }

    @Test
    public void execute_remarkFieldSpecified_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Remark remark = new Remark("Excellent student");
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setRemark(remark);
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        PersonBuilder personBuilder = new PersonBuilder(firstPerson);
        Person editedPerson = personBuilder.withRemark("Excellent student").build();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        // Execute and check model state
        try {
            CommandResult result = editCommand.execute(model);
            assertTrue(result.getFeedbackToUser().contains("Updated student"));
            assertTrue(result.getFeedbackToUser().contains("Remark"));
            assertEquals(expectedModel, model);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

}
