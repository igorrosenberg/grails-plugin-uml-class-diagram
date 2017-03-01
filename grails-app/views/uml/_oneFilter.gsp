<div class="fieldcontain ${hasErrors(
  bean: filterCommand, 
  field: 'regexps', 
  'error')} ">
  
	<label for="${filterName}">
    <g:message code="configurationCommand.${filterName}.label" default="${filterName}" />          
	</label>

  <g:set var="filterCommand" value="${configurationCommandInstance ? configurationCommandInstance[filterName] : [:]}"/>

  <g:radioGroup name="${filterName}.inclusion"
                labels="['Inclusion','Exclusion']"
                values="[true,false]"
                value="${filterCommand?.inclusion as boolean}">
    <span>${it.label} ${it.radio}</span>
  </g:radioGroup>

	<g:each in="${filterCommand?.regexps ?: ' '}" var="regexp" status="i">
		<g:textField name="${filterName}.regexps" value="${regexp}" id="${filterName}.regexps${i}"/>
	</g:each>

	<a href="#" onclick="duplicatePreviousField(this)">Add</a>	

</div>


