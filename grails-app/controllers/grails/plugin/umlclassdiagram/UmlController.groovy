package grails.plugin.umlclassdiagram

class UmlController {

    def umlService

    static allowedMethods = [draw: "POST"]

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

      def umlURL = umlService.redirect(configurationCommandInstance)
      log.info "user redirected to ${umlURL?.size() > 30 ? umlURL[0..30] + '...' : umlURL}"
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

  /** Short Class names for most used Java classes (from the java API) */
  boolean showCanonicalJavaClassNames = false

  /** Controllers and Services provided by the Grails framework */
  boolean showGrailsInternalClasses = false

  boolean filterGrailsFields = true

  DiagramType diagramType  = DiagramType.DOMAIN

  RenderingEngine renderingEngine = RenderingEngine.LOCAL_PLANT_UML

  /** Canonical or Short Class names for all fileds, not restricted to java API*/
  // TODO boolean showCanonicalClassNames
  /** Properties which generate an arrow also listed in the origin class */
  // TODO boolean duplicateFieldAndArrow
  
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

