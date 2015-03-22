package grails.plugin.umlclassdiagram.generator

import static grails.plugin.umlclassdiagram.Constants.*

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
        log.debug "Introspect artefact data for bean=${model?.name}"

        Class realClass = model.getClazz()
        String fromPackageName = realClass.getPackage().name
        String fromClassName = realClass.getSimpleName()
        // TODO there must be a better way to introspect an org.codehaus.groovy.grails.commons.GrailsClass
        def fields = model.getReferenceInstance().properties
        fields = fields.findAll { k, v -> !(k in exclusionList) }

        // as dict of {name, type, generic}
        def properties = fields.collect{ k,v ->
            [name: k,
             type : v.getClass().name.replaceAll('\\$.*$', '')
            ]
        }
        // as list of dict of {from, to}
        def links = fields.collect { k,v ->
            def destFieldName = v.getClass().name.replaceAll('\\$.*$', '')
            destFieldName = destFieldName[destFieldName.lastIndexOf('.')+1..-1]

            [
                    from: [package: fromPackageName, class: fromClassName, field: k],
                    to  : [package: v.getClass().getPackage().name, class: destFieldName]
            ]
        }
        [
                packageName  : fromPackageName,
                className    : fromClassName,
                properties   : properties,
                associations : links,
        ]
    }

}
