---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TeachMate Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of TeachMate.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [Main.java]<!-- -->(../src/main/java/seedu/address/Main.java) and [MainApp.java]<!-- -->(../src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the [Logic.java]<!-- -->(../src/main/java/seedu/address/logic/Logic.java) interface and implements its functionality using the [LogicManager.java]<!-- -->(../src/main/java/seedu/address/logic/LogicManager.java) class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [Ui.java]<!-- -->(../src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [MainWindow]<!-- -->(../src/main/java/seedu/address/ui/MainWindow.java) is specified in [MainWindow.fxml]<!-- -->(../src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` objects (representing students) residing in the `Model`.

**TeachMate-specific UI features:**
* **PersonCard Display**: The [PersonCard.java]<!-- -->(../src/main/java/seedu/address/ui/PersonCard.java) component displays comprehensive student information including name, student ID, module codes, tags, grades, consultations, remarks, and a visual attendance grid.
* **Visual Attendance Grid**: PersonCard displays attendance using a grid of 13 colored rectangles representing weekly attendance for Week 1-13, with color coding:
  * Grey for no attendance record
  * Green for present
  * Red for absent
* **Theme Toggle**: Supports switching between light and dark themes for user preference
* **Interactive Help Window**: Shows a summary of available commands with keyboard shortcut F1

### Logic component

**API** : [Logic.java]<!-- -->(../src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a student).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

**TeachMate Commands:**

TeachMate implements 16 commands to support TA workflows:

| Command | Purpose | Example |
|---------|---------|---------|
| `add` | Add a student with details | `add n/John Doe s/A0123456X e/john@u.nus.edu m/CS2103T` |
| `delete` | Delete a student | `delete 1` |
| `edit` | Edit student details | `edit 1 e/newemail@u.nus.edu` |
| `list` | List all students or filter by module | `list` or `list m/CS2103T` |
| `find` | Find students by name keywords | `find john doe` |
| `filter` | Filter students by tags (AND condition) | `filter struggling` |
| `view` | View detailed student info | `view 1` or `view s/A0123456X` |
| `attendance` | Mark attendance for a student or all | `attendance 1 w/1 present` or `attendance all w/1 present` |
| `grade` | Add or update a grade | `grade 1 g/Midterm:85` |
| `deletegrade` | Delete a specific grade | `deletegrade 1 g/Midterm` |
| `remark` | Add a remark to a student | `remark s/A0123456X r/Needs help with OOP` |
| `tag` | Add a tag to a student | `tag 1 t/struggling` |
| `untag` | Remove a tag from a student | `untag 1 t/struggling` |
| `clear` | Clear all entries | `clear` |
| `help` | Show help window | `help` |
| `exit` | Exit the application | `exit` |

### Model component
**API** : [Model.java](../src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores TeachMate data i.e., all `Person` objects (representing students) which are contained in a `UniquePersonList` object.
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user's preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

**TeachMate Domain Model:**

The `Person` class in TeachMate has been enhanced to represent students with TA-specific attributes:

**Core Identity Fields:**
* `Name`: Student's full name
* `StudentId`: NUS student ID (format: A + 7 digits + 1 letter, e.g., A0123456X)
* `Email`: Student's email address
* `Phone`: Optional (can be null for students)
* `Address`: Optional (can be null for students)

**Academic Fields:**
* `Set<ModuleCode>`: Module codes the student is enrolled in (format: 2-4 letters + exactly 4 digits + optional 0-2 letters, e.g., CS2103T, ACC1701XA, GESS1000, BMA5001)
* `Set<Grade>`: Assignment grades with assignment names and scores (0-100)
* `AttendanceRecord`: Immutable record of weekly attendance (Week 1-13) mapped to `AttendanceStatus` (PRESENT/ABSENT)
* `List<Consultation>`: Scheduled consultations with date and time

**Other Fields:**
* `Set<Tag>`: Custom tags for categorization (e.g., "struggling", "excellent")
* `Remark`: Optional free-form text notes (supports multi-line)

The `Person` class supports multiple constructors to accommodate different use cases:
* Students with student ID, modules, grades, and attendance
* Regular persons with phone and address
* Flexible combinations with optional fields

**Supporting Domain Classes:**

Located in `seedu.address.model.*` subpackages:
* `attendance.AttendanceRecord`: Immutable map of Week → AttendanceStatus
* `attendance.Week`: Represents week numbers 1-13
* `attendance.AttendanceStatus`: Enum (PRESENT, ABSENT, UNMARK)
* `module.ModuleCode`: NUS module code validation and storage
* `grade.Grade`: Assignment name and score pair
* `consultation.Consultation`: Date-time based consultation record
* `person.StudentId`: NUS student ID validation and storage
* `person.Remark`: Multi-line text notes
* `tag.Tag`: Simple categorization labels

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [Storage.java](../src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

**TeachMate-specific Storage:**

The storage component includes specialized JSON adapters for TeachMate's domain objects:
* `JsonAdaptedPerson`: Serializes/deserializes `Person` objects with all student-specific fields
* `JsonAdaptedGrade`: Handles grade data (assignment name and score)
* `JsonAdaptedAttendance`: Serializes attendance records (week number and status)
* `JsonAdaptedConsultation`: Handles consultation date-time data
* `JsonAdaptedTag`: Serializes tag data

Data files:
* `data/addressbook.json`: Main data file containing all student records
* `config.json`: Application configuration
* `preferences.json`: User preferences (window size, theme, etc.)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

The `Commons` package contains:
* `core`: Core utilities including `Config`, `GuiSettings`, `LogsCenter`, `Version`, and `Index`
* `exceptions`: Common exceptions like `DataLoadingException` and `IllegalValueException`
* `util`: Utility classes including `JsonUtil`, `StringUtil`, `FileUtil`, and `CollectionUtil`

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### List Feature

#### Implementation

The list feature allows teaching assistants to view all students in the system or filter them by module code. It is implemented through the `ListCommand` command class and `ListCommandParser` parser class.

**Key Components:**

The `ListCommand` class:
- Supports two modes: listing all students or filtering by a specific module code
- Uses an `Optional<ModuleCode>` to store the optional module filter
- Updates the filtered person list in the model based on the mode
- Provides success messages showing the total count of displayed students

The `ListCommandParser`:
- Parses user input to detect the presence of a module code prefix (`m/`)
- If no prefix is present, validates that the input is empty (simple `list` command)
- If a module prefix is present, extracts and validates the module code format
- Creates a `ListCommand` with either an empty `Optional` (list all) or a specific `ModuleCode` (filter by module)

**Execution Flow:**

Given below is an example usage scenario and how the list mechanism behaves.

**Step 1.** The user executes `list` or `list m/CS2103T` to view students.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a list command and creates a `ListCommandParser`.

**Step 3.** `ListCommandParser` parses the arguments:
- If `m/` prefix is absent: Validates empty input and creates a `ListCommand()` with no module filter
- If `m/` prefix is present: Extracts module code, validates format, and creates `ListCommand(moduleCode)`

**Step 4.** When `ListCommand#execute()` is called:
- If no module filter: Calls `Model#updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)` to show all students
- If module filter exists: Creates a predicate that checks if any of the student's enrolled modules match the filter, then applies it to the filtered list
- Counts the number of students in the filtered list
- Returns a success message with the count

**Step 5.** The UI updates to display the filtered list of students in the person list panel.

#### Design Considerations

**Aspect: Module filtering support**

* **Alternative 1 (current choice):** Support optional module filtering with the `m/` prefix
  * Pros: Flexible - can view all students or just those in a specific module. Simple command structure - `list` or `list m/CS2103T`. Consistent with other commands using prefixes.
  * Cons: Only supports single module filtering. Cannot filter by multiple modules or other criteria simultaneously.

* **Alternative 2:** Only support listing all students without filters
  * Pros: Simpler implementation. No parsing complexity for filters. Single, clear purpose.
  * Cons: Less useful for TAs managing multiple modules. Requires additional filtering steps to find module-specific students. Cannot leverage module-based workflows.

**Aspect: Empty input validation**

* **Alternative 1 (current choice):** Strictly validate that `list` command has no arguments when no prefix is used
  * Pros: Prevents accidental typos or invalid arguments. Clear error messages guide users to correct usage. Prevents confusion about command behavior.
  * Cons: Slightly less forgiving - users must use exact format.

* **Alternative 2:** Accept any arguments and ignore them if no prefix is present
  * Pros: More forgiving for users. Easier to use without strict format.
  * Cons: Hides potential user errors. May lead to confusion if extra arguments are silently ignored.

### Add Student Feature

#### Implementation

The add student feature allows teaching assistants to add new student records to TeachMate. It is implemented through the `AddCommand` command class and `AddCommandParser` parser class.

**Key Components:**

The `AddCommand` class:
- Takes a `Person` object representing the student to be added
- Checks for duplicate student IDs before adding
- Validates that a student with the same student ID doesn't already exist
- Calls `Model#addPerson()` to add the student to the address book

The `AddCommandParser`:
- Parses user input to extract student information from various prefixes
- Validates required fields: name (`n/`), student ID (`s/`), email (`e/`), and at least one module code (`m/`)
- Parses optional fields: tags (`t/`), consultations (`c/`)
- Handles duplicate consultations by deduplicating them using `.distinct()`
- Creates a new `Person` object with parsed data and returns an `AddCommand`

The `Person` class:
- Provides multiple constructors to handle different field combinations
- For students: requires name, student ID, email, module codes
- Includes optional fields: tags, attendance records, grades, consultations, and remarks
- Initializes attendance record and grades as empty if not provided

**Execution Flow:**

Given below is an example usage scenario and how the add mechanism behaves.

**Step 1.** The user executes `add n/John Doe s/A0123456X e/john@u.nus.edu m/CS2103T m/CS2101 t/struggling` to add a new student.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as an add command and creates an `AddCommandParser`.

**Step 3.** `AddCommandParser` parses the arguments:
- Validates that required prefixes (`n/`, `s/`, `e/`, `m/`) are all present and the preamble is empty
- Uses `ArgumentTokenizer.tokenize()` to extract values for each prefix
- Verifies no duplicate single-value prefixes using `verifyNoDuplicatePrefixesFor()`
- Parses each field using appropriate `ParserUtil` methods
- For consultations: parses each datetime string and removes duplicates using `.distinct()`
- Creates a `Person` object with all parsed fields

**Step 4.** When `AddCommand#execute()` is called:
- Checks if the student has a student ID (required for all students)
- Calls `Model#getPersonByStudentId()` to check for duplicates
- Throws `CommandException` with message "Cannot add student: A student with ID [ID] already exists" if duplicate found
- Calls `Model#addPerson()` to add the student to the address book
- Returns a success message with the student's details

**Step 5.** The model persists the changes:
- Added student triggers storage save
- Student data is serialized to JSON format
- Data is written to disk automatically

The following sequence diagram shows how an add operation works:

<puml src="diagrams/AddSequenceDiagram.puml" alt="AddSequenceDiagram" />

#### Design Considerations

**Aspect: Required vs optional fields**

* **Alternative 1 (current choice):** Require name, student ID, email, and at least one module code
  * Pros: Ensures all students have essential information. Student ID provides unique identification. Module codes are essential for academic tracking. Prevents creation of incomplete records.
  * Cons: Less flexible for initial quick entry. Requires multiple fields for simple additions.

* **Alternative 2:** Make more fields optional or have minimal required fields
  * Pros: More flexible for quick entry. Can add students with partial information and fill in later.
  * Cons: Risk of incomplete data. Harder to track students without essential identifiers. Potential for orphaned records.

### Edit Student Feature

#### Implementation

The edit student feature allows teaching assistants to modify existing student records in TeachMate. It is implemented through the `EditCommand` command class, `EditCommandParser` parser class, and the `EditPersonDescriptor` inner class.

**Key Components:**

The `EditCommand` class:
- Takes an `Index` to identify the student and an `EditPersonDescriptor` with updated fields
- Supports updating all student attributes: name, phone, email, address, student ID, module codes, tags, consultations, grades, attendance, and remarks
- Validates that at least one field is being edited
- Checks for duplicate student IDs when student ID is changed
- Builds a detailed message showing which fields were updated

The `EditPersonDescriptor` class:
- Stores optional field values to be updated (all fields use `Optional` type)
- Provides methods to set and get each field
- Tracks whether any field has been edited via `isAnyFieldEdited()`
- Uses defensive copying for collections (tags, module codes, consultations)

The `EditCommandParser`:
- Parses user input to extract the index and field updates
- Validates the index is a positive integer
- Parses each provided prefix's value using appropriate `ParserUtil` methods
- Handles optional fields gracefully (only sets values if prefix is present)
- Creates an `EditPersonDescriptor` and populates it with parsed values

**Execution Flow:**

Given below is an example usage scenario and how the edit mechanism behaves.

**Step 1.** The user executes `edit 1 e/newemail@u.nus.edu` to edit the email of the first student in the list.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as an edit command and creates an `EditCommandParser`.

**Step 3.** `EditCommandParser` parses the arguments:
- Extracts index from preamble using `ParserUtil#parseIndex()`
- Validates required prefixes are not duplicated
- For each provided prefix, parses the value and sets it in `EditPersonDescriptor`
- Creates an `EditCommand` with the index and descriptor

**Step 4.** When `EditCommand#execute()` is called:
- Gets the student at the specified index from the filtered person list
- Validates that at least one field is being edited
- If student ID is being changed, checks for duplicates
- Creates an edited `Person` object using `createEditedPerson()` helper method
- This method uses `Optional#orElse()` to preserve existing values for fields not being edited
- For grades: finds and removes existing grade with matching assignment name (case-sensitive), then adds updated grade
- For attendance: updates the attendance record using `AttendanceRecord#markAttendance()` or `unmarkAttendance()`
- Calls `Model#setPerson()` to replace the old person with the edited person
- Builds a detailed message showing which fields were changed
- Returns success message with edited student details

**Step 5.** The model persists the changes:
- Updated person triggers storage save
- Changes are serialized to JSON format
- Data is written to disk automatically

The following sequence diagram shows how an edit operation works:

<puml src="diagrams/EditSequenceDiagram.puml" alt="EditSequenceDiagram" />

#### Design Considerations

**Aspect: Field update behavior**

* **Alternative 1 (current choice):** Only update fields that are explicitly provided in the command
  * Pros: Preserves existing data for unchanged fields. Selective updates without full replacement. Flexible for partial modifications.
  * Cons: Users must remember current values if they want to keep them. More complex logic to merge old and new values.

* **Alternative 2:** Replace entire person object with new values, requiring all fields
  * Pros: Simpler logic - complete replacement. Clear, explicit updates.
  * Cons: Tedious for users to provide all fields. Risk of data loss if some fields are omitted. Unfriendly user experience.

**Aspect: EditPersonDescriptor design**

* **Alternative 1 (current choice):** Use `Optional` fields in a descriptor class
  * Pros: Type-safe indication of which fields are being updated. Clear API. Easy to check if any field is edited. Follows builder pattern.
  * Cons: Boilerplate code for each field. More verbose than direct field manipulation.

* **Alternative 2:** Pass all fields directly to `EditCommand`, using null for unchanged fields
  * Pros: Simpler class structure. Fewer objects involved.
  * Cons: Less type-safe. Ambiguous whether null means "not provided" or "set to null". Harder to validate completeness.

### Delete Student Feature

#### Implementation

The delete student feature allows teaching assistants to remove student records from TeachMate. It is implemented through the `DeleteCommand` command class and `DeleteCommandParser` parser class.

**Key Components:**

The `DeleteCommand` class:
- Supports two methods of identifying students: by index in the displayed list or by student ID
- Provides two constructors: one for index-based deletion and one for student ID-based deletion
- Finds the target student using the appropriate lookup method
- Calls `Model#deletePerson()` to remove the student from the address book

The `DeleteCommandParser`:
- Parses user input to determine whether the user is deleting by index or student ID
- Uses the presence of `s/` prefix to differentiate between index-based and ID-based deletion
- Validates that both index and student ID are not provided simultaneously (throws error if both detected)
- If `s/` prefix is present: extracts and validates the student ID, creates a `DeleteCommand(StudentId)`
- If no prefix: parses the argument as an index, creates a `DeleteCommand(Index)`
- Validates the format of the parsed index or student ID

**Execution Flow:**

Given below is an example usage scenario and how the delete mechanism behaves.

**Step 1.** The user executes `delete 1` or `delete s/A0123456X` to delete a student.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a delete command and creates a `DeleteCommandParser`.

**Step 3.** `DeleteCommandParser` parses the arguments:
- Checks if `s/` prefix is present in the input
- Validates that both preamble (index) and student ID prefix are not present simultaneously
- If both are present: Throws `ParseException` with message "Conflicting parameters detected. Please use either index or student ID — not both."
- If prefix present: Extracts student ID using `ParserUtil#parseStudentId()`, creates `DeleteCommand(studentId)`
- If no prefix: Parses argument as index using `ParserUtil#parseIndex()`, creates `DeleteCommand(index)`

**Step 4.** When `DeleteCommand#execute()` is called:
- If using index:
  - Gets the filtered person list from `Model`
  - Gets the student at the specified index
  - Throws `CommandException` if index is out of bounds
- If using student ID:
  - Calls `Model#getPersonByStudentId()` to locate the student
  - Throws `CommandException` with "No student found with student ID [ID]" if not found
- Calls `Model#deletePerson()` to remove the student from the address book
- Returns a success message with the deleted student's details

**Step 5.** The model persists the changes:
- Deletion triggers storage save
- Updated student list is serialized to JSON format
- Data is written to disk automatically

#### Design Considerations

**Aspect: Dual lookup methods (index vs student ID)**

* **Alternative 1 (current choice):** Support both index and student ID lookup
  * Pros: Flexible for different use cases. Index is quick for visible students in the current list. Student ID works regardless of filter state and is unambiguous. Accommodates different user workflows.
  * Cons: More complex parsing logic. Need to handle two different code paths in execution. Potential confusion about which method to use.

* **Alternative 2:** Support only index-based deletion
  * Pros: Simpler implementation. Consistent with other basic operations. Easier to understand.
  * Cons: Less convenient when user knows student ID but student is not visible in current list. Requires additional list/find commands first.

**Aspect: Student ID as unique identifier**

* **Alternative 1 (current choice):** Use student ID as the primary unique identifier
  * Pros: Naturally unique for each student. Unambiguous identification. Works across different filter contexts. Permanent identifier that doesn't change.
  * Cons: Requires users to know the student ID. Slightly more verbose command format with `s/` prefix.

* **Alternative 2:** Use name or email as identifier
  * Pros: More human-readable. Easier to remember than student ID.
  * Cons: Not guaranteed to be unique. Names can have duplicates. Emails can change. Ambiguous identification.

### View Student Feature

#### Implementation

The view student feature allows teaching assistants to view detailed information about a specific student, including their personal details and complete attendance record. It is implemented through the `ViewCommand` command class and `ViewCommandParser` parser class.

**Key Components:**

The `ViewCommand` class:
- Supports two methods of identifying students: by index in the displayed list or by student ID
- Filters the displayed list to show only the selected student
- Generates a comprehensive detailed view message containing student information and attendance records
- Displays attendance week-by-week in the result display with text symbols (✓ for present, ✗ for absent) and status text

The `ViewCommandParser`:
- Parses user input to determine whether the user is searching by index or student ID
- Uses the presence of `s/` prefix to differentiate between index-based and ID-based lookup
- Validates that both index and student ID are not provided simultaneously (throws error if both detected)
- Creates appropriate `ViewCommand` objects with either an `Index` or `StudentId` parameter

**Execution Flow:**

Given below is an example usage scenario and how the view mechanism behaves.

**Step 1.** The user executes `view 1` or `view s/A0123456X` to view a student's details.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a view command and creates a `ViewCommandParser`.

**Step 3.** `ViewCommandParser` parses the arguments:
- Validates that both preamble (index) and student ID prefix are not present simultaneously
- If both are present: Throws `ParseException` with message "Conflicting parameters detected. Please use either index or student ID — not both."
- If `s/` prefix is present: Extracts and validates the student ID, creates a `ViewCommand(StudentId)`
- If no prefix: Parses the argument as an index, creates a `ViewCommand(Index)`

**Step 4.** When `ViewCommand#execute()` is called:
- If using index: Gets the person at the specified index from the filtered person list
- If using student ID: Calls `Model#findPersonByStudentId()` to locate the student
- Throws `CommandException` if student not found or index is invalid
- Calls `Model#updateFilteredPersonList()` to filter and show only the selected student
- Generates detailed view message via `createDetailedViewMessage(Person)`
- Returns a `CommandResult` with the formatted message

**Step 5.** The `createDetailedViewMessage()` method:
- Formats basic student information (name, student ID, email)
- Lists all enrolled module codes
- Lists all tags if present
- Displays attendance record sorted by week number
- Shows each week's status with symbols (✓/✗) and text (Present/Absent) in the result display

The following sequence diagram shows how a view operation works:

<puml src="diagrams/ViewSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `view 1` Command" />

#### Design Considerations

**Aspect: Dual lookup methods (index vs student ID)**

* **Alternative 1 (current choice):** Support both index and student ID lookup
  * Pros: Flexible for different use cases. Index is quick for visible students; student ID works regardless of filter state. Accommodates different user workflows.
  * Cons: More complex parsing logic. Need to handle two different code paths in execution.

* **Alternative 2:** Support only index-based lookup
  * Pros: Simpler implementation. Consistent with other commands like delete, edit.
  * Cons: Less convenient when user knows student ID but student is not visible in current list. Requires additional find/list commands first.

**Aspect: Display format for attendance**

* **Alternative 1 (current choice):** Two different display formats optimized for their context
  * ResultDisplay (from `view` command): Each week displayed individually with symbols (✓/✗) and text (Present/Absent) for detailed review
  * PersonCard (UI list): Visual grid of 13 colored rectangles with rounded edges - grey for no record, green (#4CAF50) for present, red (#F44336) for absent
  * Pros: ResultDisplay format is clear for detailed inspection during `view` command. PersonCard grid provides instant visual pattern recognition across entire semester at a glance. Optimized for different use cases - detailed examination vs quick scanning.
  * Cons: Two different display implementations to maintain. Users need to understand both representations. More complex UI code for PersonCard grid.

* **Alternative 2:** Single consistent text-based format in both displays
  * Pros: Consistent user experience across all views. Simpler implementation with single formatting method. Easier to maintain.
  * Cons: Text format in PersonCard takes more horizontal space. Harder to see attendance patterns quickly. Less intuitive for visual scanning across multiple students in the list.

**Aspect: List filtering after view**

* **Alternative 1 (current choice):** Filter list to show only viewed student
  * Pros: Focuses user attention on the selected student. Reduces visual clutter. Clear indication of which student is being viewed.
  * Cons: Requires user to run `list` command to see all students again. Changes the displayed list state.

* **Alternative 2:** Keep list unchanged, show details in popup or side panel
  * Pros: Preserves list context. User can view multiple students without losing their place.
  * Cons: Requires more complex UI components. Less suitable for CLI-focused application.

### Attendance Tracking Feature

#### Implementation

The attendance tracking feature allows teaching assistants to mark and track student attendance for weekly tutorial sessions. It is implemented through the `AttendanceRecord` model class, `AttendanceCommand` command class, and supporting classes `Week`, `AttendanceStatus`.

**Key Components:**

The `AttendanceRecord` class:
- Immutable data structure storing week-to-status mappings
- Uses `Map<Week, AttendanceStatus>` internally for efficient lookup
- Supports marking, querying, and retrieving all attendance records
- Returns new `AttendanceRecord` instances when marking attendance (defensive copying)

The `Week` class:
- Represents tutorial week numbers (1-13, corresponding to a typical semester)
- Validates week numbers are within valid range
- Implements proper equality for use as map keys

The `AttendanceStatus` enum:
- Three states: `PRESENT`, `ABSENT`, and `UNMARK`
- `UNMARK` is a special status that removes an existing attendance record
- Case-insensitive parsing from user input

The `AttendanceCommand`:
- Supports marking attendance for individual students by index or student ID
- Supports bulk marking attendance for all students using `all` keyword
- Takes week number and attendance status as parameters
- Status must be specified after the week parameter
- Supports unmarking attendance (removing records) with `unmark` status
- Creates updated `Person` objects with modified attendance records

The `PersonCard` UI component:
- Displays attendance visually in the student list using a grid of 13 rectangles (16x16 pixels each with rounded edges)
- Each rectangle represents one week (1-13) of the semester
- Color coding: Grey (#CCCCCC) for no record, Green (#4CAF50) for present, Red (#F44336) for absent
- Week numbers displayed above rectangles for easy reference
- Provides instant visual pattern recognition of attendance across the entire semester

**Execution Flow:**

Given below is an example usage scenario and how the attendance marking mechanism behaves.

**Step 1.** The user executes `attendance 1 w/1 present` to mark the first student in the displayed list as present for week 1.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as an attendance command and creates an `AttendanceCommandParser`.

**Step 3.** `AttendanceCommandParser` parses the arguments:
- Checks the preamble to determine the format (index, `all` keyword, or student ID with prefix)
- Validates that both preamble (index or "all") and student ID prefix are not present simultaneously (excluding "all" keyword)
- If both are present: Throws `ParseException` with message "Conflicting parameters detected. Please use either index or student ID — not both."
- For index format: Extracts index (`1`) from preamble
- For `all` format: Detects `all` keyword in preamble
- For student ID format: Extracts student ID (`A0123456X`) from `s/` prefix
- Extracts week number (`1`) from `w/` prefix and validates it's between 1-13
- Parses attendance status (`present`, `absent`, or `unmark`) from after the week parameter into corresponding `AttendanceStatus`
- Creates an `AttendanceCommand` with these parameters

**Step 4.** When `AttendanceCommand#execute()` is called:
- Determines whether to mark by index, by student ID, or all students
- For marking by index:
  - Retrieves the filtered person list from `Model`
  - Gets the student at the specified index
  - Throws `CommandException` if index is out of bounds
- For marking by student ID:
  - Calls `Model#findPersonByStudentId()` to locate the student
  - Throws `CommandException` if student not found
- For marking all students:
  - Iterates through all students in address book
  - Counts number of students updated
- For each student being marked:
  - Gets current `AttendanceRecord` from the student
  - If status is `UNMARK`: Calls `AttendanceRecord#unmarkAttendance(week)` to remove the record
  - Otherwise: Calls `AttendanceRecord#markAttendance(week, status)` to add/update the record
  - Creates new `Person` object with updated attendance record
  - Calls `Model#setPerson()` to replace old person with updated person
- Returns `CommandResult` with appropriate success message

**Step 5.** The model persists the changes:
- Updated person list triggers storage save
- Attendance data is serialized to JSON format
- Data is written to disk automatically

The following sequence diagram shows how an attendance marking operation works for the index-based format (`attendance 1 w/1 present`). The flow for student ID-based (`attendance s/A0123456X w/1 present`) and mark all (`attendance all w/1 present`) formats follows a similar execution path, with the main difference being how the student(s) are identified.

<puml src="diagrams/AttendanceSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `attendance 1 w/1 present` Command" />

#### Design Considerations

**Aspect: Attendance record storage**

* **Alternative 1 (current choice):** Store attendance as part of `Person` object
  * Pros: Direct association between person and their attendance. Easy to access when viewing student details. Follows object-oriented principles. Simplifies serialization and storage.
  * Cons: Attendance data is duplicated if needed elsewhere. Cannot easily query attendance independently.

* **Alternative 2:** Store attendance in separate `AttendanceBook` class
  * Pros: Separation of concerns. Can query attendance data independently. Supports attendance-focused operations (e.g., "show all students absent in week 3").
  * Cons: More complex architecture. Requires maintaining relationships between Person and AttendanceRecord. More complex serialization logic.

**Aspect: Immutability of AttendanceRecord**

* **Alternative 1 (current choice):** Immutable AttendanceRecord
  * Pros: Thread-safe. Prevents accidental modifications. Fits functional programming paradigm. Easier to reason about state changes. Consistent with other model classes.
  * Cons: Creates new objects on every update. Slightly higher memory usage. Requires creating new Person object for each attendance change.

* **Alternative 2:** Mutable AttendanceRecord
  * Pros: More efficient for frequent updates. No need to recreate objects. Direct modification of state.
  * Cons: Risk of unintended side effects. Thread safety concerns. Harder to track state changes. Inconsistent with existing codebase patterns.

**Aspect: Student identification methods**

* **Alternative 1 (current choice):** Support index, student ID, and `all` keyword
  * Pros: Flexible for different workflows. Index is fastest for visible students. Student ID works when student not in filtered list. `all` keyword saves time for bulk operations. Consistent with other commands like `delete` and `view`.
  * Cons: More complex parsing logic. Need to distinguish between numeric index and `all` keyword. Multiple constructors in `AttendanceCommand`.

* **Alternative 2:** Only support student ID-based marking
  * Pros: Simpler parsing logic. Single identification method. No ambiguity in command format.
  * Cons: Less convenient. Requires memorizing or looking up student IDs. Cannot leverage displayed list. Slower for bulk operations.

### Grade Feature

#### Implementation

The grade feature allows teaching assistants to add, view, and manage grades for students' assignments. It is implemented through the `Grade` model class and the `GradeCommand` command class.

**Key Components:**

The `Grade` class:
- Stores an assignment name and a score (0-100)
- Is immutable to prevent unintended modifications
- Validates that scores are numeric and within valid range using regex pattern `^(100|[0-9]{1,2})$`
- Implements proper `equals()` and `hashCode()` for use in collections

The `GradeCommand`:
- Takes an index and one or more grade entries in the format `g/ASSIGNMENT_NAME:SCORE`
- Finds the person at the specified index in the filtered person list
- Creates a new `Person` object with the updated grades (defensive copying)
- Updates existing grades if assignment name matches (case-insensitive)
- Only allows adding grades to students (persons with StudentId)

**Execution Flow:**

Given below is an example usage scenario and how the grade mechanism behaves.

**Step 1.** The user executes `grade 1 g/Midterm:85` to add a grade for the 1st student.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a grade command and creates a `GradeCommandParser`.

**Step 3.** `GradeCommandParser` parses the arguments:
- Extracts the index (1)
- Extracts the grade string ("Midterm:85")
- Splits on the colon to get assignment name "Midterm" and score "85"
- Creates a `Grade` object after validation
- Creates a `GradeCommand` with the index and set of grades

**Step 4.** When `GradeCommand#execute()` is called:
- Gets the person at index 1 from the filtered person list using `Model#getFilteredPersonList()`
- Validates the person has a StudentId (only students can have grades)
- Checks for existing grades with same assignment name (case-insensitive)
- Creates a new `Person` with updated/added grades
- Updates the model with `Model#setPerson(personToEdit, editedPerson)`
- Returns a `CommandResult` with success message

The following sequence diagram shows how the grade operation works:

<puml src="diagrams/GradeSequenceDiagram.puml" alt="GradeSequenceDiagram" />

**Note:** The lifeline for `GradeCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

#### Design Considerations

**Aspect: How grades are stored**

* **Alternative 1 (current choice):** Store grades as a `Set<Grade>` in the `Person` class
  * Pros: Simple, direct association between Person and Grade. Using a Set prevents duplicate grades automatically. Easy to retrieve all grades for a person.
  * Cons: Grades cannot be accessed independently of persons. Cannot easily query all grades across all students.

* **Alternative 2:** Store grades in a separate `GradeBook` class
  * Pros: Better separation of concerns, allows grade-specific operations like calculating class average. Can query grades independently.
  * Cons: More complex architecture, requires maintaining bidirectional references between Person and Grade. Higher coupling between components.

**Aspect: Duplicate grade handling**

* **Alternative 1 (current choice):** Allow updates by replacing grades with matching assignment names
  * Pros: Easy to update grades without deleting first. Natural user experience. Case-insensitive matching prevents accidental duplicates.
  * Cons: No history of grade changes. Cannot track grade evolution over time.

* **Alternative 2:** Prevent duplicates entirely, require explicit deletion before update
  * Pros: More explicit operations. Forces user to acknowledge grade replacement.
  * Cons: More tedious workflow. Requires two commands to update a grade.

**Aspect: Grade validation**

* **Alternative 1 (current choice):** Validate in `Grade` constructor
  * Pros: Ensures all Grade objects are valid (fail-fast). Defensive programming - impossible to create invalid grade. Validation logic centralized in one place.
  * Cons: Cannot create Grade object with invalid data even for testing purposes.

* **Alternative 2:** Validate only when parsing user input
  * Pros: More flexible for internal use and testing. Can create temporary Grade objects during processing.
  * Cons: Risk of invalid grades propagating through the system. Validation logic scattered across multiple parsers.

### Delete Grade Feature

#### Implementation

The delete grade feature allows teaching assistants to remove specific grades from students. It is implemented through the `DeleteGradeCommand` command class and `DeleteGradeCommandParser` parser class.

**Key Components:**

The `DeleteGradeCommand`:
- Takes an index and one or more assignment names in the format `g/ASSIGNMENT_NAME`
- Finds the person at the specified index in the filtered person list
- Validates that all specified assignment names exist for the student
- Creates a new `Person` object with the specified grades removed (defensive copying)
- Only allows deleting grades from students (persons with StudentId)
- Assignment name matching is case-sensitive (exact match required)

The `DeleteGradeCommandParser`:
- Parses user input in the format `deletegrade INDEX g/ASSIGNMENT_NAME [g/ASSIGNMENT_NAME]...`
- Extracts the index and validates it is a positive integer
- Extracts all assignment names from `g/` prefixes
- Creates a `DeleteGradeCommand` with the validated index and set of assignment names

**Execution Flow:**

Given below is an example usage scenario and how the delete grade mechanism behaves.

**Step 1.** The user executes `deletegrade 1 g/Midterm` to delete the "Midterm" grade from the 1st student.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a deletegrade command and creates a `DeleteGradeCommandParser`.

**Step 3.** `DeleteGradeCommandParser` parses the arguments:
- Extracts the index (1)
- Extracts assignment name(s) from `g/` prefix ("Midterm")
- Validates at least one assignment name is provided
- Creates a `DeleteGradeCommand` with the index and set of assignment names

**Step 4.** When `DeleteGradeCommand#execute()` is called:
- Gets the person at index 1 from the filtered person list using `Model#getFilteredPersonList()`
- Validates the person has a StudentId (only students can have grades deleted)
- Checks that all specified assignment names exist in the student's grades
- Throws `CommandException` with message "Grade not found for assignment: [NAME]" if any assignment doesn't exist
- Creates a new `Person` with specified grades removed via `createPersonWithDeletedGrades()`
- Updates the model with `Model#setPerson(personToEdit, updatedPerson)`
- Returns a `CommandResult` with success message

**Step 5.** The `createPersonWithDeletedGrades()` helper method:
- Creates a new set of grades excluding the specified assignment names
- Constructs a new `Person` object preserving all other fields
- Returns the updated person object

#### Design Considerations

**Aspect: Assignment name matching**

* **Alternative 1 (current choice):** Case-sensitive exact matching
  * Pros: Unambiguous - user must specify exact assignment name. Prevents accidental deletion of wrong grades. Consistent with file system conventions.
  * Cons: Less user-friendly if user doesn't remember exact capitalization. Requires checking exact name first (via view command).

* **Alternative 2:** Case-insensitive matching
  * Pros: More flexible and user-friendly. Consistent with grade update behavior.
  * Cons: Could lead to accidental deletions if multiple assignments have similar names with different cases. Less explicit about which grade is being deleted.

**Aspect: Validation timing**

* **Alternative 1 (current choice):** Validate all assignment names exist before deleting any
  * Pros: Atomic operation - either all grades deleted or none. Prevents partial deletions on error. Clear error message identifies missing assignment.
  * Cons: Cannot delete subset of grades if one name is invalid.

* **Alternative 2:** Delete valid assignments, skip invalid ones
  * Pros: More forgiving - deletes what it can. User doesn't have to retry entire command.
  * Cons: Unclear behavior - some grades deleted, some not. Harder to track what was actually deleted. Less predictable.

### Consultation Feature

#### Implementation

The consultation feature allows teaching assistants to record and view student consultation sessions as part of each student's profile. Unlike other standalone commands, consultations are implemented as an **attribute of each student**, and can be added or modified **only through the `add` or `edit` commands**.

Consultations are stored within each `Person` object as a list of `Consultation` instances, each representing a single consultation date and time.

**Key Components:**

The `Consultation` class:
- Represents an individual consultation with a `LocalDateTime` attribute
- Provides accessor and formatting methods to return a readable date/time string
- Supports multiple date/time input formats for user convenience

The `AddCommand` and `EditCommand` classes:
- Allow consultations to be provided as part of the `c/` prefix when adding or editing a student
- Construct or update a `Person` object that includes the parsed consultations
- Ensure that consultation data is properly stored, persisted, and displayed

The `AddCommandParser` and `EditCommandParser` classes:
- Extract and validate all consultation values provided using the `c/` prefix
- Support multiple datetime input formats (e.g., `dd/MM/yyyy HH:mm`, `yyyy-MM-dd HH:mm`, etc.)
- Create corresponding `Consultation` objects and include them when constructing the `Person` to be added or edited

#### Execution Flow

Given below is an example usage scenario and how the consultation mechanism behaves during both add and edit operations.

**Step 1.** The user executes either:
- `add n/John Doe s/A0123456X e/john@u.nus.edu m/CS2103T c/22/10/2025 15:30`, or
- `edit 1 c/25/10/2025 14:00`  
  to add or update a student's consultation record.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies the command type (`add` or `edit`) and creates the respective command parser.

**Step 3.** The parser (`AddCommandParser` or `EditCommandParser`) processes the input:
- Extracts consultation values from each `c/` prefix
- Validates and parses them into `LocalDateTime` objects using multiple supported formats
- Creates a list of `Consultation` objects

**Step 4.** During command execution:
- The `AddCommand` constructs a new `Person` with the consultations included as part of their attributes
- The `EditCommand` retrieves the target `Person`, replaces their existing consultations with the new list, and updates the model
- Both commands call `Model#setPerson()` to commit the change

**Step 5.** The updated `Person` (with consultations) is persisted to storage and reflected in the UI.  
Consultation details can then be viewed through the `view` command, which displays all recorded consultations for that student in the result panel.

The following sequence diagram shows how consultations are processed as part of the add workflows:

<puml src="diagrams/ConsultationSequenceDiagram.puml" alt="ConsultationSequenceDiagram" />

*The edit command follows a similar sequence, with the difference being that it retrieves an existing student from the model, updates their consultations, and re-saves the modified record.*

#### Design Considerations

**Aspect: Integration within `add` and `edit` commands vs standalone `consult` command**

* **Alternative 1 (current choice):** Integrate consultation handling within existing `add` and `edit` commands
    * Pros: Keeps the command set simple and intuitive — no need for an additional `consult` command. Ensures that all student-related data (attendance, consultations, grades) are managed consistently within a single workflow. Prevents duplicate logic for modifying the same `Person` object.
    * Cons: Less explicit — users cannot add a consultation directly without editing the student. Parsing logic for `add` and `edit` becomes more complex as new optional fields are introduced.

* **Alternative 2:** Implement a dedicated `consult` command for adding consultations
    * Pros: Clear separation of concerns. Easier to track when consultations are added. Provides finer-grained control for managing consultation records.
    * Cons: Adds a new command and parser, increasing system complexity. Introduces overlapping functionality with `edit`. Users would need to remember and use multiple commands to update the same student record.

**Aspect: Consultation data structure**

* **Alternative 1 (current choice):** Store consultations as a list of `Consultation` objects within `Person`
    * Pros: Maintains a one-to-many relationship (one student → many consultations). Easy to extend in the future (e.g., add notes or duration fields). Keeps related data encapsulated within the `Person` model.
    * Cons: Slightly larger memory footprint per student. Requires additional serialization logic for saving and loading lists of consultations.

* **Alternative 2:** Store consultations as a simple formatted `String` within `Person`
    * Pros: Simpler to implement and serialize. Minimal model changes required.
    * Cons: Harder to validate and manipulate programmatically. Loses type safety and flexibility for future enhancements.

**Aspect: Datetime input format**

* **Alternative 1 (current choice):** Accept multiple datetime formats for user flexibility
    * Pros: Improves user experience by accommodating a variety of date/time input styles. Reduces likelihood of input errors due to format mismatch.
    * Cons: Increases parser complexity — more code paths to validate and test. Slightly higher chance of ambiguity between formats.

* **Alternative 2:** Enforce a single strict datetime format (e.g., `yyyy-MM-dd HH:mm`)
    * Pros: Simpler parsing logic and clearer documentation. Ensures data consistency across all entries.
    * Cons: Less user-friendly; rejects valid but differently formatted datetimes. May frustrate users unfamiliar with the required format.

### Remark Feature

#### Implementation

The remark feature allows teaching assistants to add personalized notes and observations about individual students. It is implemented through the `Remark` model class, `RemarkCommand` command class, and `RemarkCommandParser` parser class.

**Key Components:**

The `Remark` class:
- Immutable value object storing a remark string
- Validates that remarks contain at least one non-whitespace character using regex pattern `(?s)[^\\s].*`
- Supports multi-line text with the `(?s)` DOTALL flag, allowing remarks to span multiple lines
- Implements proper `equals()` and `hashCode()` for comparison operations
- Stored as an optional field in the `Person` class (can be null)

The `RemarkCommand`:
- Takes a student ID and a remark text as parameters
- Uses student ID lookup to find the target student via `Model#findPersonByStudentId()`
- Creates a new `Person` object with the updated remark (defensive copying)
- Replaces any existing remark (not cumulative)
- Only accepts student IDs, not index-based lookup

The `RemarkCommandParser`:
- Parses user input in the format `remark s/STUDENT_ID r/REMARK`
- Extracts and validates both the student ID and remark text
- Uses `PREFIX_STUDENT_ID` (s/) and `PREFIX_REMARK` (r/) for parameter identification
- Validates that both index and student ID are not provided simultaneously (throws error if both detected)
- Validates that both required prefixes are present and not duplicated
- Trims whitespace from the remark text before validation

**Execution Flow:**

Given below is an example usage scenario and how the remark mechanism behaves.

**Step 1.** The user executes `remark s/A0123456X r/Needs extra help with OOP concepts` to add a remark for student A0123456X.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a remark command and creates a `RemarkCommandParser`.

**Step 3.** `RemarkCommandParser` parses the arguments:
- Tokenizes the input using `ArgumentTokenizer` with `PREFIX_STUDENT_ID` and `PREFIX_REMARK`
- Validates that both preamble (index) and student ID prefix are not present simultaneously
- If both are present: Throws `ParseException` with message "Conflicting parameters detected. Please use either index or student ID — not both."
- Verifies both required prefixes are present
- Verifies no duplicate prefixes using `ArgumentMultimap#verifyNoDuplicatePrefixesFor()`
- Extracts student ID value and parses it using `ParserUtil#parseStudentId()`
- Extracts remark value and parses it using `ParserUtil#parseRemark()`
- Creates a `RemarkCommand` with the validated student ID and remark

**Step 4.** When `RemarkCommand#execute()` is called:
- Calls `Model#findPersonByStudentId()` to locate the student
- Throws `CommandException` with message "No student found with ID: [ID]" if student not found
- Creates a new `Person` object using `createStudentWithRemark()` helper method
- The helper method constructs a new `Person` with all existing fields preserved except remark is replaced
- Calls `Model#setPerson(studentToEdit, editedStudent)` to update the model
- Returns a `CommandResult` with success message showing the updated student details

**Step 5.** The model persists the changes:
- Updated person list triggers storage save via JSON serialization
- Remark is serialized as a nullable string field in `JsonAdaptedPerson`
- If remark is null, it is saved as null in JSON
- Data is written to disk automatically

The following sequence diagram shows how a remark operation works:

<puml src="diagrams/RemarkSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `remark s/A0123456X r/...` Command" />

<box type="info" seamless>

**Note:** The lifeline for `RemarkCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

**UI Display:**

The remark is displayed in the student card in the UI:
- Shows in a dedicated section with a 📝 icon
- Displays "No remarks" if the remark field is null
- Supports multi-line display with text wrapping enabled
- Located after the consultations section in the card layout

#### Design Considerations

**Aspect: Student identification method**

* **Alternative 1 (current choice):** Only support student ID-based lookup
  * Pros: Unambiguous identification - student ID is unique. Works regardless of filter state or displayed list. Consistent with the nature of remarks as persistent student attributes. Prevents accidental remark assignment to wrong student due to changing list order.
  * Cons: Requires user to know or look up the student ID. Cannot use index for quick operations on visible students. More typing required compared to index.

* **Alternative 2:** Support both index and student ID lookup
  * Pros: More flexible for different workflows. Index is faster for students currently visible in list.
  * Cons: Risk of assigning remarks to wrong student if list changes. Index-based lookup is context-dependent. More complex parsing logic.

**Aspect: Remark update behavior**

* **Alternative 1 (current choice):** Replace existing remark with new remark
  * Pros: Simple and predictable behavior. Consistent with edit command semantics. Prevents remark accumulation and clutter. Easy to understand - latest remark is always shown.
  * Cons: Previous remark content is lost. No history of remark changes. Cannot append to existing remarks without manually copying old content.

* **Alternative 2:** Append new remarks to existing remarks
  * Pros: Preserves history of observations. Can build up notes over time. No data loss.
  * Cons: Remarks can grow very long. No clear structure or timestamps. Harder to find recent information. Needs delimiter or formatting to separate entries.

**Aspect: Multi-line support**

* **Alternative 1 (current choice):** Support multi-line remarks with DOTALL regex mode
  * Pros: Allows detailed notes with paragraphs and structure. Natural for longer observations. Flexible formatting for complex information.
  * Cons: Regex validation more complex. UI must handle multi-line display properly. Can take more vertical space in display.

* **Alternative 2:** Restrict to single-line remarks
  * Pros: Simpler validation. Predictable display size. Encourages concise notes.
  * Cons: Limited expressiveness. Cannot organize complex information. Need multiple short remarks instead of structured notes.

**Aspect: Remark storage location**

* **Alternative 1 (current choice):** Store remark as optional field in Person class
  * Pros: Direct association between person and remark. Easy to access when viewing student. Simplifies serialization. Follows object-oriented principles.
  * Cons: Remark is duplicated if person object is copied. Cannot easily query all remarks independently.

* **Alternative 2:** Store remarks in separate RemarkBook class
  * Pros: Separation of concerns. Can query all remarks independently. Supports remark-focused operations.
  * Cons: More complex architecture. Need to maintain relationships between Person and Remark. More complex serialization logic. Overhead for simple feature.

**Aspect: Empty remark handling**

* **Alternative 1 (current choice):** Reject empty or whitespace-only remarks
  * Pros: Ensures remarks contain meaningful content. Prevents accidental empty submissions. Clear validation feedback to user.
  * Cons: Cannot explicitly clear a remark using the remark command. Need to use edit command or delete/re-add student to remove remark.

* **Alternative 2:** Accept empty remarks to clear existing remark
  * Pros: Provides direct way to remove remarks. Consistent with "set remark to X" semantics where X can be empty.
  * Cons: Easy to accidentally clear remarks. Less intuitive validation - why validate format if empty is allowed? Ambiguous whether empty input means "clear" or "keep existing".

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* needs to manage students across multiple modules
* responsible for tracking consultations and monitoring academic progress
* prefers quick keyboard-driven workflows during tutorials and consultations
* is reasonably comfortable using CLI-based desktop applications
* values efficiency and organization when handling large amounts of student data

**Value proposition**: manage student information and academic progress faster and more efficiently than a typical mouse/GUI-driven app.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                       | I want to …                                                   | So that I can …                                                          |
|----------|------------------------------|---------------------------------------------------------------|--------------------------------------------------------------------------|
| `* * *`  | TA                           | add a new student's details                                   | have their basic information readily available (name, ID, email, module) |
| `* * *`  | TA                           | view student contact details                                  | check in with my student's progress                                      |
| `* * *`  | TA                           | edit an existing student's details                            | update outdated or incorrect information easily                          |
| `* * *`  | TA                           | delete a student                                              | remove students who have dropped the module or are no longer relevant    |
| `* * *`  | TA                           | view student grades                                           | track my students' academic progress                                     |
| `* * *`  | TA                           | add or update grades for a student                            | record their assessment results consistently                             |
| `* * *`  | TA                           | delete specific grades from a student                         | correct grading mistakes or remove outdated assessments                  |
| `* * *`  | TA                           | mark a student's attendance                                   | award marks according to their attendance                                |
| `* *`    | TA                           | unmark or edit attendance for a student                       | fix mistakes in attendance tracking                                      |
| `* *`    | TA                           | add consultations to my calendar                              | keep track of consultation sessions and follow-ups                       |
| `* *`    | TA                           | delete consultations                                          | free up slots for other students                                         |
| `* *`    | TA                           | add custom tags to students (e.g., "struggling", "excellent") | quickly identify students who need special attention                     |
| `* *`    | TA                           | add special notes/remarks for each student                    | record qualitative feedback for future reference                         |
| `* * *`  | busy TA for multiple modules | search for a student by typing partial names                  | quickly find their information during consultations                      |
| `* *`    | TA                           | filter students by tags or modules                            | view specific subsets of students efficiently                            |
| `* *`    | TA with many modules         | view all my modules that I teach                              | easily track all modules from one glance                                 |
| `*`      | TA with many classes         | view my timetable schedule                                    | view my schedule from one location                                       |
| `*`      | TA                           | randomly select students for class participation              | ensure fair distribution of participation opportunities                  |
| `*`      | TA                           | group students for class projects                             | keep track of group work allocations                                     |
| `*`      | TA                           | add reminders to follow up with students                      | ensure their questions are addressed after class                         |


### Use cases

(For all use cases below, the **System** is the `TeachMate` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC1 - Add a new student's details**

**MSS**

1.  User requests to add a new student.
2.  TeachMate requests for details of the student.
3.  User enters the requested details.
4.  User saves the details.
5.  TeachMate adds the new student and displays the new student's details.
    Use case ends.

**Extensions**

- 3a. TeachMate detects an error in the entered details.
  - 3a1. TeachMate requests for the correct details.
  - 3a2. User enters new detail.
  - Steps 3a1-3a2 are repeated until the data entered are correct.
  - Use case resumes from step 4.

**Use case: UC2 - Add consultations for a student**

**MSS**

1.  User requests to add or edit a student with consultation details.
2.  TeachMate requests for student information and consultation date/time.
3.  User enters student details including one or more consultation date/times.
4.  TeachMate validates the consultation format and adds the student with consultations.
5.  TeachMate displays the student details with scheduled consultations.
    Use case ends.

**Extensions**

- 3a. Entered date/time format is invalid.
  - 3a1. TeachMate displays error with supported date/time formats.
  - 3a2. User enters a valid date/time in one of the supported formats.
  - Steps 3a1-3a2 are repeated until valid format is used.
  - Use case resumes from step 4.

- 3b. User is editing an existing student.
  - 3b1. User provides the student index and new consultation details.
  - 3b2. TeachMate replaces existing consultations with the new ones.
  - Use case resumes from step 5.

**Use case: UC3 - Mark a student's attendance**

**MSS**

1.  User requests to mark attendance for a student for a specific week.
2.  TeachMate requests for the student identifier and week number.
3.  User provides the student (by index or student ID), week number (1-13), and status (present/absent).
4.  TeachMate marks the attendance and displays the updated status.
    Use case ends.

**Extensions**

- 3a. The student index is invalid.
  - 3a1. TeachMate displays 'The student index provided is invalid'.
  - 3a2. User enters a valid index from the displayed list.
  - Use case resumes from step 4.

- 3b. The student ID is not found.
  - 3b1. TeachMate displays 'No student found with ID: [ID]'.
  - 3b2. User enters a valid student ID.
  - Use case resumes from step 4.

- 3c. The week number is invalid.
  - 3c1. TeachMate displays 'Week should be a number between 1 and 13 (inclusive)'.
  - 3c2. User enters a valid week number.
  - Use case resumes from step 4.

- 3d. User wants to unmark attendance.
  - 3d1. User specifies 'unmark' as the status.
  - 3d2. TeachMate removes the attendance record for that week.
  - Use case ends.

**Use case: UC4 - Add a remark to a student**

**MSS**

1.  User requests to add a remark to a specific student.
2.  TeachMate requests for the student ID and remark text.
3.  User provides the student ID and enters the remark.
4.  TeachMate adds the remark to the student and displays the updated student details.
    Use case ends.

**Extensions**

- 3a. Student ID does not exist in the system.
  - 3a1. TeachMate displays 'No student found with ID: [ID]'.
  - 3a2. User enters a valid student ID.
  - Use case resumes from step 4.

- 3b. Remark text is empty or contains only whitespace.
  - 3b1. TeachMate displays 'Remarks should not be blank'.
  - 3b2. User enters valid remark text.
  - Use case resumes from step 4.

*{More to be added}*

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
3. The system should be intuitive enough for new TAs with basic command-line familiarity to perform core operations (add, view, list students) within 15 minutes of first use.
4. The system should not lose data during normal operations. All changes should be persisted to storage within 1 second of command execution.
5. All commands should execute and provide feedback within 2 seconds under normal load conditions.
6. Should support TAs managing up to 10 different modules simultaneously without performance degradation.
7. Error messages should be clear, specific, and actionable, guiding users to correct their input without needing to reference documentation.
8. Code should follow standard software engineering practices to allow for incremental feature additions and modifications.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **TA**: Teaching Assistant - A university staff member or senior student who assists professors in conducting tutorials, grading assignments, and providing academic support to students
* **Module**: A course or subject offered by NUS, identified by a unique module code (e.g., CS2103T)
* **Module Code**: NUS standard format for course identification, consisting of 2-4 uppercase letters followed by exactly 4 digits and an optional suffix of 0-2 uppercase letters (e.g., CS2103T, ACC1701XA, GESS1000, CS2040DE, LL4008BV, BMA5001)
* **Student ID**: A unique identifier assigned to each NUS student, following the format AXXXXXXXY where X represents digits (0-9) and Y represents an uppercase letter (e.g., A0123456X)
* **Index**: A positive integer representing the position of a student in the currently displayed list, used for quick command-line reference
* **Tag**: A custom label (e.g., "struggling", "excellent", "international") assigned to students to quickly identify those requiring special attention or categorization
* **Consultation**: A scheduled one-on-one or small group meeting between a TA and student(s) for academic assistance
* **Command**: A text-based instruction entered by the user to perform operations in TeachMate, following the format `COMMAND_WORD [parameters]`
* **Prefix**: A short identifier (e.g., n/, s/, e/, m/, t/) used before parameter values in commands to specify which field the value corresponds to
* **Duplicate Student**: A student entry that shares the same Student ID with an existing student in the system, regardless of other fields
* **Remark**: A personalized note or observation recorded by a TA about an individual student, supporting multi-line text for detailed tracking of student progress, learning needs, or behavior patterns

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample students. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Adding a student

1. Adding a new student to TeachMate

   1. Prerequisites: Application is running with sample data loaded.

   1. Test case: `add n/John Tan s/A0123456X e/johntan@u.nus.edu m/CS2103T m/CS2101`<br>
      Expected: New student "John Tan" is added with ID A0123456X. Success message shows the added student details. Student appears in the list.

   1. Test case: `add n/Mary Lee s/A0123456X e/mary@u.nus.edu m/CS2103T` (duplicate student ID)<br>
      Expected: No student is added. Error message indicates student with this ID already exists.

   1. Test case: `add n/Jane s/A1234567B e/invalid-email m/CS2103T` (invalid email)<br>
      Expected: No student is added. Error message shows email validation requirements.

   1. Test case: `add n/Bob s/INVALID e/bob@u.nus.edu m/CS2103T` (invalid student ID format)<br>
      Expected: No student is added. Error message shows student ID format requirements.

### Editing a student

1. Editing student details

   1. Prerequisites: List all students using `list`. At least one student in the list.

   1. Test case: `edit 1 e/newemail@u.nus.edu`<br>
      Expected: Email of 1st student is updated. Success message shows updated details.

   1. Test case: `edit 1 m/CS2103T m/CS2101` (replacing modules)<br>
      Expected: Modules of 1st student are replaced with CS2103T and CS2101. Previous modules are removed.

   1. Test case: `edit 0 n/Test` (invalid index)<br>
      Expected: No student is edited. Error message shows invalid index.

### Deleting a student

1. Deleting a student from TeachMate

   1. Prerequisites: List all students using `list`. Multiple students in the list.

   1. Test case: `delete 1`<br>
      Expected: First student is deleted from the list. Success message shows the deleted student's details.

   1. Test case: `delete 0`<br>
      Expected: No student is deleted. Error message shows invalid index.

   1. Test case: `delete x` (where x is larger than the list size)<br>
      Expected: No student is deleted. Error message shows invalid index.

### Viewing a student

1. Viewing detailed information about a student

   1. Prerequisites: At least one student in the list.

   1. Test case: `view 1`<br>
      Expected: Detailed information for the 1st student is displayed in the result panel, including attendance history. The list filters to show only this student.

   1. Test case: `view s/A0123456X` (viewing by student ID)<br>
      Expected: Student with ID A0123456X is displayed with full details. List filters to show only this student.

   1. Test case: `view s/A9999999Z` (non-existent student ID)<br>
      Expected: Error message shows "No student found with student ID: A9999999Z".

   1. Test case: `view 0`<br>
      Expected: Error message shows invalid index.

### Adding a remark to a student

1. Adding a remark to an existing student

   1. Prerequisites: List all students using the `list` command. At least one student in the list. Note a student's ID (e.g., A0123456X).

   1. Test case: `remark s/A0123456X r/Needs extra help with OOP concepts`<br>
      Expected: Remark is added to the student with ID A0123456X. Success message shows the updated student details. The student card in the UI displays the remark with a 📝 icon.

   1. Test case: `remark s/A0123456X r/Strong understanding of data structures. Excellent participation in class.`<br>
      Expected: Multi-line remark is added successfully. Previous remark (if any) is replaced. Student card displays the new remark with text wrapping.

   1. Test case: `remark s/A9999999Z r/Some remark` (where A9999999Z does not exist)<br>
      Expected: No remark is added. Error message shows "No student found with ID: A9999999Z".

   1. Test case: `remark s/A0123456X r/   ` (remark with only whitespace)<br>
      Expected: No remark is added. Error message shows "Remarks should not be blank".

   1. Test case: `remark s/A0123456X` (missing remark prefix)<br>
      Expected: No remark is added. Error message shows invalid command format with usage instructions.

   1. Test case: `remark r/Some remark` (missing student ID prefix)<br>
      Expected: No remark is added. Error message shows invalid command format with usage instructions.

   1. Other incorrect remark commands to try: `remark`, `remark A0123456X r/test` (missing s/ prefix), `remark s/INVALID r/test` (invalid student ID format)<br>
      Expected: Similar error messages indicating the specific validation failure.

### Managing attendance

1. Marking attendance for students

   1. Prerequisites: At least one student in the list.

   1. Test case: `attendance 1 w/1 present`<br>
      Expected: Attendance for week 1 is marked as present for the 1st student. Success message confirms the update. Attendance grid in student card shows green for week 1.

   1. Test case: `attendance s/A0123456X w/2 absent`<br>
      Expected: Attendance for week 2 is marked as absent for student with ID A0123456X. Attendance grid shows red for week 2.

   1. Test case: `attendance all w/3 present`<br>
      Expected: Attendance for week 3 is marked as present for all students. Success message shows number of students updated.

   1. Test case: `attendance 1 w/1 unmark` (where week 1 attendance exists)<br>
      Expected: Attendance record for week 1 is removed for the 1st student. Attendance grid shows grey for week 1.

   1. Test case: `attendance 1 w/14 present` (invalid week number)<br>
      Expected: No attendance is marked. Error message shows week must be between 1 and 13.

   1. Test case: `attendance 0 w/1 present`<br>
      Expected: No attendance is marked. Error message shows invalid index.

### Managing grades

1. Adding and updating grades

   1. Prerequisites: At least one student in the list.

   1. Test case: `grade 1 g/Midterm:85`<br>
      Expected: Grade "Midterm: 85" is added to the 1st student. Success message shows the student name and added grade.

   1. Test case: `grade 1 g/Quiz1:90 g/Assignment1:88`<br>
      Expected: Both grades are added to the 1st student. Success message shows both grades were added.

   1. Test case: `grade 1 g/Midterm:95` (where "Midterm" grade already exists)<br>
      Expected: Existing "Midterm" grade is updated to 95. Success message indicates grade was updated.

   1. Test case: `grade 1 g/MIDTERM:90` (case-insensitive update test)<br>
      Expected: Existing "Midterm" grade is updated to 90. Case-insensitive matching succeeds.

   1. Test case: `grade 1 g/Final:101`<br>
      Expected: No grade is added. Error message shows "Grades should be a number between 0 and 100".

   1. Test case: `grade 1 g/:85` (missing assignment name)<br>
      Expected: No grade is added. Error message shows "Assignment name should not be blank".

2. Deleting grades from a student

   1. Prerequisites: Student at index 1 has grades for "Midterm" and "Quiz1".

   1. Test case: `deletegrade 1 g/Midterm`<br>
      Expected: "Midterm" grade is deleted from the 1st student. Success message confirms deletion.

   1. Test case: `deletegrade 1 g/Quiz1 g/Assignment1` (where only "Quiz1" exists)<br>
      Expected: No grades are deleted. Error message shows "Grade not found for assignment: Assignment1".

   1. Test case: `deletegrade 1 g/midterm` (case-sensitive test)<br>
      Expected: No grades are deleted. Error message shows "Grade not found for assignment: midterm".

   1. Test case: `deletegrade 1` (missing grade prefix)<br>
      Expected: No grade is deleted. Error message shows invalid command format with usage instructions.

### Managing tags

1. Adding and removing tags

   1. Prerequisites: At least one student in the list.

   1. Test case: `tag 1 t/Struggling t/NeedsHelp`<br>
      Expected: Tags "Struggling" and "NeedsHelp" are added to the 1st student. Success message shows updated tags.

   1. Test case: `tag s/A0123456X t/Excelling`<br>
      Expected: Tag "Excelling" is added to student with ID A0123456X.

   1. Test case: `untag 1 t/Struggling`<br>
      Expected: Tag "Struggling" is removed from the 1st student. Success message confirms removal.

   1. Test case: `tag 1 t/ExistingTag` (where tag already exists)<br>
      Expected: Tag is not duplicated. Success message may indicate tag already exists.

### Finding and filtering students

1. Searching for students

   1. Prerequisites: Multiple students in the list with different names and tags.

   1. Test case: `find John`<br>
      Expected: List shows only students with "John" in their name. Number of matching students displayed in result message.

   1. Test case: `find alex david` (multiple keywords)<br>
      Expected: List shows students with "alex" OR "david" in their names.

   1. Test case: `filter t/Struggling`<br>
      Expected: List shows only students tagged with "Struggling".

   1. Test case: `filter t/Struggling t/NeedsHelp`<br>
      Expected: List shows only students with BOTH "Struggling" AND "NeedsHelp" tags.

   1. Test case: `list`<br>
      Expected: All students are displayed again, removing any filters.

### Saving data

1. Dealing with missing/corrupted data files

   1. Test case: Delete the data file at `[JAR file location]/data/addressbook.json` and restart the application.<br>
      Expected: Application starts with sample data loaded.

   1. Test case: Edit the data file to add an invalid student ID (e.g., change "A0123456X" to "INVALID"), then restart the application.<br>
      Expected: Application starts with an empty student list, discarding the corrupted data.

   1. Test case: Edit the data file to make it invalid JSON (e.g., remove a closing brace), then restart the application.<br>
      Expected: Application starts with an empty student list.

--------------------------------------------------------------------------------------------------------------------

## Appendix: Planned Enhancements

Team size: 5

1. **Make grade assignment name matching consistent between grade and deletegrade commands**: Currently, the `grade` command uses case-insensitive matching (e.g., "midterm" matches "Midterm"), while `deletegrade` uses case-sensitive matching. We plan to make both commands use case-insensitive matching for consistency. This will allow users to delete grades without remembering the exact capitalization, matching the update behavior.

2. **Enhance find command to support partial matching**: Currently, `find` only matches complete words. We plan to support more flexible, token-based matching so any full word in a name can be matched (case-insensitive). This is still word-by-word (complete-word) matching — not character-level substrings. For example, searching `John` will match names containing the complete word "John" (e.g., "John Tan", "John Lim"); searching `chae` will NOT match names where `chae` is only a partial word (e.g., "Chaewon", "Chaeyoung").

3. **Add confirmation prompt for bulk attendance operations**: Currently, `attendance all w/1 present` marks attendance for all students without confirmation. We plan to add a confirmation prompt showing the number of students affected: "This will mark attendance for [N] students. Confirm? (y/n)".

4. **Improve error message specificity for invalid module codes**: Currently, invalid module codes show a generic "Module codes should be in NUS format (e.g. CS2103T, ACC1701XA, GESS1000, BMA5001)" message. We plan to make this more specific by showing the exact format requirements: "Module code must be 2-4 uppercase letters followed by exactly 4 digits and an optional suffix of 0-2 uppercase letters (e.g., CS2103T, ACC1701XA, GESS1000, BMA5001)".

5. **Add ability to clear all consultations for a student**: Currently, there's no direct way to remove all consultations from a student without using the edit command and omitting the `c/` prefix. We plan to add support for `edit INDEX c/` (empty consultation prefix) to explicitly clear all consultations.

6. **Enhance grade display sorting**: Currently, grades are displayed sorted alphabetically by assignment name. We plan to add an optional sort order preference (alphabetical or by date added) that users can set, allowing TAs to see the most recent grades first if preferred.

7. **Add validation for duplicate module codes in add/edit commands**: Currently, users can add the same module code multiple times (e.g., `m/CS2103T m/CS2103T`). We plan to detect and prevent duplicate module codes with an error message: "Duplicate module code detected: [code]. Each module should only be listed once."

8. **Allow for special characters in student name**: Currently, student names can only contain alphanumeric characters and spaces. Special characters (e.g., accented letters such as `ā` in `Prakāś`), punctuation (including apostrophes `'` and slashes `/`), symbols, and non-alphabet scripts (e.g., Chinese, Japanese, or any other non-Latin characters) are not allowed. We plan to add parsing support for a broader set of characters in student names in the future.

9. **Enhance attendance system to be mapped to modules**: Currently, each student has one attendance list that does not take into account of multiple modules, making attendance tracking difficult for such students. We plan to change attendance such that a student will have a separate attendance record for each module taking.

10. **Add index-based lookup support for remark command**: Currently, the `remark` command only accepts student ID (e.g., `remark s/A0123456X r/...`) and does not support index-based lookup like other commands. Users who try `remark 1 r/Needs help with work` will encounter an error. We plan to add support for both index and student ID, matching the behavior of `tag`, `untag`, and `view`. The format will be: `remark INDEX r/REMARK` or `remark s/STUDENT_ID r/REMARK`, allowing users to choose the most convenient method based on their workflow.

--------------------------------------------------------------------------------------------------------------------

## Appendix: Effort

### Difficulty Level and Challenges

TeachMate presented moderate-to-high difficulty compared to AB3 due to several key expansions:

**Multiple Entity Types and Relationships:**
While AB3 deals with a single `Person` entity, TeachMate manages multiple interconnected entities:
- **Person** with student-specific fields (StudentId, ModuleCode, Grades, AttendanceRecord, Consultations, Remarks)
- **AttendanceRecord** with Week and AttendanceStatus mappings
- **Grade** with assignment tracking
- **Consultation** with datetime parsing supporting multiple formats
- **Remark** with multi-line text support

This required careful design of object relationships, defensive copying strategies, and comprehensive JSON serialization/deserialization logic.

**Complex Command Variations:**
Several commands required significant extensions beyond AB3's simple CRUD operations:
- **Attendance command:** Supports three identification methods (index, student ID, and bulk "all" keyword) with week-based tracking
- **View command:** Dual lookup methods (index and student ID) with formatted multi-section output
- **Tag/Untag commands:** Support both index and student ID lookup with additive/subtractive operations
- **Remark command:** Multi-line text validation using DOTALL regex mode
- **List command:** Enhanced with module-based filtering

**UI Complexity:**
The UI required substantial enhancements beyond AB3:
- **Attendance visualization:** 13-week grid display with color-coded rectangles (grey/green/red) per student card
- **Multi-field display:** Cards show modules, grades, consultations, attendance, tags, and remarks with appropriate icons
- **Text wrapping:** Support for multi-line remarks with proper layout
- **Dual themes:** Maintained both light and dark theme compatibility across all new UI elements

### Effort Required

**Implementation Effort:**
- **Attendance system:** ~15 hours (model classes, three command variations, UI grid visualization, comprehensive testing)
- **Grade system:** ~8 hours (Grade model, validation, duplicate checking, UI styling)
- **Consultation system:** ~10 hours (datetime parsing with 4 format support, integration with add/edit commands)
- **Remark system:** ~12 hours (multi-line support, parser, UI display, documentation)
- **View command:** ~6 hours (dual lookup, formatted output, integration with attendance display)
- **Tag/Untag enhancements:** ~5 hours (dual lookup methods, validation)
- **UI enhancements:** ~20 hours (attendance grid, icon layout, multi-field cards, theme compatibility)
- **Testing and bug fixes:** ~25 hours (unit tests, integration tests, manual testing, bug resolution)
- **Documentation:** ~15 hours (User Guide, Developer Guide, diagrams, use cases)

**Total estimated effort:** ~116 person-hours

### Reuse and Libraries

**JavaFX Framework:**
We reused JavaFX for UI components (Labels, HBoxes, VBoxes, GridPanes). This saved approximately 30-40 hours that would have been spent implementing custom UI rendering. Our main effort was in:
- Layout design in FXML files (PersonListCard.fxml, PersonCard.java)
- CSS styling for new components (LightTheme.css, DarkTheme.css)
- Binding UI to model data

**Jackson JSON Library:**
Reused Jackson for JSON serialization/deserialization. This saved ~10 hours of manual JSON handling. Our adaptation work is in:
- JsonAdaptedPerson.java: Extended to handle all new fields (attendance, grades, consultations, remarks)
- Added validation logic for deserialization of complex types
- Null-safety handling for optional fields

**Java Time API:**
Used `java.time.LocalDateTime` for consultation datetime handling. Saved ~5 hours compared to implementing custom date/time logic. Adaptation in:
- Consultation.java: Multiple format parsing using DateTimeFormatter
- Support for 4 different datetime input formats for user convenience

**AB3 Foundation:**
Built upon AB3's architecture (Model-View-Controller, Command pattern, Parser framework). This provided solid foundation saving ~40 hours. Major adaptations:
- Extended Command pattern with 8+ new command types
- Enhanced Model with new entity types and relationships
- Expanded Storage layer for complex object serialization

**Total effort saved through reuse:** ~85-95 hours (~45% of total development time)

### Achievements

1. **Comprehensive TA Management System:** Successfully transformed AB3 from a simple contact manager into a full-featured teaching assistant tool with attendance tracking, grade management, consultation scheduling, and student notes.

2. **Robust Multi-format Support:** Implemented flexible datetime parsing, multi-line text support, and various command format variations that significantly improve user experience.

3. **Rich Visual Feedback:** Created an intuitive UI with color-coded attendance grids, icon-based layouts, and clear visual hierarchy that allows TAs to quickly scan and assess student information.

4. **Extensive Documentation:** Produced comprehensive User Guide and Developer Guide with UML diagrams, use cases, design rationales, and manual testing instructions.

5. **High Test Coverage:** Maintained code quality with extensive unit and integration tests across all new features.

6. **Scalability:** Designed architecture to handle up to 1000 students and 10 modules as per NFRs, with efficient filtering and lookup operations.

The project successfully met all functional and non-functional requirements while maintaining code quality, documentation standards, and usability principles.
