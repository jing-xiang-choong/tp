package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     * For students (with studentId and no phone/address), generates student format command.
     * For regular persons, generates regular format command.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");

        // Check if this is a student (has studentId but no phone/address)
        if (person.getStudentId() != null && person.getPhone() == null && person.getAddress() == null) {
            // Student format: name, studentId, email, moduleCodes, tags
            sb.append(PREFIX_STUDENT_ID + person.getStudentId().value + " ");
            sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
            person.getModuleCodes().forEach(
                mc -> sb.append(PREFIX_MODULE_CODE + mc.value + " ")
            );
        } else {
            // Regular person format: name, phone, email, address, studentId (optional), moduleCodes, tags
            if (person.getPhone() != null) {
                sb.append(PREFIX_PHONE + person.getPhone().value + " ");
            }
            sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
            if (person.getAddress() != null) {
                sb.append(PREFIX_ADDRESS + person.getAddress().value + " ");
            }
            if (person.getStudentId() != null) {
                sb.append(PREFIX_STUDENT_ID + person.getStudentId().value + " ");
            }
            person.getModuleCodes().forEach(
                mc -> sb.append(PREFIX_MODULE_CODE + mc.value + " ")
            );
        }

        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        descriptor.getStudentId().ifPresent(studentId -> sb.append(PREFIX_STUDENT_ID)
                .append(studentId.value).append(" "));
        descriptor.getModuleCodes().ifPresent(moduleCodes -> moduleCodes.forEach(
                mc -> sb.append(PREFIX_MODULE_CODE).append(mc.value).append(" ")));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (!tags.isEmpty()) {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
