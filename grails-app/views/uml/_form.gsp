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

  <g:render template="oneFilter" bean="${configurationCommandInstance}" var="filterName" 
      collection="${'package class field link'.split().collect{ it + 'Filter'}}"/>
 
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

