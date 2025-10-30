package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONSULTATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GRADE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.consultation.Consultation;
import seedu.address.model.grade.Grade;
import seedu.address.model.module.ModuleCode;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing student in TeachMate.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the student identified "
            + "by the index number used in the displayed student list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_STUDENT_ID + "STUDENT ID] "
            + "[" + PREFIX_MODULE_CODE + "MODULE CODE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "[" + PREFIX_CONSULTATION + "CONSULTATION]...\n"
            + "[" + PREFIX_GRADE + "ASSIGNMENT_NAME:SCORE] "
            + "[" + PREFIX_WEEK + "WEEK_NUMBER:STATUS] "
            + "[" + PREFIX_REMARK + "REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Updated student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This student already exists in TeachMate.";
    public static final String MESSAGE_DUPLICATE_STUDENT_ID =
            "Cannot update: Student ID %1$s is already assigned to another student.";
    public static final String MESSAGE_GRADE_NOT_FOUND =
            "Cannot update grade: Assignment '%1$s' not found for this student.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new CommandException(MESSAGE_NOT_EDITED);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        // Check for duplicate student ID if the student ID is being changed
        if (editedPerson.getStudentId() != null && personToEdit.getStudentId() != null) {
            if (!editedPerson.getStudentId().equals(personToEdit.getStudentId())) {
                if (model.getPersonByStudentId(editedPerson.getStudentId()).isPresent()) {
                    throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT_ID,
                            editedPerson.getStudentId()));
                }
            }
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        String editedFieldsMessage = buildEditedFieldsMessage(personToEdit, editedPerson, editPersonDescriptor);
        return new CommandResult(Messages.successMessage(String.format(MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.formatStudentId(editedPerson)) + "\n" + editedFieldsMessage));
    }

    /**
     * Builds a message detailing which fields were edited and their new values.
     */
    private String buildEditedFieldsMessage(Person original, Person edited, EditPersonDescriptor descriptor) {
        StringBuilder message = new StringBuilder("\nEdited fields:");

        if (descriptor.getName().isPresent()) {
            message.append("\n  • Name: ").append(edited.getName());
        }
        if (descriptor.getPhone().isPresent()) {
            message.append("\n  • Phone: ").append(edited.getPhone());
        }
        if (descriptor.getEmail().isPresent()) {
            message.append("\n  • Email: ").append(edited.getEmail());
        }
        if (descriptor.getAddress().isPresent()) {
            message.append("\n  • Address: ").append(edited.getAddress());
        }
        if (descriptor.getStudentId().isPresent()) {
            message.append("\n  • Student ID: ").append(edited.getStudentId());
        }
        if (descriptor.getModuleCodes().isPresent()) {
            message.append("\n  • Module Codes: ").append(edited.getModuleCodes());
        }
        if (descriptor.getTags().isPresent()) {
            message.append("\n  • Tags: ").append(edited.getTags());
        }
        if (descriptor.getConsultations().isPresent()) {
            if (edited.getConsultations() == null || edited.getConsultations().isEmpty()) {
                message.append("\n  • Consultations: None");
            } else {
                message.append("\n  • Consultations: ").append(edited.getConsultations());
            }
        }
        if (descriptor.getGrade().isPresent()) {
            Grade grade = descriptor.getGrade().get();
            message.append("\n  • Grade updated: ").append(grade.assignmentName)
                   .append(" → ").append(grade.score);
        }
        if (descriptor.getAttendance().isPresent()) {
            Attendance attendance = descriptor.getAttendance().get();
            if (attendance.getStatus() == AttendanceStatus.UNMARK) {
                message.append("\n  • Attendance unmarked: Week ").append(attendance.getWeek().value);
            } else {
                message.append("\n  • Attendance: Week ").append(attendance.getWeek().value)
                       .append(" → ").append(attendance.getStatus());
            }
        }
        if (descriptor.getRemark().isPresent()) {
            message.append("\n  • Remark: ").append(edited.getRemark());
        }

        return message.toString();
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        StudentId updatedStudentId = editPersonDescriptor.getStudentId().orElse(personToEdit.getStudentId());
        Set<ModuleCode> updatedModuleCodes = editPersonDescriptor.getModuleCodes()
                    .orElse(personToEdit.getModuleCodes());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        List<Consultation> updatedConsultations =
                editPersonDescriptor.getConsultations().orElse(personToEdit.getConsultations());

        // Handle grade update
        Set<Grade> updatedGrades = new HashSet<>(personToEdit.getGrades());
        if (editPersonDescriptor.getGrade().isPresent()) {
            Grade gradeToUpdate = editPersonDescriptor.getGrade().get();
            // Find and remove the existing grade with the same assignment name
            boolean gradeFound = false;
            for (Grade existingGrade : updatedGrades) {
                if (existingGrade.assignmentName.equals(gradeToUpdate.assignmentName)) {
                    updatedGrades.remove(existingGrade);
                    gradeFound = true;
                    break;
                }
            }
            if (!gradeFound) {
                throw new CommandException(String.format(MESSAGE_GRADE_NOT_FOUND, gradeToUpdate.assignmentName));
            }
            updatedGrades.add(gradeToUpdate);
        }

        // Handle attendance update
        AttendanceRecord updatedAttendanceRecord = personToEdit.getAttendanceRecord();
        if (editPersonDescriptor.getAttendance().isPresent()) {
            Attendance attendanceToUpdate = editPersonDescriptor.getAttendance().get();
            if (attendanceToUpdate.getStatus() == AttendanceStatus.UNMARK) {
                updatedAttendanceRecord = updatedAttendanceRecord.unmarkAttendance(attendanceToUpdate.getWeek());
            } else {
                updatedAttendanceRecord = updatedAttendanceRecord.markAttendance(
                        attendanceToUpdate.getWeek(), attendanceToUpdate.getStatus());
            }
        }

        // Handle remark update
        Remark updatedRemark = editPersonDescriptor.getRemark().orElse(personToEdit.getRemark());

        // Check if this is a student (has studentId but no phone/address)
        if (updatedStudentId != null && updatedPhone == null && updatedAddress == null) {
            // Use student constructor
            return new Person(updatedName, updatedStudentId,
                    updatedEmail, updatedModuleCodes, updatedTags, updatedAttendanceRecord, updatedGrades,
                    updatedConsultations, updatedRemark);
        } else {
            // Use regular person constructor
            return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags,
                    updatedStudentId, updatedModuleCodes, updatedGrades);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private StudentId studentId;
        private Set<ModuleCode> moduleCodes;
        private Set<Tag> tags;
        private List<Consultation> consultations;
        private Grade grade;
        private Attendance attendance;
        private Remark remark;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setStudentId(toCopy.studentId);
            setModuleCodes(toCopy.moduleCodes);
            setConsultations(toCopy.consultations);
            setGrade(toCopy.grade);
            setAttendance(toCopy.attendance);
            setRemark(toCopy.remark);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags, studentId, moduleCodes,
                    consultations, grade, attendance, remark);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setStudentId(StudentId studentId) {
            this.studentId = studentId;
        }

        public Optional<StudentId> getStudentId() {
            return Optional.ofNullable(studentId);
        }

        public void setModuleCodes(Set<ModuleCode> moduleCodes) {
            this.moduleCodes = (moduleCodes != null) ? new HashSet<>(moduleCodes) : null;
        }

        public Optional<Set<ModuleCode>> getModuleCodes() {
            return Optional.ofNullable(moduleCodes);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public Optional<List<Consultation>> getConsultations() {
            return Optional.ofNullable(consultations);
        }

        public void setConsultations(List<Consultation> consultations) {
            this.consultations = consultations;
        }

        public Optional<Grade> getGrade() {
            return Optional.ofNullable(grade);
        }

        public void setGrade(Grade grade) {
            this.grade = grade;
        }

        public Optional<Attendance> getAttendance() {
            return Optional.ofNullable(attendance);
        }

        public void setAttendance(Attendance attendance) {
            this.attendance = attendance;
        }

        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(studentId, otherEditPersonDescriptor.studentId)
                    && Objects.equals(moduleCodes, otherEditPersonDescriptor.moduleCodes)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("student Id", studentId)
                    .add("module codes", moduleCodes)
                    .add("tags", tags)
                    .toString();
        }
    }
}
