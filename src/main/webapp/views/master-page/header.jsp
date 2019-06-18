<%--
 * header.jsp
 *
 * Copyright (C) 2019 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<spring:message code="master.page.cancel.confirm" var="msgConfirm" />

<div>
	<a href="#"><img src="images/logo.png" alt="Acme Rookies Co., Inc." /></a>
</div>

<div>
	<ul id="jMenu">
		<!-- Do not forget the "fNiv" class for the first level links !! -->
		
		<security:authorize access="isAuthenticated()">
			<li>
				<a class="fNiv"> 
					<spring:message code="master.page.profile" /> 
			        (<security:authentication property="principal.username" />)
				</a>
				<ul>
					<li class="arrow"></li>
					
					<security:authorize access="hasRole('COMPANY')">
						<li><a href="company/edit.do"><spring:message
									code="master.page.actor.edit" /></a></li>
					</security:authorize>
					
					<security:authorize access="hasRole('PROVIDER')">
						<li><a href="provider/edit.do"><spring:message
									code="master.page.actor.edit" /></a></li>
					</security:authorize>
					
					<security:authorize access="hasRole('AUDITOR')">
						<li><a href="auditor/edit.do"><spring:message
									code="master.page.actor.edit" /></a></li>
					</security:authorize>

					<security:authorize access="hasRole('ROOKIE')">
						<li><a href="rookie/edit.do"><spring:message
									code="master.page.actor.edit" /></a></li>
					</security:authorize>
					
					<security:authorize access="hasRole('ADMIN')">
						<li><a href="administrator/edit.do"><spring:message
									code="master.page.actor.edit" /></a></li>
					</security:authorize>
					
					<li><a href="message/list.do"><spring:message
								code="master.page.message.list" /> </a></li>
					
					<li><a href="socialProfile/list.do"><spring:message
								code="master.page.socialProfile" /> </a></li>
								
					<li><a href="actor/deactivate.do" onclick="return confirm('${msgConfirm}')"><spring:message
								code="master.page.deactivate" /> </a></li>
								
					<li><a href="download/myPersonalData.do"><spring:message
								code="master.page.export" /> </a></li>
							
					<li><a href="j_spring_security_logout"><spring:message code="master.page.logout" /> </a></li>
				</ul>
			</li>
		</security:authorize>
		
		
		<security:authorize access="hasRole('ADMIN')">
			<li><a class="fNiv"> <spring:message
						code="master.page.administrator" />
			</a>
				<ul>
					<li class="arrow"></li>
					<li><a href="administrator/dashboard.do"><spring:message
								code="master.page.administrator.dashboard" /></a></li>
					<li><a href="configuration/administrator/display.do"><spring:message
								code="master.page.administrator.configuration" /></a></li>
					<li><a href="administrator/flagSpam.do"><spring:message
								code="master.page.administrator.flagSpam" /></a></li>
					<li><a href="administrator/computeScore.do"><spring:message
								code="master.page.administrator.computeScore" /></a></li>
					<li><a href="administrator/bannableList.do"><spring:message
								code="master.page.administrator.bannableList" /></a></li>
				</ul></li>
				
				<li><a class="fNiv"><spring:message
						code="master.page.administrator.createAcc" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="administrator/create.do"><spring:message
								code="master.page.administrator.create" /></a></li>
					<li><a href="administrator/createAuditor.do"><spring:message
								code="master.page.administrator.auditor" /></a></li>
				</ul></li>
		</security:authorize>
		
		<security:authorize access="hasRole('COMPANY')">
			<li><a class="fNiv"> <spring:message
						code="master.page.company" />
			</a>
				<ul>
					<li class="arrow"></li>
					<li><a href="position/company/list.do"><spring:message
								code="master.page.company.position.list" /></a></li>
					<li><a href="problem/company/list.do"><spring:message
								code="master.page.company.problem.list" /></a></li>
				</ul>
			</li>
		</security:authorize>
		<security:authorize access="hasRole('ROOKIE')">
			<li><a href="curriculum/rookie/list.do"><spring:message
					code="master.page.curriculum.list" /></a></li>

			<li><a href="finder/rookie/edit.do"><spring:message
					code="master.page.finder.edit" /></a></li>
					
			<li><a href="application/rookie/list.do"><spring:message
					code="master.page.application.list" /></a></li>
		</security:authorize>
		
		<security:authorize access="hasRole('PROVIDER')">
			<li><a class="fNiv"><spring:message
						code="master.page.provider" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="sponsorship/provider/list.do"><spring:message
						code="master.page.sponsorship.list" /></a></li>
					<li><a href="item/provider/list.do"><spring:message
								code="master.page.provider.item" /></a></li>
				</ul></li>
			
		</security:authorize>
		
		<security:authorize access="hasRole('AUDITOR')">
		<li><a class="fNiv"><spring:message
						code="master.page.auditor" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="audit/auditor/list.do"><spring:message
						code="master.page.audit.list" /></a></li>
				</ul></li>
			
			
		</security:authorize>
		
		<security:authorize access="isAnonymous()">
			<li><a class="fNiv" href="security/login.do"><spring:message code="master.page.login" /></a></li>
			
			<li><a class="fNiv"><spring:message
						code="master.page.register" /></a>
				<ul>
					<li class="arrow"></li>

					<li><a href="company/create.do"><spring:message
								code="master.page.register.company" /></a></li>
					<li><a href="rookie/create.do"><spring:message
								code="master.page.register.rookie" /></a></li>
					<li><a href="provider/create.do"><spring:message
								code="master.page.register.provider" /></a></li>

				</ul>
				
				</security:authorize>
				<security:authorize access="permitAll">
				<li><a class="fNiv"><spring:message
						code="master.page.list" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="company/list.do"><spring:message
								code="master.page.list.company" /></a></li>
					<li><a href="provider/list.do"><spring:message
								code="master.page.list.provider" /></a></li>
					<li><a href="position/list.do"><spring:message
								code="master.page.list.position" /></a></li>
					<li><a href="item/list.do"><spring:message
								code="master.page.list.item" /></a></li>
					
				</ul></li>
		
		</security:authorize>
		
		
	</ul>
</div>

<div>
	<a href="?language=en">en</a> | <a href="?language=es">es</a>
</div>

