package grails.plugin.umlclassdiagram.generator

import static grails.plugin.umlclassdiagram.Constants.*

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate UML diagrams from grails domains.
 */
class DomainModelGeneratorService extends GrailsArtefactGeneratorService {

    def grailsApplication

    /**
     * Introspect all domain classes.
     * @return a List<ClassData>
     */
    protected getData() {
        grailsApplication.domainClasses.collect {
            extractDomainData(it)
        }
    }

    /**
     *  Introspect a domain class.
     * @return useful data in a ClassData map (packageName, className, properties, associations).
     */
    private extractDomainData(model) {
        def c = grailsApplication.classLoader.loadClass("${model.fullName}")
        log.debug "Introspect artefact data for ${model.fullName}"
        // FIXME really need an object ? already got a class...
        def instance = new DefaultGrailsDomainClass(c)
        [
                packageName : model.packageName,
                className   : model.fullName,
                properties  : propertiesToSpec(instance.getProperties()),
                associations: associationsToSpec(model.packageName, model.fullName, instance.getAssociations()),
        ]
    }

    /**
     * @return List < map ( propertyName , propertyType ) >
     */
    private propertiesToSpec(properties) {
        log.debug "Properties: ${properties}"
        properties.collect {
            def name = it.getName()
            def type = it.getType().name
            def ref = it.getReferencedPropertyType().name
            if (ref == type) {
                ref = null
            }
            // in the generic case, we have fieldType = "${type}<${ref}>"
            [name: name, type: (ref?: type), generic: (ref? type: null)]
        }
    }

    /**
     * @return List < ( modelName , left , type , right , typeName , assocName ) >
     */
    private associationsToSpec(packageName, modelName, associations) {
        associations.collect {
            log.debug "Association for $modelName: ${it}"
            def data = [modelName: modelName, type: 'o--']
            if (it.isManyToOne()) {
                data.left = '*'
                data.right = '1'
            } else if (it.isOneToMany()) {
                data.left = '1'
                data.right = '*'
            } else if (it.isOneToOne()) {
                data.left = '1'
                data.right = '1'
            } else if (it.isManyToMany()) {
                data.left = '*'
                data.right = '*'
            } else if (it.isEmbedded()) {
                data.left = ''
                data.right = ''
                data.type = '*-'
            } else {
                data.left = ''
                data.right = ''
            }
            data.typeName = it.referencedPropertyType?.name
            data.assocName = it.name

            data.packageName = it.referencedPropertyType?.package?.name ?: '_'
            data.className = it.referencedPropertyType?.simpleName ?: '_'

            log.info "Data to be exported: $data"

            def index = modelName.lastIndexOf('.')
            def shortModelName = modelName[index + 1..-1]

            [
            from: [package: packageName, class: shortModelName, field: data.assocName], //, field: data.left],
            to  : [package: data.packageName, class: data.className, field: data.right]
            ]
        }
    }

}
