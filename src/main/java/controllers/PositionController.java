
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AuditService;
import services.PositionService;
import services.SponsorshipService;
import domain.Audit;
import domain.Position;
import domain.Sponsorship;

@Controller
@RequestMapping("position")
public class PositionController extends AbstractController {

	//Services

	@Autowired
	private PositionService		positionService;

	@Autowired
	private AuditService		auditService;

	@Autowired
	private SponsorshipService	sponsorshipService;


	//Listing all

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) final String keyword) {
		final ModelAndView result;
		final Collection<Position> positions;

		if (keyword == null || keyword.isEmpty())
			positions = this.positionService.getPublicPositions();
		else
			positions = this.positionService.searchPosition(keyword);

		result = new ModelAndView("position/list");
		result.addObject("positions", positions);
		result.addObject("requestURI", "position/list.do");
		result.addObject("keyword", keyword);

		return result;
	}

	//Listing by company

	@RequestMapping(value = "/listByCompany", method = RequestMethod.GET)
	public ModelAndView listByCompany(@RequestParam final int varId) {
		final ModelAndView result;
		final Collection<Position> positions;

		positions = this.positionService.getPublicPositionsForCompany(varId);

		result = new ModelAndView("position/list");
		result.addObject("positions", positions);
		result.addObject("requestURI", "position/list.do");

		return result;
	}

	//Display

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int varId) {
		final ModelAndView result;
		Sponsorship sponsorship;
		Collection<Audit> auditColl;

		sponsorship = this.positionService.selectRandomSponsorship(varId);

		if (sponsorship != null)
			this.sponsorshipService.saveFromPosition(sponsorship);

		final Position position = this.positionService.findOne(varId);
		Assert.notNull(position);

		auditColl = this.auditService.getAuditsForPosition(varId);

		result = new ModelAndView("position/display");
		result.addObject("position", position);
		result.addObject("auditColl", auditColl);
		result.addObject("sponsorship", sponsorship);
		result.addObject("requestURI", "position/display.do");

		return result;
	}

}
