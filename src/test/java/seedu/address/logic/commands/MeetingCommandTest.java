package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Meeting;
import seedu.address.model.person.Person;

public class MeetingCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_indexOutOfRange() {
        Index index = Index.fromOneBased(model.getAddressBook().getPersonList().size() + 10);
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        MeetingCommand meetingCommand = new MeetingCommand(index, meeting);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                meetingCommand.execute(model));
    }

    @Test
    public void execute_indexOutOfRangeFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        MeetingCommand meetingCommand = new MeetingCommand(outOfBoundIndex, meeting);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                meetingCommand.execute(model));
    }

    @Test
    public void execute_validMeeting_success() throws Exception {
        Model actualModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index index = Index.fromOneBased(1);

        List<Person> lastShownList = expectedModel.getFilteredPersonList();
        Person personToEdit = lastShownList.get(index.getZeroBased());
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        Person updatedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getDetails(),
                personToEdit.getTags(),
                personToEdit.getIsFavourite(),
                meeting);
        expectedModel.setPerson(personToEdit, updatedPerson);

        MeetingCommand command = new MeetingCommand(index, meeting);
        CommandResult commandResult = command.execute(actualModel);

        assertEquals(String.format(MeetingCommand.MESSAGE_MEETING_ADDED, updatedPerson.getName(),
                "25 Mar 2030 2:30 pm"), commandResult.getFeedbackToUser());
        assertEquals(expectedModel, actualModel);
    }

    @Test
    public void execute_filteredList_preservesFilter_success() throws Exception {
        Model actualModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        showPersonAtIndex(actualModel, INDEX_FIRST_PERSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        Person personToEdit = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        Person updatedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getDetails(),
                personToEdit.getTags(),
                personToEdit.getIsFavourite(),
                meeting);
        expectedModel.setPerson(personToEdit, updatedPerson);

        MeetingCommand command = new MeetingCommand(INDEX_FIRST_PERSON, meeting);
        CommandResult commandResult = command.execute(actualModel);

        assertEquals(String.format(MeetingCommand.MESSAGE_MEETING_ADDED, updatedPerson.getName(),
                "25 Mar 2030 2:30 pm"), commandResult.getFeedbackToUser());
        assertEquals(expectedModel, actualModel);
    }

    @Test
    public void execute_clearMeeting_success() throws Exception {
        Model actualModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index index = Index.fromOneBased(1);

        List<Person> lastShownList = expectedModel.getFilteredPersonList();
        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person updatedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getDetails(),
                personToEdit.getTags(),
                personToEdit.getIsFavourite(),
                null);
        expectedModel.setPerson(personToEdit, updatedPerson);

        MeetingCommand command = MeetingCommand.clear(index);
        CommandResult commandResult = command.execute(actualModel);

        assertEquals(String.format(MeetingCommand.MESSAGE_MEETING_CLEARED, updatedPerson.getName()),
                commandResult.getFeedbackToUser());
        assertEquals(expectedModel, actualModel);
    }

    @Test
    public void execute_clearNoMeeting_returnsNoMeetingMessage() throws Exception {
        Model actualModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        int zeroBasedIndex = 0;
        while (expectedModel.getFilteredPersonList().get(zeroBasedIndex).hasMeeting()) {
            zeroBasedIndex++;
        }
        Index index = Index.fromZeroBased(zeroBasedIndex);
        Person personToEdit = expectedModel.getFilteredPersonList().get(index.getZeroBased());

        MeetingCommand command = MeetingCommand.clear(index);
        CommandResult commandResult = command.execute(actualModel);

        assertEquals(String.format(MeetingCommand.MESSAGE_NO_MEETINGS, personToEdit.getName()),
                commandResult.getFeedbackToUser());
        assertEquals(expectedModel, actualModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        MeetingCommand meetingCommand = new MeetingCommand(Index.fromOneBased(1), meeting);
        assertThrows(NullPointerException.class, () -> meetingCommand.execute(null));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        assertThrows(NullPointerException.class, () -> new MeetingCommand(null, meeting));
        assertThrows(NullPointerException.class, () -> new MeetingCommand(Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, () -> MeetingCommand.clear(null));
    }

    @Test
    public void getFormattedDateAndTime_returnsExpectedFormat() {
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        MeetingCommand meetingCommand = new MeetingCommand(Index.fromOneBased(1), meeting);

        assertEquals("25 Mar 2030 2:30 pm", meeting.getFormattedDateTime());
    }

    @Test
    public void modifiesAddressBook_returnsTrue() {
        Meeting meeting = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        assertTrue(new MeetingCommand(Index.fromOneBased(1), meeting).modifiesAddressBook());
    }

    @Test
    public void equals() {
        Meeting meeting1 = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        Meeting meeting2 = new Meeting(LocalDateTime.of(2030, 3, 25, 14, 30));
        Meeting meeting3 = new Meeting(LocalDateTime.of(2030, 3, 26, 15, 0));
        Meeting meeting4 = new Meeting(LocalDateTime.of(2030, 3, 26, 14, 30));
        Meeting meeting5 = new Meeting(LocalDateTime.of(2030, 3, 25, 15, 0));

        MeetingCommand first = new MeetingCommand(Index.fromOneBased(1), meeting1);
        MeetingCommand same = new MeetingCommand(Index.fromOneBased(1), meeting2);
        MeetingCommand different = new MeetingCommand(Index.fromOneBased(2), meeting3);
        MeetingCommand sameIndexDifferentDate = new MeetingCommand(Index.fromOneBased(1), meeting4);
        MeetingCommand sameIndexAndDateDifferentTime = new MeetingCommand(Index.fromOneBased(1), meeting5);
        MeetingCommand clearFirst = MeetingCommand.clear(Index.fromOneBased(1));
        MeetingCommand clearFirstCopy = MeetingCommand.clear(Index.fromOneBased(1));
        Object otherObject = new ArrayList<>();

        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(different));
        assertFalse(first.equals(sameIndexDifferentDate));
        assertFalse(first.equals(sameIndexAndDateDifferentTime));
        assertTrue(clearFirst.equals(clearFirstCopy));
        assertFalse(first.equals(clearFirst));
        assertFalse(first.equals(otherObject));
        assertFalse(first.equals(null));
    }
}
