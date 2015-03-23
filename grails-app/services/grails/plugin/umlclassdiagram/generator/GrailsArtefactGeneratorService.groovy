package grails.plugin.umlclassdiagram.generator

abstract class GrailsArtefactGeneratorService {

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
     * Introspect grails artefacts (controllers, services, domains).
     * @return List < ClassData > .
     * */
    abstract protected getData()
}
