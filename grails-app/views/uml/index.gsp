<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>UML output Configuration</title>
	</head>
	<body>
		<div class="content scaffold-create" role="main">
			<h1>UML output Configuration</h1>
			<g:if test="${flash.message}">
  			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${configurationCommandInstance}">
			  <ul class="errors" role="alert">
				  <g:eachError bean="${configurationCommandInstance}" var="error">
	  			  <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
	  			    <g:message error="${error}"/>
	  			  </li>
				  </g:eachError>
			  </ul>
			</g:hasErrors>
			<g:form action="draw" method="get">
				<g:render template="form"/>
				<fieldset class="buttons">
					<g:submitButton name="draw" class="save" 
					      value="${message(code: 'default.button.create.label', default: 'Draw')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
