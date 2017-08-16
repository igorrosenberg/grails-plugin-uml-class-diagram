package umlclassdiagram

import groovy.util.logging.Slf4j

/**
 * Generate UML diagrams (as images) from application structure.
 */

@Slf4j
class UmlService {

    def modelGeneratorService

    def plantUmlService

    /**
     * Full UML drawing workflow:
     *    generate model, convert to plantUML, generate image.
     * @return Stream representing the bytes of the output image.
     */
    def localPlantUml(ConfigurationCommand configurationCommand) {
        def plantUmlScript = plantUmlString(configurationCommand)
        log.info "UML Script: $plantUmlScript"
        plantUmlService.asStream(plantUmlScript)
    }

    /**
     * UML as text: generate model, convert to plantUML syntax.
     * @return Stream representing the bytes of the output image.
     * @see #localPlantUml(umlclassdiagram.ConfigurationCommand)
     */
    def plantUmlString(ConfigurationCommand configurationCommand) {
        def model = modelGeneratorService.makeModel(configurationCommand.diagramType)
        plantUmlService.modelToScript(model, configurationCommand)
    }
}
