<%@ page import="grails.plugin.umlclassdiagram.*" %>

<script>
function duplicatePreviousField(element) {
	var previousSibling = element.previousSibling;
	while(previousSibling && previousSibling.nodeType != 1) {
	    previousSibling = previousSibling.previousSibling
	}
	previousSibling.removeAttribute('id');
	var clone = previousSibling.cloneNode();
	element.parentNode.insertBefore(clone,element);
}
</script>


<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'fieldFilterRegexps', 'error')} ">
	<label for="fieldFilterRegexps">
		<g:message code="configurationCommand.fieldFilterRegexps.label" default="Field Filter Regexps" />		
	</label>
	<g:each in="${configurationCommandInstance?.fieldFilterRegexps ?: ' '}" var="regexp" status="i" >
		<g:textField name="fieldFilterRegexps" value="${regexp}" id="fieldFilterRegexps${i}"/>
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

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'renderingEngine', 'error')} required">
	<label for="renderingEngine">
		<g:message code="configurationCommand.renderingEngine.label" default="Rendering Engine" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="renderingEngine" from="${RenderingEngine?.values()}" keys="${RenderingEngine.values()*.name()}" required="" value="${configurationCommandInstance?.renderingEngine?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'showCanonicalJavaClassNames', 'error')} ">
	<label for="showCanonicalJavaClassNames">
		<g:message code="configurationCommand.showCanonicalJavaClassNames.label" default="Show Canonical Java Class Names" />
		
	</label>
	<g:checkBox name="showCanonicalJavaClassNames" value="${configurationCommandInstance?.showCanonicalJavaClassNames}" />

</div>

<div class="fieldcontain ${hasErrors(bean: configurationCommandInstance, field: 'showGrailsInternalClasses', 'error')} ">
	<label for="showGrailsInternalClasses">
		<g:message code="configurationCommand.showGrailsInternalClasses.label" default="Show Grails Internal Classes" />
		
	</label>
	<g:checkBox name="showGrailsInternalClasses" value="${configurationCommandInstance?.showGrailsInternalClasses}" />

</div>

