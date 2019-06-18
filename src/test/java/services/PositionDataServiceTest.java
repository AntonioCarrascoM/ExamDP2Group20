
package services;

import java.util.GregorianCalendar;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.PositionData;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class PositionDataServiceTest extends AbstractTest {

	// System under test: PositionData ------------------------------------------------------

	// Tests ------------------------------------------------------------------
	// PLEASE READ
	// The Sentence coverage has been obtained with the tool EclEmma from Eclipse. 
	// Since having one @Test for every case is not optimal we divided the user cases in two cases. Positives and Negatives.

	@Autowired
	private PositionDataService	positionDataService;


	@Test
	public void PositionDataPositiveTest() {
		final Object testingData[][] = {
			//Total sentence coverage : Coverage 94.3% | Covered Instructions 100 | Missed Instructions 6 | Total Instructions 106

			{
				"rookie1", null, "curriculum1", "create", null
			},
			/*
			 * 
			 * Positive test: A rookie registers a new positionData
			 * Requisite tested: Functional requirement - 17.1. An actor who is authenticated as a rookie must be able
			 * to: Manage his or her curricula , which includes listing, showing, creating, updating, and deleting them.
			 * Data coverage : We created a new positionData with valid data.
			 * Exception expected: None. A rookie can edit his positionData.
			 */{
				"rookie1", null, "positionData1", "edit", null
			},

			/*
			 * Positive test: A rookie edit his positionData.
			 * Requisite tested: Functional requirement - 17.1. An actor who is authenticated as a rookie must be able
			 * to: Manage his or her curricula , which includes listing, showing, creating, updating, and deleting them.
			 * Data coverage : From 4 editable attributes we tried to edit 1 attribute (title) with valid data.
			 * Exception expected: None. A rookie can edit his positionDatas.
			 */

			{
				"rookie1", null, "positionData1", "delete", null
			},
		/*
		 * 
		 * Positive test: A rookie delete his positionData
		 * Requisite tested: Functional requirement - 17.1. An actor who is authenticated as a rookie must be able
		 * to: Manage his or her curricula , which includes listing, showing, creating, updating, and deleting them.
		 * Data coverage : We tried to delete a positionData.
		 * Exception expected: None. A rookie can delete his positionData.
		 */

		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	@Test
	public void PositionDataNegativeTest() {
		final Object testingData[][] = {
			//Total Sentence Coverage: Coverage 95.1% | Covered Instructions 116 | Missed Instructions 6 | Total Instructions 122
			{
				"rookie1", "", "curriculum1", "createNegative", ConstraintViolationException.class
			},
			/*
			 * Negative test: A rookie tries to create a positionData
			 * Requisite tested: Functional requirement - 17.1. An actor who is authenticated as a rookie must be able
			 * to: Manage his or her curricula , which includes listing, showing, creating, updating, and deleting them.
			 * Data coverage: We created a positionData with 3 out of 4 valid parameters.
			 * Exception expected: ConstraintViolationException. Title cannot be blank.
			 */

			{
				"rookie2", null, "positionData1", "edit", IllegalArgumentException.class
			},

			/*
			 * Negative test: A rookie tries to edit a positionData that not owns.
			 * Requisite tested: Functional requirement - 17.1. An actor who is authenticated as a rookie must be able
			 * to: Manage his or her curricula , which includes listing, showing, creating, updating, and deleting them.
			 * Data coverage : From 3 editable attributes we tried to edit 1 attribute (Description) with a user that is not the owner.
			 * Exception expected: IllegalArgumentException. A Rookie can not edit positionDatas from another rookie.
			 */
			{
				"rookie2", null, "positionData1", "delete", IllegalArgumentException.class
			},
		/*
		 * Negative test: A rookie tries to delete a positionData that not owns.
		 * Requisite tested: Functional requirement - 17.1. An actor who is authenticated as a rookie must be able
		 * to: Manage his or her curricula , which includes listing, showing, creating, updating, and deleting them.
		 * Data coverage : We tried to delete a positionData with a user that is not the owner.
		 * Exception expected: IllegalArgumentException. A Rookie can not delete positionDatas from another rookie.
		 */
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	protected void template(final String username, final String st, final String id, final String operation, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			super.authenticate(username);

			if (operation.equals("edit")) {
				final PositionData positionData = this.positionDataService.findOne(this.getEntityId(id));
				positionData.setDescription("Description");

				this.positionDataService.save(positionData);

			} else if (operation.equals("create")) {

				final PositionData positionData = this.positionDataService.create(this.getEntityId(id));

				positionData.setTitle("title");
				positionData.setDescription("Description");

				positionData.setStartDate(new GregorianCalendar(2018, 11, 02).getTime());
				positionData.setEndDate(new GregorianCalendar(2021, 11, 02).getTime());

				this.positionDataService.save(positionData);

			} else if (operation.equals("delete")) {
				final PositionData positionData = this.positionDataService.findOne(this.getEntityId(id));
				this.positionDataService.delete(positionData);

			} else if (operation.equals("createNegative")) {
				final PositionData positionData = this.positionDataService.create(this.getEntityId(id));

				positionData.setTitle(st);
				positionData.setDescription("Description");
				positionData.setStartDate(new GregorianCalendar(2018, 11, 02).getTime());
				positionData.setEndDate(new GregorianCalendar(2021, 11, 02).getTime());

				this.positionDataService.save(positionData);
			}
			this.positionDataService.flush();
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.checkExceptions(expected, caught);
	}
}
