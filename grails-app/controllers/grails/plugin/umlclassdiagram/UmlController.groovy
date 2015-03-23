package grails.plugin.umlclassdiagram

class UmlController {

    def umlService

    def index() {
        def instance = new ConfigurationCommand()
        bindData(instance, params)
        render(view: 'index', model: [configurationCommandInstance: instance])
    }

    def draw(ConfigurationCommand configurationCommandInstance) {
        if (configurationCommandInstance == null) {
            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'configurationCommand.label', default: 'ConfigurationCommand'), params.id])
                    redirect action: "index", method: "GET"
                }
                '*' { render status: NOT_FOUND }
            }
            return
        }

        if (configurationCommandInstance.hasErrors()) {
            respond configurationCommandInstance.errors, view: 'index'
            return
        }

        if (params.onlyText) {
            render text: umlService.plantUmlString(configurationCommandInstance), contentType: "text/plain"
            return
        }

        def stream = umlService.localPlantUml(configurationCommandInstance)
        log.info 'Image byte stream sent to user'
        // response.addHeader('Content-Disposition', 'attachment; filename="uml.svg"');
        render file: stream, contentType: 'image/svg+xml', filename:'uml.svg' //'image/png'
    }

}

/**
 * Command sub-object for Configuration options
 */
class ConfigurationFilterCommand {
    /** Filters restricting the visibility */
    String[] regexps = new String[0]
    /** true => include specified regexp; false => exclude specified regexp */
    boolean inclusion

    static constraints = {
        regexps nullable: true
    }

    boolean validate(target) {
        def matchedFilter =
                this.regexps?.find { regexp ->
                    log.info "comparing $target with regexp $regexp, result=${target?.matches(regexp)}"
                    target?.matches(regexp) ? regexp : null
                }
        if (this.inclusion && !matchedFilter) {
            log.info "Skipping $target : no match for inclusion regexps"
            return false
        }
        if (!this.inclusion && matchedFilter) {
            log.info "Skipping $target : it matched exclusion regexp=$matchedFilter"
            return false
        }
        return true
    }
}

/**
 * Command object for Configuration options
 */
class ConfigurationCommand {

    /** Visibility of packages */
    ConfigurationFilterCommand packageFilter

    /** Visibility of classes within packages */
    ConfigurationFilterCommand classFilter

    /** Visibility of fields within classes */
    ConfigurationFilterCommand fieldFilter

    /** Visibility of links between classes */
    ConfigurationFilterCommand linkFilter

    /** Short Class names for most used Java classes (from the java API) */
    boolean showCanonicalJavaClassNames = false

    /** Controllers and Services provided by the Grails framework */
    boolean showGrailsInternalClasses = false

    boolean filterGrailsFields = true

    DiagramType diagramType = DiagramType.DB2

    /** Canonical or Short Class names for all fields, not restricted to java API*/
    // TODO boolean showCanonicalClassNames
    /** Properties which generate an arrow also listed in the origin class */
    // TODO boolean duplicateFieldAndArrow

}

enum DiagramType {
    DOMAIN, LAYERS, DB2
}

