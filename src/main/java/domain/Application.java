
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "rookie, status")
})
public class Application extends DomainEntity {

	//Attributes

	private Date		moment;
	private String		answerDescription;
	private String		answerLink;
	private Date		answerMoment;
	private Status		status;

	//Relationships

	private Curriculum	curriculum;
	private Problem		problem;
	private Position	position;
	private Rookie		rookie;


	//Getters

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getMoment() {
		return this.moment;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Problem getProblem() {
		return this.problem;
	}

	public String getAnswerDescription() {
		return this.answerDescription;
	}

	@URL
	public String getAnswerLink() {
		return this.answerLink;
	}

	@Past
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getAnswerMoment() {
		return this.answerMoment;
	}

	@NotNull
	@Valid
	public Status getStatus() {
		return this.status;
	}

	@Valid
	@NotNull
	@OneToOne(optional = false)
	public Curriculum getCurriculum() {
		return this.curriculum;
	}

	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public Position getPosition() {
		return this.position;
	}

	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public Rookie getRookie() {
		return this.rookie;
	}

	//Setters
	public void setMoment(final Date moment) {
		this.moment = moment;
	}

	public void setProblem(final Problem problem) {
		this.problem = problem;
	}

	public void setAnswerDescription(final String answerDescription) {
		this.answerDescription = answerDescription;
	}

	public void setAnswerLink(final String answerLink) {
		this.answerLink = answerLink;
	}

	public void setAnswerMoment(final Date answerMoment) {
		this.answerMoment = answerMoment;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public void setPosition(final Position position) {
		this.position = position;
	}

	public void setRookie(final Rookie rookie) {
		this.rookie = rookie;
	}

	public void setCurriculum(final Curriculum curriculum) {
		this.curriculum = curriculum;
	}
}
