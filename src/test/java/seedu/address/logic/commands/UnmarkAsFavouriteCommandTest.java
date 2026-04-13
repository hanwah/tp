package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests for {@code UnmarkAsFavouriteCommand}.
 */
public class UnmarkAsFavouriteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Tests that executing the command with an out-of-range index fails.
     */
    @Test
    public void execute_indexOutOfRange() {
        Index index = Index.fromOneBased(model.getAddressBook().getPersonList().size() + 10);
        UnmarkAsFavouriteCommand unmarkAsFavouriteCommand = new UnmarkAsFavouriteCommand(index);
        assertCommandFailure(unmarkAsFavouriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Tests that executing the command on a person who is not marked as favourite fails.
     */
    @Test
    public void execute_personNotInFavourites_throwsCommandException() throws CommandException {
        Index index = Index.fromOneBased(2);

        UnmarkAsFavouriteCommand command = new UnmarkAsFavouriteCommand(index);

        List<Person> lastShownList = model.getFilteredPersonList();
        Person personToEdit = lastShownList.get(index.getZeroBased());

        assertCommandFailure(command, model,
                String.format(UnmarkAsFavouriteCommand.MESSAGE_UNMARK_PERSON_DUPLICATE,
                        personToEdit.getName()));
    }

    /**
     * Tests that executing the command with a valid index successfully removes the favourite status.
     */
    @Test
    public void execute_validUnmarkAsFavourites() throws Exception {
        Model testModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index index = Index.fromOneBased(1);

        //Add Index to Favourites
        new MarkAsFavouriteCommand(index).execute(testModel);

        List<Person> lastShownList = testModel.getFilteredPersonList();
        Person personToEdit = lastShownList.get(index.getZeroBased());

        UnmarkAsFavouriteCommand command = new UnmarkAsFavouriteCommand(index);


        assertCommandSuccess(command, testModel,
                String.format(UnmarkAsFavouriteCommand.MESSAGE_UNMARK_PERSON_SUCCESS, personToEdit.getName()),
                testModel);
    }

    @Test
    public void execute_filteredList_preservesFilter_success() throws Exception {
        Model actualModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        new MarkAsFavouriteCommand(INDEX_FIRST_PERSON).execute(actualModel);
        new MarkAsFavouriteCommand(INDEX_FIRST_PERSON).execute(expectedModel);
        showPersonAtIndex(actualModel, INDEX_FIRST_PERSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        Person personToEdit = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person unmarkedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getDetails(),
                personToEdit.getTags(),
                false,
                personToEdit.getMeeting().orElse(null));
        expectedModel.setPerson(personToEdit, unmarkedPerson);

        UnmarkAsFavouriteCommand command = new UnmarkAsFavouriteCommand(INDEX_FIRST_PERSON);
        assertCommandSuccess(command, actualModel,
                String.format(UnmarkAsFavouriteCommand.MESSAGE_UNMARK_PERSON_SUCCESS, personToEdit.getName()),
                expectedModel);
    }

    /**
     * Testing of equals method overwritten in UnmarkAsFavouriteCommand
     */
    @Test
    public void execute_equals() {
        UnmarkAsFavouriteCommand sameOne = new UnmarkAsFavouriteCommand(Index.fromOneBased(1));
        UnmarkAsFavouriteCommand sameTwo = new UnmarkAsFavouriteCommand(Index.fromOneBased(1));
        UnmarkAsFavouriteCommand different = new UnmarkAsFavouriteCommand(Index.fromOneBased(2));
        Object otherObject = new ArrayList<>();

        assertTrue(sameOne.equals(sameOne));
        assertTrue(sameOne.equals(sameTwo));
        assertFalse(sameOne.equals(different));
        assertFalse(sameOne.equals(otherObject));
    }
}
