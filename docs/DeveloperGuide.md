---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

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
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_

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
- Creates appropriate `ViewCommand` objects with either an `Index` or `StudentId` parameter

**Execution Flow:**

Given below is an example usage scenario and how the view mechanism behaves.

**Step 1.** The user executes `view 1` or `view s/A0123456X` to view a student's details.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a view command and creates a `ViewCommandParser`.

**Step 3.** `ViewCommandParser` parses the arguments:
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

The `AttendanceRecord` class:
- Stores attendance using a `Map<Week, AttendanceStatus>` structure
- Unmarked weeks are absent from the map (no entry = unmarked state)
- `markAttendance()` adds or updates an entry in the map
- `unmarkAttendance()` removes an entry from the map

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

**Aspect: Bulk attendance marking**

* **Alternative 1 (current choice):** Support `all` keyword (without prefix) to mark all students
  * Pros: Very convenient for TAs marking full-class attendance. Saves time when all students share same status. Single command instead of N commands. Clean syntax without prefix clutter.
  * Cons: Requires special parsing logic to distinguish `all` from numeric index. All-or-nothing operation (cannot mark most students one way, exceptions another way). Risk of accidental bulk operations.

* **Alternative 2:** Require individual marking for each student
  * Pros: More explicit, less risk of mistakes. Simpler parsing logic. Forces TAs to verify each student.
  * Cons: Tedious for large classes. Time-consuming when most students have same status. Many repetitive commands.

**Aspect: Status parameter position**

* **Alternative 1 (current choice):** Status must come after week parameter (`w/1 present`)
  * Pros: Clear and consistent command structure. Status is visually grouped with week. Easier to parse - no ambiguity about parameter order. Natural reading flow (week then status).
  * Cons: Slightly longer to type. Cannot put status at the beginning for emphasis.

* **Alternative 2:** Allow flexible status positioning (before or after week)
  * Pros: More flexibility for users. Can emphasize status by putting it first.
  * Cons: More complex parsing logic. Ambiguous command structure. Harder to document and explain. Can lead to user confusion about accepted formats.

**Aspect: Week number validation**

* **Alternative 1 (current choice):** Validate weeks are between 1-13
  * Pros: Matches typical NUS semester structure. Prevents obviously invalid data. Provides clear error messages to users.
  * Cons: Not flexible for special terms or non-standard schedules. Hard-coded constraint.

* **Alternative 2:** Allow any positive week number
  * Pros: Flexible for different semester structures. Works for special terms, summer sessions, etc.
  * Cons: Allows potentially meaningless data (e.g., week 100). No validation against errors. Harder to generate meaningful reports.

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
- Checks for duplicate grades (same assignment name) and prevents them
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
- Checks for duplicate grades by comparing assignment names
- Creates a new `Person` with the existing grades plus the new grade
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

* **Alternative 1 (current choice):** Prevent duplicates by checking assignment names
  * Pros: Ensures data consistency, prevents accidental overwrites. Clear error message to user.
  * Cons: Cannot easily update an existing grade - must delete first then add. More validation logic needed.

* **Alternative 2:** Allow duplicates and keep all versions
  * Pros: Maintains history of grade changes. Simpler implementation.
  * Cons: Confusing for users which grade is "current". Takes more storage space. Need additional logic to determine which grade to display.

**Aspect: Grade validation**

* **Alternative 1 (current choice):** Validate in `Grade` constructor
  * Pros: Ensures all Grade objects are valid (fail-fast). Defensive programming - impossible to create invalid grade. Validation logic centralized in one place.
  * Cons: Cannot create Grade object with invalid data even for testing purposes.

* **Alternative 2:** Validate only when parsing user input
  * Pros: More flexible for internal use and testing. Can create temporary Grade objects during processing.
  * Cons: Risk of invalid grades propagating through the system. Validation logic scattered across multiple parsers.

### Consultation Feature

#### Implementation

The consultation feature allows teaching assistants to record and view student consultation sessions as part of each student’s profile. Unlike other standalone commands, consultations are implemented as an **attribute of each student**, and can be added or modified **only through the `add` or `edit` commands**.

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
- `add n/John Doe s/A0123456X e/john@u.nus.edu m/CS2103T c/22-10-2025 15:30`, or
- `edit 1 c/25-10-2025 14:00`  
  to add or update a student’s consultation record.

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

The following sequence diagram shows how consultations are processed as part of the add and edit workflows:

<puml src="diagrams/ConsultationSequenceDiagram.puml" alt="ConsultationSequenceDiagram" />

#### Design Considerations

**Aspect: Integration within `add` and `edit` commands vs standalone `consult` command**

* **Alternative 1 (current choice):** Integrate consultation handling within existing `add` and `edit` commands
    * Pros: Keeps the command set simple and intuitive — no need for an additional `consult` command. Ensures that all student-related data (attendance, consultations, grades) are managed consistently within a single workflow. Prevents duplicate logic for modifying the same `Person` object.
    * Cons: Less explicit — users cannot add a consultation directly without editing the student. Parsing logic for `add` and `edit` becomes more complex as new optional fields are introduced.

* **Alternative 2:** Implement a dedicated `consult` command for adding consultations
    * Pros: Clear separation of concerns. Easier to track when consultations are added. Provides finer-grained control for managing consultation records.
    * Cons: Adds a new command and parser, increasing system complexity. Introduces overlapping functionality with `edit`. Users would need to remember and use multiple commands to update the same student record.

---

**Aspect: Consultation data structure**

* **Alternative 1 (current choice):** Store consultations as a list of `Consultation` objects within `Person`
    * Pros: Maintains a one-to-many relationship (one student → many consultations). Easy to extend in the future (e.g., add notes or duration fields). Keeps related data encapsulated within the `Person` model.
    * Cons: Slightly larger memory footprint per student. Requires additional serialization logic for saving and loading lists of consultations.

* **Alternative 2:** Store consultations as a simple formatted `String` within `Person`
    * Pros: Simpler to implement and serialize. Minimal model changes required.
    * Cons: Harder to validate and manipulate programmatically. Loses type safety and flexibility for future enhancements.

---

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
- Validates that both required prefixes are present and not duplicated
- Trims whitespace from the remark text before validation

**Execution Flow:**

Given below is an example usage scenario and how the remark mechanism behaves.

**Step 1.** The user executes `remark s/A0123456X r/Needs extra help with OOP concepts` to add a remark for student A0123456X.

**Step 2.** The command is parsed by `AddressBookParser`, which identifies it as a remark command and creates a `RemarkCommandParser`.

**Step 3.** `RemarkCommandParser` parses the arguments:
- Tokenizes the input using `ArgumentTokenizer` with `PREFIX_STUDENT_ID` and `PREFIX_REMARK`
- Verifies both required prefixes are present and the preamble is empty
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

| Priority | As a …                            | I want to …                                                   | So that I can …                                                          |
|----------|-----------------------------------|---------------------------------------------------------------|--------------------------------------------------------------------------|
| `* * *`  | TA                                | add a new student’s details                                   | have their basic information readily available (name, ID, email, module) |
| `* * *`  | TA                                | view student contact details                                  | check in with my student’s progress                                      |
| `* * *`  | TA                                | view student grades                                           | track my students’ academic progress                                     |
| `* *`    | TA                                | view student assignment submissions                           | track their assignment progress                                          |
| `* *`    | TA with many modules              | view all my modules that I teach                              | easily track all modules from one glance                                 |
| `* * *`  | TA                                | mark a student’s attendance                                   | award marks according to their attendance                                |
| `* *`    | TA with many classes              | view my timetable schedule                                    | view my schedule from one location                                       |
| `* *`    | TA willing to give consultations  | add consultations to my calendar                              | keep track of my timetable                                               |
| `* *`    | TA                                | delete consultations                                          | allocate time for other students                                         |
| `* *`    | TA                                | add custom tags to students (e.g., "struggling", "excellent") | quickly identify students who need special attention                     |
| `* * *`  | busy TA for multiple modules      | search for a student by typing partial names                  | quickly find their information during consultations                      |
| `* *`    | TA                                | add special notes/remarks for each student                    | keep tabs on certain students through remarks                            |
| `* *`    | TA with many things to do         | add tasks that are related to my classes                      | keep track of what to do outside of class                                |
| `*`      | TA                                | randomly select students for class participation              | ensure fair distribution of participation opportunities                  |
| `* *`    | TA                                | flag out students with special needs                          | pay more attention to them                                               |
| `* *`    | TA                                | unmark a student’s attendance if they leave mid-lesson        | easily edit their attendance                                             |
| `*`      | TA                                | assign students to tutorial questions to present              | ensure all students have a fair chance to present their answers          |
| `*`      | TA that gets asked many questions | add reminders to follow up with students after class          | ensure their questions get answered (even if out of syllabus)            |
| `*`      | TA                                | group students up if the module requires group work           | keep track of all groupings                                              |
| `*`      | TA                                | randomly pair/group students for each tutorial session        | all students get to pair up with everyone else                           |



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

* 3a. TeachMate detects an error in the entered details.
      3a1. TeachMate requests for the correct details.
      3a2. User enters new detail.
      Steps 3a1-3a2 are repeated until the data entered are correct.
      Use case resumes from step 4.

**Use case: UC2 - Assign student to tutorial questions to present**

**MSS**

1.  User requests to search for the student.
2.  TeachMate requests for name or ID of the student.
3.  User enters the requested details.
4.  TeachMate displays the matching students.
5.  User selects the student from the matches.
6.  User fills up the tutorial & question number.
7.  TeachMate assigns the question to student and displays the assigned status.
    Use case ends.

**Extensions**

* 3a. TeachMate cannot find any matching student based on the entered details.
      3a1. TeachMate displays 'No Student Found'.
      3a2. User enters new detail.
      Steps 3a1-3a2 are repeated until the detail entered have matches.
      Use case resumes from step 4.

**Use case: UC3 - Group students in the tutorial class**

**MSS**

1.  User requests to group students up
2.  TeachMate requests to select the students and group ID
3.  User enters the group ID and selects the students
4.  TeachMate displays the selected students under the new group 

**Extensions**

* 3a. User does not select any students.
      3a1. TeachMate displays 'No students selected'.
      3a2. User selects at least one student.
      Use case resumes from step 4.

**Use case: UC4 - Add consultations to my calendar**

**MSS**

1.  User requests to add a consultation to the calendar.
2.  TeachMate requests for date, time, student, and description.
3.  User enters the requested details.
4.  TeachMate adds the consultation and displays the scheduled entry.
    Use case ends.

**Extensions**

* 3a. Entered date/time is invalid.
      3a1. TeachMate displays 'Invalid date/time'.
      3a2. User enters a valid date/time.
      Use case resumes from step 4.

**Use case: UC5 - Mark a student's attendance for a class**

**MSS**

1.  User requests to mark a student's attendance.
2.  TeachMate requests for the class/session and the student.
3.  User provides the class/session and selects the student.
4.  TeachMate marks the student's attendance and displays the updated status.
    Use case ends.

**Extensions**

* 3a. Student is not enrolled in the specified class/session.
      3a1. TeachMate displays 'Student not in class'.
      3a2. User selects a valid student in the class/session.
      Use case resumes from step 4.

**Use case: UC6 - Add a remark to a student**

**MSS**

1.  User requests to add a remark to a specific student.
2.  TeachMate requests for the student ID and remark text.
3.  User provides the student ID and enters the remark.
4.  TeachMate adds the remark to the student and displays the updated student details.
    Use case ends.

**Extensions**

* 3a. Student ID does not exist in the system.
      3a1. TeachMate displays 'No student found with ID: [ID]'.
      3a2. User enters a valid student ID.
      Use case resumes from step 4.

* 3b. Remark text is empty or contains only whitespace.
      3b1. TeachMate displays 'Remarks should not be blank'.
      3b2. User enters valid remark text.
      Use case resumes from step 4.

*{More to be added}*

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. Should be able to hold up to 1000 students without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. The system should be intuitive enough for new TAs with basic command-line familiarity to perform core operations (add, view, list students) within 15 minutes of first use.
5. The system should not lose data during normal operations. All changes should be persisted to storage within 1 second of command execution.
6. Student IDs must remain unique across the system. The application should validate all NUS-specific formats (student IDs, module codes) to prevent invalid data entry.
7. All commands should execute and provide feedback within 2 seconds under normal load conditions.
8. Should support TAs managing up to 10 different modules simultaneously without performance degradation.
9. Error messages should be clear, specific, and actionable, guiding users to correct their input without needing to reference documentation.
10. Code should follow standard software engineering practices to allow for incremental feature additions and modifications.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **TA**: Teaching Assistant - A university staff member or senior student who assists professors in conducting tutorials, grading assignments, and providing academic support to students
* **Module**: A course or subject offered by NUS, identified by a unique module code (e.g., CS2103T)
* **Module Code**: NUS standard format for course identification, consisting of 2-3 uppercase letters followed by 4 digits and an optional suffix letter (e.g., CS2103T, MA1521)
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

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

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

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
