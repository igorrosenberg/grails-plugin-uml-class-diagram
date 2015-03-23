package grails.plugin.umlclassdiagram.generator

import static grails.plugin.umlclassdiagram.Constants.*

class LayersModelGeneratorService extends GrailsArtefactGeneratorService {

    def grailsApplication

    /**
     * Introspect the internal layered architecture (controllers and services)
     * @return List < ClassData > .
     * */
    protected getData() {
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
        log.debug "Introspect artefact data for bean=${model.name}"

        Class realClass = model.getClazz()
        String fromPackageName = realClass.package?.name ?: '_'
        String fromClassName = realClass.simpleName ?: '_'
        // TODO there must be a better way to introspect an org.codehaus.groovy.grails.commons.GrailsClass
        def fields = model.referenceInstance.properties
        fields = fields.findAll { k, v -> !(k in exclusionList) }

        // as dict of {name, type, generic}
        def properties = []
        // as list of dict of {from, to}
        def links = []

        fields.each { k, v ->
            def destFieldName = getFieldType(k, v)
            def index = destFieldName.lastIndexOf('.')
            def shortDestFieldName = destFieldName[index + 1..-1]
            def destFieldPackage = index > 0 ? destFieldName[0..index - 1] : '_'
            properties <<
                    [
                            name: k,
                            type: shortDestFieldName // destFieldName
                    ]

            links <<
                    [
                            from: [package: fromPackageName, class: fromClassName, field: k],
                            to  : [package: destFieldPackage, class: shortDestFieldName]
                    ]
        }

        [
                packageName : fromPackageName,
                className   : fromClassName,
                properties  : properties,
                associations: links,
        ]
    }

    /**
     * Type of a field found on an artefact.
     * @param k the field name.
     * @param v the value of that field.
     * @return String representing that field's type.
     */
    private String getFieldType(k, v) {
        // Simple form, v is set
        if (v) {
            def fieldType = v.getClass().name ?: UNKNOWN
            fieldType = fieldType.replaceAll('\\$.*$', '')
            return fieldType
        }
        /*
        try {
            def cheat = model.referenceInstance.getClass().getField(k)
            log.warn "Found field for $fromClassName.$k : $cheat"
        } catch (NoSuchFieldException nfe) {
            log.warn "NoSuchFieldException $fromClassName.$k "
        }*/

        // Service form
        def beanName = k.capitalize()  // FIXME sometimes artefact.propertyName is capitalized, sometimes not ?!
        if (beanName =~ /.*Service/) {
            def serviceArtefact = grailsApplication.getArtefacts('Service').find { it ->
                it.propertyName.capitalize() == beanName
            }
            if (serviceArtefact) {
                log.debug "   --- $beanName has matching service artefact: $serviceArtefact"
                return serviceArtefact.fullName
            } else {
                return UNKNOWN
            }
        }

        // Grails specifics
        if (k == 'grailsApplication') {
            return 'org.codehaus.groovy.grails.commons.GrailsApplication'
        }

        return UNKNOWN
    }

}
