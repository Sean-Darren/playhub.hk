package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ClassTest {

	private Class testClass;

	@BeforeEach
	void setUp() {
		testClass = new Class();
	}

	@Test
	@DisplayName("Default constructor creates instance")
	void testDefaultConstructorCreatesInstance() {
		assertNotNull(testClass);
	}

	@Test
	@DisplayName("Parameterized constructor sets all fields")
	void testParameterizedConstructorSetsAllFields() {
		Class c = new Class("class1", "Basketball 101", "Basketball");
		assertEquals("class1", c.getClassId());
		assertEquals("Basketball 101", c.getName());
		assertEquals("Basketball", c.getSportType());
	}

	@Test
	@DisplayName("setClassId and getClassId work correctly")
	void testSetClassIdAndGetClassId() {
		testClass.setClassId("newId");
		assertEquals("newId", testClass.getClassId());
	}

	@Test
	@DisplayName("setName and getName work correctly")
	void testSetNameAndGetName() {
		testClass.setName("New Class Name");
		assertEquals("New Class Name", testClass.getName());
	}

	@Test
	@DisplayName("setSportType and getSportType work correctly")
	void testSetSportTypeAndGetSportType() {
		testClass.setSportType("Football");
		assertEquals("Football", testClass.getSportType());
	}

	@Test
	@DisplayName("setAssociatedSession and getAssociatedSession work")
	void testSetAssociatedSessionAndGetAssociatedSession() {
		Community community = new Community("Test", "Basketball", "Desc", new java.util.Date());
		Booking booking = new Booking(java.time.LocalDateTime.now().plusDays(1));
		Session session = new Session(booking, community, 10, 50.0);
		testClass.setAssociatedSession(session);
		assertSame(session, testClass.getAssociatedSession());
	}

	@Test
	@DisplayName("Associated session is null by default")
	void testAssociatedSessionIsNullByDefault() {
		assertNull(testClass.getAssociatedSession());
	}
}
