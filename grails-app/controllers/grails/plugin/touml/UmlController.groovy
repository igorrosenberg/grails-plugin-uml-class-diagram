package grails.plugin.touml

class UmlController {

    // static defaultAction = "domain"
    
    def umlService
       
    def index(ConfigurationCommand config){      
      // render "fieldFilterRegexps=${config.fieldFilterRegexps} classFilterRegexps=${config.classFilterRegexps} duplicateFieldAndArrow=${config.duplicateFieldAndArrow} diagramType=${config.diagramType}"
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
  /** Short Class names for mosted used Java classes*/
  boolean showCanonicalJavaClassNames
  DiagramType diagramType 

  /** Canonical or Short Class names */
  // NOT READY boolean showCanonicalClassNames
  /** Properties which generate an arrow also listed in the origin class */
  // NOT READY boolean duplicateFieldAndArrow
  /** Classes provided by the Grails framework */
  // NOT READY boolean showGrailsInternals
  
    static constraints = {
        fieldFilterRegexps nullable: false // I think 'nullable: false' is the grails default, meaning this line is useless
        classFilterRegexps nullable: false
    }
  }
  
enum DiagramType {
    DOMAIN, LAYERS
}
  