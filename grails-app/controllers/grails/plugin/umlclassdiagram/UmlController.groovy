package grails.plugin.umlclassdiagram

class UmlController {

    def umlService

    def index() {
        def instance = new ConfigurationCommand()
        bindData(instance, params)
        render (view:'index', model:[configurationCommandInstance: instance]) 
    }

    def draw(ConfigurationCommand configurationCommandInstance) {
      if (configurationCommandInstance == null) {
        request.withFormat {
		      form multipartForm {
		          flash.message = message(code: 'default.not.found.message', args: [message(code: 'configurationCommand.label', default: 'ConfigurationCommand'), params.id])
		          redirect action: "index", method: "GET"
		      }
  		    '*'{ render status: NOT_FOUND }
    		}
        return
      }

      if (configurationCommandInstance.hasErrors()) {
          respond configurationCommandInstance.errors, view:'index'
          return
      }

      def stream = umlService.localPlantUml(configurationCommandInstance)
      log.info 'PNG byte stream sent to user'
      render file: stream, contentType: 'image/png'          
    }
 
}

/**
* Command object for Configuration options
*/
class ConfigurationCommand { 

  /** Filters restricting the visibility of fields within classes */
  String [] fieldFilterRegexps = ['^id$','^version$']

  /** Filters restricting the visibility of classes */
  String [] classFilterRegexps = new String[0]

  /** Short Class names for most used Java classes (from the java API) */
  boolean showCanonicalJavaClassNames = false

  /** Controllers and Services provided by the Grails framework */
  boolean showGrailsInternalClasses = false

  boolean filterGrailsFields = true

  DiagramType diagramType  = DiagramType.DOMAIN

  /** Canonical or Short Class names for all fields, not restricted to java API*/
  // TODO boolean showCanonicalClassNames
  /** Properties which generate an arrow also listed in the origin class */
  // TODO boolean duplicateFieldAndArrow
  
  static constraints = {
    fieldFilterRegexps nullable: true
    classFilterRegexps nullable: true
  }
}
  
enum DiagramType {
    DOMAIN, LAYERS, DB2
}

