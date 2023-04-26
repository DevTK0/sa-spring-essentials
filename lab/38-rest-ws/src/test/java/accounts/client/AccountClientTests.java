package accounts.client;

import common.money.Percentage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import accounts.RestWsApplication;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.net.URI;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RestWsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccountClientTests {

	private static final String BASE_URL = "http://localhost:8080";

	private RestTemplate restTemplate = new RestTemplate();
	private Random random = new Random();

	@Test
	public void listAccounts() {
		Account[] accounts = restTemplate.getForObject(BASE_URL + "/accounts", Account[].class); // Modify this line to
																									// use the
																									// restTemplate

		assertNotNull(accounts);
		assertTrue(accounts.length >= 21);
		assertEquals("Keith and Keri Donald", accounts[0].getName());
		assertEquals(2, accounts[0].getBeneficiaries().size());
		assertEquals(Percentage.valueOf("50%"), accounts[0].getBeneficiary("Annabelle").getAllocationPercentage());
	}

	@Test
	public void getAccount() {
		Account account = restTemplate.getForObject(BASE_URL + "/accounts/0", Account.class); // Modify this line to
																								// use the
																								// restTemplate

		assertNotNull(account);
		assertEquals("Keith and Keri Donald", account.getName());
		assertEquals(2, account.getBeneficiaries().size());
		assertEquals(Percentage.valueOf("50%"), account.getBeneficiary("Annabelle").getAllocationPercentage());
	}

	@Test
	public void createAccount() {
		// Use a unique number to avoid conflicts
		String number = String.format("12345%4d", random.nextInt(10000));
		Account account = new Account(number, "John Doe");
		account.addBeneficiary("Jane Doe");

		URI newAccountLocation = restTemplate.postForLocation(BASE_URL + "/accounts", account);

		Account retrievedAccount = restTemplate.getForObject(newAccountLocation, Account.class);

		assertEquals(account.getNumber(), retrievedAccount.getNumber());

		Beneficiary accountBeneficiary = account.getBeneficiaries().iterator().next();
		Beneficiary retrievedAccountBeneficiary = retrievedAccount.getBeneficiaries().iterator().next();

		assertEquals(accountBeneficiary.getName(), retrievedAccountBeneficiary.getName());
		assertNotNull(retrievedAccount.getEntityId());
	}

	@Test
	public void createSameAccountTwiceResultsIn409() {
		Account account = new Account("123123123", "John Doe");
		account.addBeneficiary("Jane Doe");

		HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.postForLocation(BASE_URL + "/accounts", account);
			restTemplate.postForLocation(BASE_URL + "/accounts", account);
		});
		assertEquals(HttpStatus.CONFLICT, httpClientErrorException.getStatusCode());

	}

	@Test
	public void addAndDeleteBeneficiary() {
		// perform both add and delete to avoid issues with side effects

		URI location = restTemplate.postForLocation(BASE_URL + "/accounts/1/beneficiaries", "David");

		Beneficiary newBeneficiary = restTemplate.getForObject(location, Beneficiary.class);

		assertNotNull(newBeneficiary);
		assertEquals("David", newBeneficiary.getName());

		restTemplate.delete(BASE_URL + "/accounts/1/beneficiaries/David");

		HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
			System.out.println("You SHOULD get the exception \"No such beneficiary with name 'David'\" in the server.");

			restTemplate.getForObject(location, Beneficiary.class);

		});
		assertEquals(HttpStatus.NOT_FOUND, httpClientErrorException.getStatusCode());
	}

}
