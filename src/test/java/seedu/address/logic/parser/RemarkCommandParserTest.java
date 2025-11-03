package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;

/**
 * Contains unit tests for {@code RemarkCommandParser}.
 */
public class RemarkCommandParserTest {

    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        StudentId expectedStudentId = new StudentId("A0123456X");
        Remark expectedRemark = new Remark("Some remark");

        // standard format
        assertParseSuccess(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Some remark",
                new RemarkCommand(expectedStudentId, expectedRemark));
    }

    @Test
    public void parse_multiLineRemark_success() {
        StudentId expectedStudentId = new StudentId("A0123456X");
        Remark expectedRemark = new Remark("Line 1\nLine 2");

        assertParseSuccess(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Line 1\nLine 2",
                new RemarkCommand(expectedStudentId, expectedRemark));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // all prefixes missing
        assertParseFailure(parser, "A0123456X Some remark",
                expectedMessage);

        // missing remark value
        assertParseFailure(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK,
                Remark.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid student ID format - starts with B
        assertParseFailure(parser, " " + PREFIX_STUDENT_ID + "B0123456X " + PREFIX_REMARK + "Some remark",
                StudentId.MESSAGE_CONSTRAINTS);

        // invalid student ID - too short
        assertParseFailure(parser, " " + PREFIX_STUDENT_ID + "A012345X " + PREFIX_REMARK + "Some remark",
                StudentId.MESSAGE_CONSTRAINTS);

        // invalid remark - whitespace only
        assertParseFailure(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "   ",
                Remark.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_conflictingParameters_failure() {
        // both index (preamble) and student ID provided
        assertParseFailure(parser, "1 " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Some remark",
                "Conflicting parameters detected. Please use either index or student ID — not both.");

        // preamble with text and student ID
        assertParseFailure(parser, "some text " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Some remark",
                "Conflicting parameters detected. Please use either index or student ID — not both.");
    }

    @Test
    public void parse_validArgsWithSpecialCharacters_success() {
        StudentId expectedStudentId = new StudentId("A0123456X");

        // remark with punctuation
        Remark remarkWithPunctuation = new Remark("Great work! Keep it up.");
        assertParseSuccess(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Great work! Keep it up.",
                new RemarkCommand(expectedStudentId, remarkWithPunctuation));

        // remark with numbers
        Remark remarkWithNumbers = new Remark("Scored 85 in midterm");
        assertParseSuccess(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Scored 85 in midterm",
                new RemarkCommand(expectedStudentId, remarkWithNumbers));

        // remark with mixed case
        Remark remarkWithMixedCase = new Remark("Needs Help With OOP");
        assertParseSuccess(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + "Needs Help With OOP",
                new RemarkCommand(expectedStudentId, remarkWithMixedCase));
    }

    @Test
    public void parse_longRemark_success() {
        StudentId expectedStudentId = new StudentId("A0123456X");
        String longRemarkText = "This is a very long remark that contains a lot of detailed information "
                + "about the student's progress, including their strengths, weaknesses, and areas for improvement. "
                + "The student has shown significant progress in understanding object-oriented programming concepts.";
        Remark longRemark = new Remark(longRemarkText);

        assertParseSuccess(parser, " " + PREFIX_STUDENT_ID + "A0123456X " + PREFIX_REMARK + longRemarkText,
                new RemarkCommand(expectedStudentId, longRemark));
    }
}
