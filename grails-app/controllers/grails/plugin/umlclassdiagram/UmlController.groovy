package grails.plugin.umlclassdiagram

import grails.validation.Validateable

class UmlController {

    def umlService

    def index() {
        def instance = new ConfigurationCommand()
        bindData(instance, params)
        render(view: 'index', model: [configurationCommandInstance: instance, pluginVersion: pluginVersion])
    }

    /**
     * @return the version of the UML Class Diagram plugin.
     */
    private getPluginVersion() {
        def pluginList = grailsApplication.mainContext.getBean('pluginManager').allPlugins
        def umlPlugin = pluginList.find { plugin ->
            plugin.name == 'umlClassDiagram'
        }
        umlPlugin?.version
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

        extraBindData(configurationCommandInstance, params)

        if (configurationCommandInstance.hasErrors()) {
            respond configurationCommandInstance.errors, view: 'index'
            return
        }

        if (params.scriptButton) {
            def umlString = umlService.plantUmlString(configurationCommandInstance)
            render(view: 'script', model: [uml: umlString])
        } else {
            def stream = umlService.localPlantUml(configurationCommandInstance)
            log.info 'Image byte stream sent to user'
            response.contentType = 'image/svg+xml;charset=utf-8'
            response.addHeader('Content-Disposition', 'attachment; filename="uml.svg"');
            response.addHeader('Vary', 'Accept-Encoding');
            response.outputStream << stream
        }
    }

    def grailsApplication

    /**
     * Mitigation for issue #7, and GRAILS-5582
     * @see https://github.com/igorrosenberg/grails-plugin-uml-class-diagram/issues/7
     * @see https://jira.grails.org/browse/GRAILS-5582, fixed in grails 2.3 
     */
    private void extraBindData(configurationCommandInstance, params) {
        def version = grailsApplication.metadata['app.grails.version'].split('\\.')
        if (version[0].toInteger() < 2 || (version[0].toInteger() == 2 && version[1].toInteger() < 3))
            "packageFilter,classFilter,fieldFilter,linkFilter".split(',').each {
                configurationCommandInstance[it] = new ConfigurationFilterCommand()
                bindData(configurationCommandInstance[it], params[it])
            }
    }

}

/**
 * Command sub-object for Configuration options
 */
@Validateable
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
@Validateable
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
    DB2, DOMAIN, LAYERS
}
