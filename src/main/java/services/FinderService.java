
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.FinderRepository;
import security.Authority;
import domain.Actor;
import domain.Finder;
import domain.Position;
import domain.Rookie;

@Service
@Transactional
public class FinderService {

	//Managed service

	@Autowired
	private FinderRepository		finderRepository;

	//Supporting service

	@Autowired
	private ActorService			actorService;

	@Autowired
	private RookieService			rookieService;

	@Autowired
	private PositionService			positionService;

	@Autowired
	private ConfigurationService	configurationService;


	//Simple CRUD methods --------------------------------

	public Finder create() {
		final Finder f = new Finder();
		f.setPositions(new ArrayList<Position>());
		return f;
	}

	public Finder findOne(final int id) {
		Assert.notNull(id);
		return this.finderRepository.findOne(id);
	}

	public Collection<Finder> findAll() {
		return this.finderRepository.findAll();
	}

	public Finder save(final Finder f) {
		Assert.notNull(f);
		//Assertion that the user modifying this finder has the correct privilege.
		Assert.isTrue(f.getId() == this.findPrincipalFinder().getId());//this.findPrincipalFinder().getId()
		//If all fields of the finder are null, the finder returns the entire listing of available tasks.
		f.setPositions(f.getPositions());
		f.setMoment(new Date(System.currentTimeMillis() - 1));
		final Finder saved = this.finderRepository.save(f);
		final Rookie h = this.rookieService.rookieByFinder(f.getId());
		h.setFinder(f);
		this.rookieService.save(h);

		return saved;
	}

	public void delete(final Finder f) {
		Assert.notNull(f);

		//Assertion that the user deleting this finder has the correct privilege.
		Assert.isTrue(this.actorService.findByPrincipal().getId() == this.rookieService.rookieByFinder(f.getId()).getId());

		this.finderRepository.delete(f);
	}

	public Finder findPrincipalFinder() {
		final Actor a = this.actorService.findByPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ROOKIE);
		Assert.isTrue(a.getUserAccount().getAuthorities().contains(auth));

		final Rookie h = (Rookie) this.actorService.findOne(a.getId());
		Finder fd = new Finder();
		if (h.getFinder() == null) {
			fd = this.create();
			fd.setPositions(this.find(fd));
			final Finder saved = this.finderRepository.save(fd);
			h.setFinder(saved);
			this.rookieService.save(h);
			return saved;
		} else
			return h.getFinder();
	}

	public Collection<Position> find(final Finder finder) {
		Assert.notNull(finder);

		Collection<Position> positions = new ArrayList<>();

		String keyWord = finder.getKeyWord();
		Date specificDeadline = finder.getSpecificDeadline();
		Double minSalary = finder.getMinSalary(), maxSalary = finder.getMaxSalary();

		if (keyWord == null && specificDeadline == null && minSalary == null && maxSalary == null)
			positions = this.positionService.getPublicPositions();
		else {

			if (keyWord == null)
				keyWord = "";
			if (specificDeadline == null)
				specificDeadline = new Date(2524694400000L);
			if (minSalary == null)
				minSalary = 0.;
			if (maxSalary == null)
				maxSalary = 99999999.;

			positions = this.finderRepository.findPosition(keyWord, specificDeadline, minSalary, maxSalary);
		}

		return positions;
	}

	public Collection<Position> limitResults(final Collection<Position> positions) {
		Collection<Position> results = new ArrayList<>();
		final int maxResults = this.configurationService.findAll().iterator().next().getMaxFinderResults();
		if (positions.size() > maxResults)
			results = new ArrayList<Position>(((ArrayList<Position>) positions).subList(0, maxResults));
		else
			results = positions;
		return results;
	}

	//Returns a certain Rookie given his finder id
	public Rookie getRookieByFinder(final int id) {
		return this.finderRepository.getRookieByFinder(id);
	}

	//The minimum, the maximum, the average, and the standard deviation of the number of results in the finders.
	public Double[] minMaxAvgStddevResultsFinders() {
		return this.finderRepository.minMaxAvgAndStddevOfResultsByFinder();
	}

	//  The ratio of empty versus non-empty finders
	public Double ratioEmptyVersusNonEmptyFinders() {
		Double res = this.finderRepository.ratioEmptyVsNonEmptyFinders();
		if (res == null)
			res = 0.0;
		return res;
	}

	public void flush() {
		this.finderRepository.flush();
	}
}
