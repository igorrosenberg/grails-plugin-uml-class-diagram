package grails.plugin.umlclassdiagram.generator

import static grails.plugin.umlclassdiagram.Constants.*

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class LayersModelGeneratorService {

    def grailsApplication

    /**
     * Generate the model (package x class x field + links) of the underlying Grails Domain data.
     * @return ( packages x classes x fields + injected dependencies ) .
     */
    def makeModel() {

        def universe = [:]
        def links = []

        getData().each { classData ->
            def schema = classData.packageName
            def table = classData.className

            // store start
            universe[schema] = universe[schema] ?: [:]
            universe[schema][table] = universe[schema][table] ?: [:]

            // store end
            classData.properties.each { dict ->  // as dict of name, type, generic
                def column = dict.name
                def type = dict.generic ? "${dict.generic}<${dict.type}>" : dict.type
                universe[schema][table][column] = universe[schema][table][column] ?: [:]
                universe[schema][table][column].type = type
                //universe[schema][table][column].length = length
            }

            links += classData.associations
        }

        [partition: universe, links: links]
    }

    /**
     * Introspect the internal layered architecture (controllers and services)
     * @return List < ClassData > .
     * */
    private getData() {
        // Expose all controllers internals.
        // Then also do it for services
        ARTEFACTS.collect { artefactType, exclusionList ->
            grailsApplication.getArtefacts(artefactType).collect {
                extractArtefactData(it, exclusionList)
            }
        }.flatten()

    }

    /**
     *  Introspect an Artefact.
     * @return useful data in a ClassData map (className, properties, associations).
     */
    private extractArtefactData(model, exclusionList) {
        log.debug "Introspect artefact data for bean=" + model.name
        def bruntProperties = model.getReferenceInstance().properties.findAll { k, v -> !(k in exclusionList) }

        def links = getModelLinks(bruntProperties).collect { field ->
            [
                    from: [package: model.packageName, class: model.fullName, field: field.name],
                    to  : [package: 'TBD', class: field.type]
            ]
        }
        [
                packageName : model.packageName,
                className   : model.fullName,
                properties  : getModelProperties(bruntProperties),
                associations: links ,
        ]
    }

    /**
     * @return List < map (name, type) > : list of {propertyName , propertyType } dictionaries
     */
    private getModelProperties(bruntProperties) {
        bruntProperties.collect { k, v ->
            getQualifiedType(k, v)
        }
    }

    /**
     * @return List < ( modelName , left , type , right , typeName , assocName ) >
     */
    private getModelLinks(bruntProperties) {
        // on every field of the 'model' class, extract link information
        bruntProperties.collect { k, v ->
            def propertyType = getQualifiedType(k, v)
            [name : k, type: propertyType.type]
        }

    }

    /**
     * @return dictionnary , the (possibly) fully qualified class name of the bean.
     */
    private getQualifiedType(beanName, beanData) {
        beanName = beanName.capitalize()         // sometimes artefact.propertyName is capitalized, sometimes not ?!
        def type
        def generic

        return [name: beanName, type: beanName]

        if (beanData) {
            type = beanData.getType().name
            generic = beanData.getReferencedPropertyType().name
            if (type == generic) {
                generic = null
            }
        } else {
            // TODO maybe more data can be extracted, not only services
            type = UNKNOWN
            if (beanName =~ /.*Service/) {
                def serviceArtefact = grailsApplication.getArtefacts('Service').find { it ->
                    it.propertyName.capitalize() == beanName
                }
                if (serviceArtefact) {
                    log.debug "   --- $beanName has matching artefact: $serviceArtefact"
                    type = serviceArtefact.fullName
                }
            }
        }
        log.debug "Type for $beanName : $type"
        [name: beanName, type: type.replaceAll('\\$.*$', ''), generic: generic]
    }


}
