
package services;

import java.net.URL;
import java.util.Collection;
import java.util.Random;

import javax.validation.ConstraintDefinitionException;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ProblemRepository;
import security.Authority;
import domain.Company;
import domain.Position;
import domain.Problem;

@Service
@Transactional
public class ProblemService {

	//Managed repository

	@Autowired
	private ProblemRepository	problemRepository;

	//Supporting services

	@Autowired
	private ActorService		actorService;

	@Autowired
	private PositionService		positionService;

	@Autowired
	private Validator			validator;


	//Simple CRUD methods

	public Problem create() {
		final Problem p = new Problem();

		p.setCompany((Company) this.actorService.findByPrincipal());
		p.setFinalMode(false);

		return p;
	}
	public Problem findOne(final int id) {
		Assert.notNull(id);

		return this.problemRepository.findOne(id);
	}

	public Collection<Problem> findAll() {
		return this.problemRepository.findAll();
	}

	public Problem save(final Problem p) {
		Assert.notNull(p);

		//Assertion that the user modifying this task has the correct privilege.
		Assert.isTrue(this.actorService.findByPrincipal().getId() == p.getCompany().getId());

		final Problem saved = this.problemRepository.save(p);

		return saved;
	}

	public void delete(final Problem p) {
		Assert.notNull(p);

		//Assertion that the user deleting this task has the correct privilege.
		Assert.isTrue(this.actorService.findByPrincipal().getId() == p.getCompany().getId());

		//Assertion to make sure that the entity is not final
		Assert.isTrue(p.getFinalMode() == false);

		final Collection<Position> positions = this.positionService.getPositionsOfAProblem(p.getId());

		if (!positions.isEmpty())
			for (final Position pos : positions) {
				final Collection<Problem> problems = pos.getProblems();
				problems.remove(p);
				pos.setProblems(problems);
				this.positionService.saveFromProblem(pos);
			}

		this.problemRepository.delete(p);
	}

	//Reconstruct

	public Problem reconstruct(final Problem p, final BindingResult binding) {
		Assert.notNull(p);
		Problem result;
		final Authority authCompany = new Authority();
		authCompany.setAuthority(Authority.COMPANY);

		if (p.getId() == 0)
			result = this.create();
		else
			result = this.problemRepository.findOne(p.getId());

		//Assertion that the user modifying this task has the correct privilege.
		Assert.isTrue(result.getFinalMode() == false);

		result.setTitle(p.getTitle());
		result.setStatement(p.getStatement());
		result.setHint(p.getHint());
		result.setAttachments(p.getAttachments());
		result.setFinalMode(p.getFinalMode());

		this.validator.validate(result, binding);

		if (binding.hasErrors())
			throw new ValidationException();

		//Assertion to make sure that the attachments are URLs
		if (result.getAttachments() != null && !result.getAttachments().isEmpty())
			if (this.checkPictures(result.getAttachments()) == false)
				throw new ConstraintDefinitionException();

		//Assertion that the user modifying this task has the correct privilege.
		Assert.isTrue(this.actorService.findByPrincipal().getId() == result.getCompany().getId());

		return result;

	}

	//CheckPictures method
	public boolean checkPictures(final String pictures) {
		boolean result = true;
		if (pictures != null)
			if (!pictures.isEmpty()) {
				final String[] splited = pictures.split(";");
				for (final String s : splited)
					if (!this.isURL(s))
						result = false;
			}
		return result;
	}
	public boolean isURL(final String url) {
		try {
			new URL(url);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	//Other methods

	//Retrieves the problems for a certain company
	public Collection<Problem> problemsOfACompany(final int id) {
		return this.problemRepository.problemsOfACompany(id);
	}

	//Retrieves the final problems for a certain company
	public Collection<Problem> finalProblemsOfACompany(final int id) {
		return this.problemRepository.finalProblemsOfACompany(id);
	}

	//Retrieves the problems for a certain problem
	public Collection<Problem> problemsOfAPosition(final int id) {
		return this.problemRepository.problemsOfAPosition(id);
	}

	public void flush() {
		this.problemRepository.flush();
	}

	//Problems in final mode by position
	public Collection<Problem> problemsInFinalModeByPosition(final int id) {
		return this.problemRepository.problemsInFinalModeByPosition(id);
	}

	//Random problem in final mode by position
	public Problem randomProblemInFinalModeByPosition(final int id) {
		final Collection<Problem> problems = this.problemRepository.problemsInFinalModeByPosition(id);
		if (problems.isEmpty())
			return null;
		else {
			final Random rnd = new Random();
			final int i = rnd.nextInt(problems.size());
			return (Problem) problems.toArray()[i];
		}
	}
}
