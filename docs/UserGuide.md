---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TeachMate User Guide

TeachMate is a **desktop app for Teaching Assistants (TAs) to manage university students, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, TeachMate can help you manage your students faster than traditional GUI apps.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2425S1-CS2103T-T12-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TeachMate.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar addressbook.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all students.

   * `add n/John Doe s/A0123456X e/johnd@u.nus.edu m/CS2103T` : Adds a student named `John Doe` with student ID `A0123456X` to TeachMate.

   * `delete 3` : Deletes the 3rd student shown in the current list.

   * `clear` : Deletes all students.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/struggling` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/struggling`, `t/struggling t/needsHelp` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME s/STUDENT_ID`, `s/STUDENT_ID n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student: `add`

Adds a student to TeachMate.

Format: `add n/NAME s/STUDENT_ID e/EMAIL m/MODULE_CODE [m/MODULE_CODE]…​ [t/TAG]…​ [c/CONSULTATIONS]`

<box type="info" seamless>

**Parameter Constraints:**
* `NAME` should only contain alphanumeric characters and spaces, and should not be blank
* `STUDENT_ID` must be in the format A followed by exactly 7 digits and 1 uppercase letter (e.g., A0123456X)
* `EMAIL` should be of the format local-part@domain (see detailed constraints below)
* `MODULE_CODE` must be in NUS format: 2-3 uppercase letters, followed by 4 digits, optionally ending with 1 uppercase letter (e.g., CS2103T, CS2101)
* `TAG` should be alphanumeric (no spaces)
* At least one module code is required
* Tags are optional
* Consultations are optional, if provided it should follow one of the supported formats listed below
</box>

<box type="tip" seamless>

**Email Constraints:**
* The local-part should only contain alphanumeric characters and these special characters: `+ _ . -`
* The local-part may not start or end with special characters
* The domain name must end with a domain label at least 2 characters long
* Each domain label must start and end with alphanumeric characters
* Each domain label may contain hyphens between alphanumeric characters
</box>

<box type="tip" seamless>

## Consultations Constraints

- **Consultations are optional** — a student can be added without any consultations.
- **If included**, each consultation’s date and time must follow **one of the supported formats** below:

| Accepted Format        | Example Input       |
|------------------------|---------------------|
| `dd/MM/yyyy HH:mm`     | `22/10/2025 15:30`  |
| `dd-MM-yyyy HH:mm`     | `22-10-2025 15:30`  |
| `yyyy-MM-dd HH:mm`     | `2025-10-22 15:30`  |
| `yyyy/MM/dd HH:mm`     | `2025/10/22 15:30`  |

### Notes
- The time must be entered in **24-hour format** (e.g., `14:00` for 2 PM).
- All formats require both a **date** and a **time** — partial inputs like `22/10/2025` or `15:30` alone are not accepted.
- Invalid inputs (e.g., `2025-13-45 25:99`) will result in an error message showing the supported formats.
</box>


Examples:
* `add n/John Doe s/A0123456X e/johnd@u.nus.edu m/CS2103T`
* `add n/Jane Smith s/A0234567Y e/janes@u.nus.edu m/CS2103T m/CS2101 t/struggling t/needsHelp c/22/10/2025 15:30 `

### Listing students : `list`

Shows a list of all students in TeachMate, or filters students by module code.

Format:
* `list` - Lists all students
* `list m/MODULE_CODE` - Lists students in a specific module

Examples:
* `list` - Shows all students
* `list m/CS2103T` - Shows all students taking CS2103T

### Viewing a student : `view`

Views detailed information and attendance records about a student identified by their index or student ID.

Format:
* `view INDEX` - View student by list index
* `view s/STUDENT_ID` - View student by student ID

* Views the student at the specified `INDEX` or with the specified `STUDENT_ID`
* The index refers to the index number shown in the displayed student list
* The index **must be a positive integer** 1, 2, 3, …​
* Student ID must match the format A followed by 7 digits and 1 uppercase letter (e.g., A0123456X)
* The student must exist in TeachMate
* The display will automatically filter to show only the selected student

**What will be displayed:**

The view command displays comprehensive information in three sections:

1. **Student Details:**
   * Full name
   * Student ID
   * Email address
   * All enrolled module codes (comma-separated)
   * Tags (if any, comma-separated)
   * Grades (if any, in format "assignment: score")
   * Remark (if any)

2. **Attendance Record:**
   * Week-by-week attendance status for all recorded weeks
   * Each entry shows: "Week [number]: [symbol] [status]"
   * Present weeks marked with: ✓ Present
   * Absent weeks marked with: ✗ Absent
   * Weeks are displayed in ascending order (Week 1, Week 2, etc.)
   * If no attendance has been recorded yet, displays: "No attendance recorded yet."

3. **Consultation Records:**
   * All scheduled consultation sessions with date and time
   * Displayed in chronological order
   * Format: "• [date] [time]" (e.g., "• 26 Oct 2025 4:00pm")
   * If no consultations are scheduled, displays: "No consultations recorded yet."

**Output format:**
```
=== STUDENT DETAILS ===
Name: [name]
Student ID: [student ID]
Email: [email]
Modules: [module codes]
Tags: [tags]
Grades: [grades]
Remark: [remark]

=== ATTENDANCE RECORD ===
Week [number]: [symbol] [status]
...

=== CONSULTATION RECORDS ===
• [consultation date and time]
...
```

**Error messages:**
* If the index is invalid: `The student index provided is invalid`
* If the student ID is not found: `No student found with ID: [ID]`

<box type="tip" seamless>

**Tip:** After viewing a student, use the `list` command to see all students again.
</box>

Examples:
* `view 1` - Views the 1st student in the current list and filters the display to show only that student
* `view s/A0123456X` - Views the student with student ID A0123456X

**Example output:**
```
=== STUDENT DETAILS ===
Name: John Doe
Student ID: A0123456X
Email: johnd@u.nus.edu
Modules: CS2103T, CS2101
Tags: struggling, needsHelp
Grades: Midterm: 85, Quiz1: 90
Remark: Very active in class discussions

=== ATTENDANCE RECORD ===
Week 1: ✓ Present
Week 2: ✗ Absent
Week 3: ✓ Present

=== CONSULTATION RECORDS ===
• 26 Oct 2025 4:00pm
• 03 Nov 2025 2:30pm
```

### Marking attendance : `attendance`

Marks attendance for a student for a specific week.

Format: `attendance INDEX w/WEEK present|absent|unmark` or `attendance s/STUDENT_ID w/WEEK present|absent|unmark` or `attendance all w/WEEK present|absent|unmark`

* Marks attendance for the student at the specified `INDEX`, with the specified `STUDENT_ID`, or all students
* `INDEX` refers to the index number shown in the displayed student list (must be a positive integer)
* `WEEK` must be a number between 1 and 13 (inclusive), representing the teaching week
* `present|absent|unmark` specifies the attendance status (case-insensitive):
  - `present`: Mark as present
  - `absent`: Mark as absent
  - `unmark`: Remove attendance record (return to unmarked state)
* The status must come **after** the week parameter
* Use `all` (without `s/` prefix) to mark all students at once for bulk operations
* The student must exist in TeachMate
* Marking attendance for the same week again will update the previous record
* Using `unmark` on an already unmarked week has no effect

**Success message:**
* Individual: `Marked attendance for [name]: Week [week] - [status]` or `Unmarked attendance for [name]: Week [week]`
* All students: `Marked attendance for all students: Week [week] - [status] ([count] students)` or `Unmarked attendance for all students: Week [week] ([count] students)`

**Error messages:**
* If the index is invalid: `The student index provided is invalid`
* If the student ID is not found: `No student found with ID: [ID]`
* If the week is invalid: `Week should be a number between 1 and 13 (inclusive)`
* If the status is invalid: `Invalid attendance status. Use 'present', 'absent', or 'unmark'.`

<box type="tip" seamless>

**Tips:**
* Attendance can be viewed in detail using the `view` command, which shows week-by-week records
* When marking all students, the command affects every student in TeachMate regardless of the current filtered list
* Present attendance is marked with ✓ and absent with ✗ in the attendance record
* Unmarked attendance appears as grey boxes in the UI (no record)
* Using index is faster when working with the displayed list
* Use `unmark` to correct mistakes or reset attendance for a specific week
</box>

Examples:
* `attendance 1 w/1 present` - Marks the first student in the list as present for week 1
* `attendance 2 w/2 absent` - Marks the second student in the list as absent for week 2
* `attendance 1 w/1 unmark` - Unmarks attendance for the first student for week 1
* `attendance s/A0123456X w/1 present` - Marks student A0123456X as present for week 1
* `attendance s/A0123456X w/2 absent` - Marks student A0123456X as absent for week 2
* `attendance s/A0123456X w/2 unmark` - Unmarks attendance for student A0123456X for week 2
* `attendance all w/1 present` - Marks all students as present for week 1
* `attendance all w/2 absent` - Marks all students as absent for week 2
* `attendance all w/3 unmark` - Unmarks attendance for all students for week 3

### Editing a student : `edit`

Edits an existing student in TeachMate. You can edit students whether they have phone/address (legacy data) or only have student-specific fields.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/STUDENT_ID] [m/MODULE_CODE]​ [t/TAG] [c/CONSULTATIONS] [g/ASSIGNMENT_NAME:SCORE] [w/WEEK_NUMBER:STATUS] [r/REMARK]`

* Edits the student at the specified `INDEX`
* The index refers to the index number shown in the displayed student list
* The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided
* Existing values will be overwritten by the input values
* When editing module codes, the existing module codes will be removed and replaced (not cumulative)
* When editing tags, the existing tags will be removed and replaced (not cumulative)
* You can remove all module codes by typing `m/` without specifying any module codes
* Editing consultations should still follow the same format as in the `add` command
* **Editing grades:** Use `g/ASSIGNMENT_NAME:SCORE` to update an existing grade. The assignment must already exist for the student, otherwise an error will be shown.
* **Editing attendance:** Use `w/WEEK_NUMBER:STATUS` to update attendance for a specific week (1-13). Status can be `present`, `absent`, or `unmark` (to remove the attendance record).
* **Editing remarks:** Use `r/REMARK` to update the remark for a student.

<box type="warning" seamless>

**Note about legacy fields:**
The edit command still supports `p/PHONE` and `a/ADDRESS` prefixes for backward compatibility with older data. However, new students added via the `add` command will not have phone or address fields.
</box>

Examples:
*  `edit 1 s/A9999999Z e/newemail@u.nus.edu` - Edits the student ID and email of the 1st student
*  `edit 2 n/Jane Doe t/friends` - Edits the name of the 2nd student and replaces all tags with "friends"
*  `edit 3 m/CS2103T m/CS2101` - Replaces all module codes with CS2103T and CS2101
*  `edit 1 g/Midterm:90` - Updates the Midterm grade to 90 for the 1st student (assignment must already exist)
*  `edit 2 w/5:present` - Marks the 2nd student as present for week 5
*  `edit 3 w/3:absent` - Marks the 3rd student as absent for week 3
*  `edit 4 w/7:unmark` - Removes the attendance record for week 7 for the 4th student
*  `edit 1 r/Needs extra help with recursion` - Updates the remark for the 1st student

### Adding tags to a student : `tag`

Adds one or more tags to an existing student in TeachMate without removing existing tags.

Format:
* `tag INDEX t/TAG [t/TAG]…​` - Add tags by list index
* `tag s/STUDENT_ID t/TAG [t/TAG]…​` - Add tags by student ID

* The index refers to the index number shown in the displayed student list
* The index **must be a positive integer** 1, 2, 3, …​
* Student ID must match the format A followed by 7 digits and 1 uppercase letter
* At least one tag must be provided
* Tags are added to existing tags (not replaced)
* Duplicate tags that already exist on the student will still be added
* `TAG` should be alphanumeric (no spaces)

Examples:
* `tag 1 t/Struggling t/Inactive` - Adds "Struggling" and "Inactive" tags to the 1st student
* `tag s/A0291772W t/Excelling` - Adds "Excelling" tag to student with ID A0291772W
* `tag 2 t/needsHelp` - Adds "needsHelp" tag to the 2nd student

### Removing tags from a student : `untag`

Removes one or more tags from an existing student in TeachMate.

Format:
* `untag INDEX t/TAG [t/TAG]…​` - Remove tags by list index
* `untag s/STUDENT_ID t/TAG [t/TAG]…​` - Remove tags by student ID

* The index refers to the index number shown in the displayed student list
* The index **must be a positive integer** 1, 2, 3, …​
* Student ID must match the format A followed by 7 digits and 1 uppercase letter
* At least one tag must be provided
* All specified tags must exist on the student, otherwise an error will be shown
* `TAG` should be alphanumeric (no spaces)

<box type="warning" seamless>

**Note:** If you try to remove a tag that doesn't exist on the student, the command will fail and show you which tags are missing. Make sure the tags you want to remove are currently on the student.
</box>

Examples:
* `untag 1 t/Struggling` - Removes "Struggling" tag from the 1st student
* `untag s/A0291772W t/Struggling t/Inactive` - Removes "Struggling" and "Inactive" tags from student with ID A0291772W
* `untag 2 t/needsHelp` - Removes "needsHelp" tag from the 2nd student

### Adding grades to a student : `grade`

Adds one or more grades to an existing student in TeachMate.

Format: `grade INDEX g/ASSIGNMENT_NAME:SCORE [g/ASSIGNMENT_NAME:SCORE]…​`

* Adds grades to the student at the specified `INDEX`
* The index refers to the index number shown in the displayed student list
* The index **must be a positive integer** 1, 2, 3, …​
* At least one grade must be provided
* Grades are added to existing grades (not replaced)
* Each grade consists of an assignment name and a score separated by a colon `:`
* `ASSIGNMENT_NAME` should not be blank
* `SCORE` must be a number between 0 and 100 (inclusive)
* Only students (those with Student IDs) can have grades added
* Duplicate grades for the same assignment name are not allowed

<box type="warning" seamless>

**Note:** If you try to add a grade for an assignment that already exists for the student, the command will fail. Each assignment name must be unique per student.
</box>

<box type="tip" seamless>

**Tips:**
* You can add multiple grades in one command by using multiple `g/` prefixes
* Grades will appear as purple badges in the student card, below the email
* Assignment names can contain spaces (e.g., "Final Exam")
* Grades are automatically saved and will persist across application restarts
</box>

Examples:
* `grade 1 g/Midterm:85` - Adds a grade of 85 for "Midterm" to the 1st student
* `grade 2 g/Quiz1:90 g/Assignment1:88` - Adds two grades to the 2nd student
* `grade 3 g/Final Exam:92` - Adds a grade for "Final Exam" to the 3rd student

### Adding remarks to a student : `remark`

Adds or updates a personalized remark for an existing student in TeachMate.

Format: `remark s/STUDENT_ID r/REMARK`

* Adds or updates a remark for the student with the specified `STUDENT_ID`
* Student ID must match the format A followed by 7 digits and 1 uppercase letter
* `REMARK` can contain any text including spaces and special characters
* `REMARK` supports multi-line text for longer notes
* The remark must not be blank (must contain at least one non-whitespace character)
* Existing remarks will be overwritten by the new remark (not cumulative)
* The student must exist in TeachMate

**Success message:** `Added remark to Student: [student details]`

**Error messages:**
* If the student ID is not found: `No student found with ID: [ID]`
* If the remark is blank: `Remarks should not be blank`

<box type="tip" seamless>

**Tips:**
* Use remarks to record personalized notes about each student (e.g., learning preferences, areas of difficulty, progress notes)
* Remarks are displayed in the student card view with a 📝 icon
* To view all details including remarks, use the student card in the list view
* Remarks are automatically saved and will persist across application restarts
</box>

Examples:
* `remark s/A0123456X r/Needs extra help with OOP concepts` - Adds a remark for student A0123456X
* `remark s/A0234567Y r/Excellent participation in tutorials` - Adds a remark for student A0234567Y
* `remark s/A0345678Z r/Struggling with time management. Recommended office hours on Fridays.` - Adds a multi-line remark

### Locating students by name: `find`

Finds students whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive (e.g. `john` will match `John`)
* The order of the keywords does not matter (e.g. `John Doe` will match `Doe John`)
* Only the name is searched
* Only full words will be matched (e.g. `Joh` will not match `John`)
* Students matching at least one keyword will be returned (i.e. `OR` search)

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Filtering students by tags: `filter`

Filters students who have all of the specified tags.

Format: `filter t/TAG [t/MORE_TAGS]…​`

* At least one tag must be provided
* Only students who have **all** the specified tags will be shown (i.e. `AND` search)
* **Tag matching is case-insensitive** (e.g., `Friends` matches `friends`)
* **Substring matching is supported** (e.g., `help` matches tags containing "help" like `needHelp`)
* Tags must be alphanumeric (no spaces)

Examples:
* `filter t/friends` returns all students tagged with `friends`, `Friends`, or `FRIENDS`
* `filter t/struggling t/needsHelp` returns students who have both `struggling` and `needsHelp` tags
* `filter t/help` returns students with tags like `needHelp`, `helpfulStudent`, or `help`
* `filter t/FRIEND` returns students with tags like `friends`, `friendlyStudent`, or `bestFriend`

### Deleting a student : `delete`

Deletes the specified student from TeachMate.

Format: `delete INDEX`

* Deletes the student at the specified `INDEX`
* The index refers to the index number shown in the displayed student list
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd student in TeachMate
* `find Jane` followed by `delete 1` deletes the 1st student in the results of the `find` command

### Clearing all entries : `clear`

Clears all entries from TeachMate.

Format: `clear`

<box type="warning" seamless>

**Warning:** This action cannot be undone. All student data will be permanently deleted.
</box>

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

TeachMate data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

TeachMate data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, TeachMate will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause TeachMate to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TeachMate home folder.

**Q**: What is the student ID format?<br>
**A**: Student IDs must follow the NUS format: A followed by exactly 7 digits and 1 uppercase letter (e.g., A0123456X, A1234567B).

**Q**: Can I add a student without module codes?<br>
**A**: No, every student must have at least one module code when using the `add` command.

**Q**: Why does the edit command still accept phone (p/) and address (a/) prefixes?<br>
**A**: For backward compatibility with older data that may contain phone and address information. New students added via the `add` command will only have student-specific fields (student ID, module codes).

**Q**: Can tags contain spaces?<br>
**A**: No, tags must be alphanumeric without spaces. Use camelCase or single words (e.g., `needsHelp`, `struggling`).

**Q**: Can I edit or delete grades after adding them?<br>
**A**: Currently, grades cannot be edited or deleted through commands. If you need to modify a grade, you can manually edit the `data/addressbook.json` file (make sure to back it up first), or delete the student and re-add them with the correct grades.

**Q**: Why can't I add grades to a person without a student ID?<br>
**A**: Grades are only applicable to students. The system requires students to have a student ID to ensure grades are tracked for the right individuals.

**Q**: What happens to grades when I edit or delete a student?<br>
**A**: When you edit a student's information (name, email, etc.), their grades are preserved. If you delete a student, all their associated grades are permanently deleted as well.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

| Action     | Format, Examples                                                                                                                                                          |
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Add**    | `add n/NAME s/STUDENT_ID e/EMAIL m/MODULE_CODE [m/MODULE_CODE]…​ [t/TAG]…​` <br> e.g., `add n/John Doe s/A0123456X e/johnd@u.nus.edu m/CS2103T m/CS2101 t/struggling`  |
| **Clear**  | `clear`                                                                                                                                                                   |
| **Delete** | `delete INDEX`<br> e.g., `delete 3`                                                                                                                                       |
| **Edit**   | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/STUDENT_ID] [m/MODULE_CODE]…​ [t/TAG]…​`<br> e.g., `edit 2 n/Jane Lee s/A9999999Z`                              |
| **Filter** | `filter t/TAG [t/MORE_TAGS]…​`<br> e.g., `filter t/struggling t/needsHelp`                                                                                               |
| **Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find John Jane`                                                                                                                |
| **Grade**  | `grade INDEX g/ASSIGNMENT_NAME:SCORE [g/ASSIGNMENT_NAME:SCORE]…​`<br> e.g., `grade 1 g/Midterm:85 g/Quiz1:90`                                                            |
| **List**   | `list` or `list m/MODULE_CODE`<br> e.g., `list m/CS2103T`                                                                                                                |
| **Remark** | `remark s/STUDENT_ID r/REMARK`<br> e.g., `remark s/A0123456X r/Needs extra help with OOP concepts`                                                                       |
| **Tag**    | `tag INDEX t/TAG [t/TAG]…​` or `tag s/STUDENT_ID t/TAG [t/TAG]…​`<br> e.g., `tag 1 t/Struggling t/Inactive` or `tag s/A0291772W t/Excelling`                            |
| **Untag**  | `untag INDEX t/TAG [t/TAG]…​` or `untag s/STUDENT_ID t/TAG [t/TAG]…​`<br> e.g., `untag 1 t/Struggling` or `untag s/A0291772W t/Inactive`                                |
| **View**   | `view INDEX` or `view s/STUDENT_ID`<br> e.g., `view 1` or `view s/A0123456X`                                                                                             |
| **Attendance** | `attendance INDEX w/WEEK present|absent|unmark` or `attendance s/STUDENT_ID w/WEEK present|absent|unmark` or `attendance all w/WEEK present|absent|unmark`<br> e.g., `attendance 1 w/1 present` or `attendance s/A0123456X w/1 unmark` or `attendance all w/1 absent` |
| **Help**   | `help`                                                                                                                                                                    |
| **Exit**   | `exit`                                                                                                                                                                    |
