package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.Week;
import seedu.address.model.consultation.Consultation;
import seedu.address.model.grade.Grade;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                    PREFIX_STUDENT_ID, PREFIX_MODULE_CODE, PREFIX_CONSULTATION, PREFIX_GRADE, PREFIX_WEEK,
                    PREFIX_REMARK);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
            PREFIX_STUDENT_ID);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_STUDENT_ID).isPresent()) {
            editPersonDescriptor.setStudentId(ParserUtil.parseStudentId(argMultimap.getValue(PREFIX_STUDENT_ID).get()));
        }
        if (!argMultimap.getAllValues(PREFIX_MODULE_CODE).isEmpty()) {
            editPersonDescriptor.setModuleCodes(ParserUtil.parseModuleCodes(
                    argMultimap.getAllValues(PREFIX_MODULE_CODE)));
        }
        if (argMultimap.getValue(PREFIX_CONSULTATION).isPresent()) {
            List<Consultation> consultations = new ArrayList<>();
            for (String consultationString : argMultimap.getAllValues(PREFIX_CONSULTATION)) {
                consultations.add(new Consultation(ParserUtil.parseDateTime(consultationString)));
            }
            // Remove duplicate consultations
            consultations = consultations.stream().distinct().collect(java.util.stream.Collectors.toList());
            editPersonDescriptor.setConsultations(consultations);
        }
        if (argMultimap.getValue(PREFIX_GRADE).isPresent()) {
            String gradeString = argMultimap.getValue(PREFIX_GRADE).get();
            Grade grade = ParserUtil.parseGrade(gradeString);
            editPersonDescriptor.setGrade(grade);
        }
        if (argMultimap.getValue(PREFIX_WEEK).isPresent()) {
            String weekString = argMultimap.getValue(PREFIX_WEEK).get();
            String[] parts = weekString.split(":", 2);
            if (parts.length != 2) {
                throw new ParseException("Attendance format should be WEEK_NUMBER:STATUS");
            }
            Week week = ParserUtil.parseWeek(parts[0].trim());
            AttendanceStatus status = ParserUtil.parseAttendanceStatus(parts[1].trim());
            Attendance attendance = new Attendance(week, status);
            editPersonDescriptor.setAttendance(attendance);
        }
        if (argMultimap.getValue(PREFIX_REMARK).isPresent()) {
            editPersonDescriptor.setRemark(ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * Throws ParseException if any tag is empty.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseTags(tags));
    }

}
