package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

	private UserService service;

	@BeforeEach
	void setUp() {
		service = new UserService();
	}

	@Test
	@DisplayName("createUser creates user successfully")
	void testCreateUserCreatesSuccessfully() {
		service.createUser("John", "12345678", "password");
		assertEquals(1, service.getUserCount());
	}

	@Test
	@DisplayName("createUser generates unique user IDs")
	void testCreateUserGeneratesUniqueIds() {
		service.createUser("John", "12345678", "password");
		service.createUser("Jane", "87654321", "password2");
		assertEquals(2, service.getUserCount());
		Map<String, User> users = service.getUserMap();
		assertNotEquals(users.keySet().iterator().next(), 
			users.keySet().toArray()[1]);
	}

	@Test
	@DisplayName("createUser throws for null name")
	void testCreateUserThrowsForNullName() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createUser(null, "12345678", "password"));
	}

	@Test
	@DisplayName("createUser throws for empty name")
	void testCreateUserThrowsForEmptyName() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createUser("", "12345678", "password"));
	}

	@Test
	@DisplayName("createUser throws for null phoneNum")
	void testCreateUserThrowsForNullPhoneNum() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createUser("John", null, "password"));
	}

	@Test
	@DisplayName("createUser throws for empty phoneNum")
	void testCreateUserThrowsForEmptyPhoneNum() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createUser("John", "", "password"));
	}

	@Test
	@DisplayName("createUser throws for null password")
	void testCreateUserThrowsForNullPassword() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createUser("John", "12345678", null));
	}

	@Test
	@DisplayName("createUser throws for empty password")
	void testCreateUserThrowsForEmptyPassword() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createUser("John", "12345678", ""));
	}

	@Test
	@DisplayName("getUser returns existing user")
	void testGetUserReturnsExisting() {
		service.createUser("John", "12345678", "password");
		User user = service.getUserMap().values().iterator().next();
		User retrieved = service.getUser(user.getUserId());
		assertSame(user, retrieved);
	}

	@Test
	@DisplayName("getUser returns null for nonexistent user")
	void testGetUserReturnsNullForNonexistent() {
		assertNull(service.getUser("nonexistent"));
	}

	@Test
	@DisplayName("findUserByName finds existing user")
	void testFindUserByNameFindsExisting() {
		service.createUser("John", "12345678", "password");
		User found = service.findUserByName("John");
		assertNotNull(found);
		assertEquals("John", found.getName());
	}

	@Test
	@DisplayName("findUserByName returns null for nonexistent name")
	void testFindUserByNameReturnsNullForNonexistent() {
		service.createUser("John", "12345678", "password");
		assertNull(service.findUserByName("Jane"));
	}

	@Test
	@DisplayName("getUserCount returns correct count")
	void testGetUserCountReturnsCorrectCount() {
		assertEquals(0, service.getUserCount());
		service.createUser("John", "12345678", "password");
		assertEquals(1, service.getUserCount());
		service.createUser("Jane", "87654321", "password2");
		assertEquals(2, service.getUserCount());
	}

	@Test
	@DisplayName("getUserMap returns a copy")
	void testGetUserMapReturnsCopy() {
		service.createUser("John", "12345678", "password");
		Map<String, User> copy = service.getUserMap();
		copy.clear();
		assertEquals(1, service.getUserMap().size());
	}

	@Test
	@DisplayName("Created user has initialized wallet")
	void testCreatedUserHasInitializedWallet() {
		service.createUser("John", "12345678", "password");
		User user = service.getUserMap().values().iterator().next();
		assertNotNull(user.getWallet());
		assertEquals(0.0, user.getWalletBalance());
	}

	@Test
	@DisplayName("registerUser stores existing user")
	void testRegisterUserStoresUser() {
		User user = new User("Pat", "11111111", "pw");
		service.registerUser(user);
		assertSame(user, service.getUser(user.getUserId()));
	}

	@Test
	@DisplayName("registerUser throws for null user")
	void testRegisterUserThrowsForNull() {
		assertThrows(IllegalArgumentException.class, () -> service.registerUser(null));
	}
}
