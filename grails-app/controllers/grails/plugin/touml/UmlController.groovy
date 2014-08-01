package grails.plugin.touml

class UmlController {

    def umlService
       
    def index(ConfigurationCommand config) {
      def umlURL = umlService.redirect(config)
      log.info 'user redirected'
      redirect url:umlURL     
    }

    def localPlantUml() {
      def stream = umlService.localPlantUml(params.id)
      log.info 'PNG byte stream sent to user '
      render file: stream, contentType: 'image/png'          
    }
 }

/**
* Command object for Configuration options
*/
class ConfigurationCommand { 

  /** Filters restricting the visibility of fields within classes */
  String [] fieldFilterRegexps = ['^id$','^version$'] as String []

  /** Filters restricting the visibility of classes */
  String [] classFilterRegexps = new String[0]

  /** Short Class names for most used Java classes (from java API) */
  boolean showCanonicalJavaClassNames = false

  /** Classes provided by the Grails framework */
  boolean showGrailsInternalClasses = false

  boolean filterGrailsFields = true

  DiagramType diagramType  = DiagramType.DOMAIN

  RenderingEngine renderingEngine = RenderingEngine.LOCAL_PLANT_UML

  /** Canonical or Short Class names for all fileds, not restricted to java API*/
  // NOT READY boolean showCanonicalClassNames
  /** Properties which generate an arrow also listed in the origin class */
  // NOT READY boolean duplicateFieldAndArrow
  
    static constraints = {
        fieldFilterRegexps nullable: true
        classFilterRegexps nullable: true
    }
  }
  
enum DiagramType {
    DOMAIN, LAYERS
}

enum RenderingEngine {
    LOCAL_PLANT_UML, WEB_PLANT_UML, YUML
}
