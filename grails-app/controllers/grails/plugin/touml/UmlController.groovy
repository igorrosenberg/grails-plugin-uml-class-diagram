package grails.plugin.touml

class UmlController {

    // static defaultAction = "domain"
    
    def umlService
       
    def index(ConfigurationCommand config){      
      render "fieldFilterRegexps=${config.fieldFilterRegexps} classFilterRegexps=${config.classFilterRegexps} duplicateFieldAndArrow=${config.duplicateFieldAndArrow} diagramType=${config.diagramType}"
    }
    def domain() {
        def umlURL = umlService.domain()
        redirect url:umlURL
    }

    def layers() {
        def umlURL = umlService.layers()
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
  /** Should properties which generate an arrow be listed in the origin class */
  boolean duplicateFieldAndArrow
  DiagramType diagramType 
  }
  
enum DiagramType {
    DOMAIN, LAYERS
}
  