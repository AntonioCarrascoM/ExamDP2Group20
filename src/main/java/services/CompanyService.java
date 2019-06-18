
package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import javax.validation.ConstraintDefinitionException;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CompanyRepository;
import security.Authority;
import security.UserAccount;
import domain.Audit;
import domain.Company;
import domain.Position;
import forms.FormObjectCompany;

@Service
@Transactional
public class CompanyService {

	//Managed repository ---------------------------------

	@Autowired
	private CompanyRepository	companyRepository;

	//Supporting services --------------------------------

	@Autowired
	private ActorService		actorService;

	@Autowired
	private PositionService		positionService;

	@Autowired
	private AuditService		auditService;

	@Autowired
	private Validator			validator;


	//Simple CRUD Methods --------------------------------

	public Company create() {
		final Authority a = new Authority();
		a.setAuthority(Authority.COMPANY);
		final UserAccount account = new UserAccount();
		account.setAuthorities(Arrays.asList(a));
		account.setBanned(false);
		account.setInactive(false);

		final Company company = new Company();
		company.setSpammer(false);
		company.setEvaluated(false);
		company.setUserAccount(account);

		return company;
	}

	public Collection<Company> findAll() {
		return this.companyRepository.findAll();
	}

	public Company findOne(final int id) {
		Assert.notNull(id);

		return this.companyRepository.findOne(id);
	}

	public Company save(final Company company) {
		Assert.notNull(company);
		Company saved2;

		//Assertion that the email is valid according to the checkAdminEmail method.
		Assert.isTrue(this.actorService.checkUserEmail(company.getEmail()));

		//Assertion to check that the address isn't just a white space.
		Assert.isTrue(this.actorService.checkAddress(company.getAddress()));

		//Assertion that the phone is valid according to the checkPhone method.
		Assert.isTrue(this.actorService.checkPhone(company.getPhone()));

		//Checking if the actor is bannable according to the "bannableActors" query.
		if (this.actorService.isBannable(company) == true)
			company.setSpammer(true);

		if (company.getId() != 0) {
			Assert.isTrue(this.actorService.findByPrincipal().getId() == company.getId());
			saved2 = this.companyRepository.save(company);
		} else {
			final Company saved = this.companyRepository.save(company);
			this.actorService.hashPassword(saved);
			saved2 = this.companyRepository.save(saved);
		}

		return saved2;
	}

	public Company saveAdmin(final Company company) {

		final Authority a = new Authority();
		a.setAuthority(Authority.ADMIN);

		//Assertion that the phone is valid according to the checkPhone method.
		Assert.isTrue(this.actorService.findByPrincipal().getUserAccount().getAuthorities().contains(a));

		return this.companyRepository.save(company);
	}

	public void delete(final Company company) {
		Assert.notNull(company);

		//Assertion that the user deleting this company has the correct privilege.
		Assert.isTrue(this.actorService.findByPrincipal().getId() == company.getId());

		this.companyRepository.delete(company);
	}

	public Company reconstruct(final FormObjectCompany foc, final BindingResult binding) {
		final Company result = this.create();

		Assert.isTrue(foc.getAcceptedTerms());
		Assert.isTrue(foc.getPassword().equals(foc.getSecondPassword()));

		result.setName(foc.getName());
		result.setSurnames(foc.getSurnames());
		result.setVatNumber(foc.getVatNumber());
		result.setCreditCard(foc.getCreditCard());
		result.setPhoto(foc.getPhoto());
		result.setEmail(foc.getEmail());
		result.setPhone(foc.getPhone());
		result.setAddress(foc.getAddress());
		result.setCommercialName(foc.getCommercialName());
		result.getUserAccount().setUsername(foc.getUsername());
		result.getUserAccount().setPassword(foc.getPassword());

		this.validator.validate(result, binding);

		if (binding.hasErrors())
			throw new ValidationException();

		final int year = Calendar.getInstance().get(Calendar.YEAR);
		final int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

		//Assertion to make sure that the credit card has a valid expiration date.
		if (result.getCreditCard() != null) {
			if (result.getCreditCard().getExpYear() < year)
				throw new ConstraintDefinitionException();
			if (result.getCreditCard().getExpYear() == year && result.getCreditCard().getExpMonth() < month)
				throw new ConstraintDefinitionException();
		}

		//Assertion that the email is valid according to the checkAdminEmail method.
		Assert.isTrue(this.actorService.checkUserEmail(result.getEmail()));

		//Assertion to check that the address isn't just a white space.
		Assert.isTrue(this.actorService.checkAddress(result.getAddress()));

		//Assertion that the phone is valid according to the checkPhone method.
		Assert.isTrue(this.actorService.checkPhone(result.getPhone()));

		return result;

	}

	public Company reconstructPruned(final Company company, final BindingResult binding) {
		Company result;

		result = this.companyRepository.findOne(company.getId());

		result.setName(company.getName());
		result.setSurnames(company.getSurnames());
		result.setVatNumber(company.getVatNumber());
		result.setCreditCard(company.getCreditCard());
		result.setPhoto(company.getPhoto());
		result.setEmail(company.getEmail());
		result.setPhone(company.getPhone());
		result.setAddress(company.getAddress());
		result.setCommercialName(company.getCommercialName());

		this.validator.validate(result, binding);

		if (binding.hasErrors())
			throw new ValidationException();

		final int year = Calendar.getInstance().get(Calendar.YEAR);
		final int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

		//Assertion to make sure that the credit card has a valid expiration date.
		if (result.getCreditCard() != null) {
			if (result.getCreditCard().getExpYear() < year)
				throw new ConstraintDefinitionException();
			if (result.getCreditCard().getExpYear() == year && result.getCreditCard().getExpMonth() < month)
				throw new ConstraintDefinitionException();
		}

		//Assertion the user has the correct privilege
		Assert.isTrue(this.actorService.findByPrincipal().getId() == result.getId());

		//Assertion that the email is valid according to the checkAdminEmail method.
		Assert.isTrue(this.actorService.checkUserEmail(result.getEmail()));

		//Assertion to check that the address isn't just a white space.
		Assert.isTrue(this.actorService.checkAddress(result.getAddress()));

		//Assertion that the phone is valid according to the checkPhone method.
		Assert.isTrue(this.actorService.checkPhone(result.getPhone()));

		return result;

	}

	//Other methods

	public void flush() {
		this.companyRepository.flush();
	}
	//Compute score for all
	public void computeScoreForAll() {
		final Collection<Company> companies = this.findAll();
		if (!companies.isEmpty())
			for (final Company c : companies)
				this.computeScore(c);
	}
	//Compute score
	public void computeScore(final Company company) {
		final Collection<Position> positions = this.positionService.getPublicPositionsForCompany(company.getId());
		Double score = 0.0;
		for (final Position p : positions)
			if (!this.auditService.getAuditsForPosition(p.getId()).isEmpty())
				score = score + this.createScore(this.auditService.getAuditsForPosition(p.getId()));

		company.setAuditScore(score);
		this.saveAdmin(company);
	}

	private Double createScore(final Collection<Audit> audits) {
		Double score = 0.0;
		final Integer tam = audits.size();
		for (final Audit a : audits)
			score = score + a.getScore();
		return (score / tam) / 10;
	}

	//The companies that have offered more positions
	public Collection<String> companiesWithMoreOfferedPossitions() {
		Collection<String> results = new ArrayList<>();
		final Collection<String> companies = this.companyRepository.companiesWithMoreOfferedPossitions();
		final int maxResults = 2;
		if (companies.size() > maxResults)
			results = new ArrayList<String>(((ArrayList<String>) companies).subList(0, maxResults));
		else
			results = companies;
		return results;
	}

	//The average, the minimum, the maximum, and the standard deviation of the audit score of the companies that are registered in the system
	public Double[] avgMinMaxStddevAuditScorePerCompany() {
		return this.companyRepository.avgMinMaxStddevAuditScorePerCompany();
	}

	//The companies with the highest audit score.
	public Collection<String> companiesWithHighAuditScore() {
		Collection<String> results = new ArrayList<>();
		final Collection<String> companies = this.companyRepository.companiesWithHighAuditScore();
		final int maxResults = 2;
		if (companies.size() > maxResults)
			results = new ArrayList<String>(((ArrayList<String>) companies).subList(0, maxResults));
		else
			results = companies;
		return results;
	}
}
