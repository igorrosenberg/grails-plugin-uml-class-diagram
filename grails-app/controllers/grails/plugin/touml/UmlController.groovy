package grails.plugin.touml

class UmlController {

    def umlService
       
    def index(ConfigurationCommand config){      
      def umlURL = umlService.generate(config)
      redirect url:umlURL     
    }
    
}

/**
* Command object for Configuration options
*/
class ConfigurationCommand { 
  /** Filters restricting the visibility of fields within classes */
  String [] fieldFilterRegexps
  /** Filters restricting the visibility of classes */
  String [] classFilterRegexps
  /** Short Class names for most used Java classes (from java API) */
  boolean showCanonicalJavaClassNames = false
  boolean filterGrailsFields = true
  DiagramType diagramType 

  /** Canonical or Short Class names for all fileds, not restricted to java API*/
  // NOT READY boolean showCanonicalClassNames
  /** Properties which generate an arrow also listed in the origin class */
  // NOT READY boolean duplicateFieldAndArrow
  /** Classes provided by the Grails framework */
  boolean showGrailsInternalClasses = false
  
    static constraints = {
        fieldFilterRegexps nullable: true
        classFilterRegexps nullable: true
    }
  }
  
enum DiagramType {
    DOMAIN, LAYERS
}
  