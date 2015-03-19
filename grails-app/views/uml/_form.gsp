<%@ page import="grails.plugin.umlclassdiagram.*" %>

<script>
  function duplicatePreviousField(element) {
	  var previousSibling = element.previousSibling;
	  while (previousSibling && previousSibling.nodeType != 1) {
	      previousSibling = previousSibling.previousSibling;
	  }
	  previousSibling.removeAttribute('id');
	  element.parentNode.insertBefore(previousSibling.cloneNode(), element);
  }
</script>


<fieldset class="form">
HELLO
<g:radioGroup name="filterX"
              labels="['Inclusion','Exclusion']"
              values="['Inclusion','Exclusion']"
              value="'Inclusion'">
  <span>${it.label} ${it.radio}</span>
</g:radioGroup>
<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'packageFilterRegexps', 'error')} ">
	<label for="packageFilterRegexps">
		<g:message code="configurationCommand.packageFilterRegexps.label" default="Package Filter Regexps" />		
	</label>
	<g:each in="${configurationCommandInstance?.packageFilterRegexps ?: ' '}" var="regexp" status="i">
		<g:textField name="packageFilterRegexps" value="${regexp}" id="packageFilterRegexps${i}"/>
	</g:each>
	<a href="#" onclick="duplicatePreviousField(this)">Add</a>	
</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'classFilterRegexps', 'error')} ">
	<label for="classFilterRegexps">
		<g:message code="configurationCommand.classFilterRegexps.label" default="Class Filter Regexps" />		
	</label>
	<g:each in="${configurationCommandInstance?.classFilterRegexps ?: ' '}" var="regexp" status="i">
		<g:textField name="classFilterRegexps" value="${regexp}" id="classFilterRegexps${i}"/>
	</g:each>
	<a href="#" onclick="duplicatePreviousField(this)">Add</a>	
</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'fieldFilterRegexps', 'error')} ">
	<label for="fieldFilterRegexps">
		<g:message code="configurationCommand.fieldFilterRegexps.label" default="Field Filter Regexps" />		
	</label>
	<g:each in="${configurationCommandInstance?.fieldFilterRegexps ?: ' '}" var="regexp" status="i" >
		<g:textField name="fieldFilterRegexps" value="${regexp}" id="fieldFilterRegexps${i}"/>
	</g:each>
	<a href="#" onclick="duplicatePreviousField(this)">Add</a>
</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'linkFilterRegexps', 'error')} ">
	<label for="linkFilterRegexps">
		<g:message code="configurationCommand.linkFilterRegexps.label" default="Link Filter Regexps" />		
	</label>
	<g:each in="${configurationCommandInstance?.linkFilterRegexps ?: ' '}" var="regexp" status="i">
		<g:textField name="linkFilterRegexps" value="${regexp}" id="linkFilterRegexps${i}"/>
	</g:each>
	<a href="#" onclick="duplicatePreviousField(this)">Add</a>	
</div>

</fieldset>
<fieldset class="form">
<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'diagramType', 'error')} required">
	<label for="diagramType">
		<g:message code="configurationCommand.diagramType.label" default="Diagram Type" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="diagramType" from="${DiagramType?.values()}" keys="${DiagramType.values()*.name()}" required="" value="${configurationCommandInstance?.diagramType?.name()}" />
</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'filterGrailsFields', 'error')} ">
	<label for="filterGrailsFields">
		<g:message code="configurationCommand.filterGrailsFields.label" default="Filter Grails Fields" />
	</label>
	<g:checkBox name="filterGrailsFields" value="${configurationCommandInstance?.filterGrailsFields}" />
</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'showCanonicalJavaClassNames', 'error')} ">
	<label for="showCanonicalJavaClassNames">
		<g:message code="configurationCommand.showCanonicalJavaClassNames.label" default="Show Canonical Class Names" />
	</label>
	<g:checkBox name="showCanonicalJavaClassNames" value="${configurationCommandInstance?.showCanonicalJavaClassNames}" />
</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'showGrailsInternalClasses', 'error')} ">
	<label for="showGrailsInternalClasses">
		<g:message code="configurationCommand.showGrailsInternalClasses.label" default="Show Grails Internals" />
	</label>
	<g:checkBox name="showGrailsInternalClasses" value="${configurationCommandInstance?.showGrailsInternalClasses}" />
</div>

</fieldset>

