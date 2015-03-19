package grails.plugin.umlclassdiagram

/**
 * Generate UML diagrams (as images) from application structure.
 */
class UmlService {

   def modelGeneratorService
   
   def plantUmlService
   
  /**
  * Full UML drawing workflow: 
  *    generate model, convert to plantUML, generate image.
  * @return Stream representing the bytes of the output image.
  */
  def localPlantUml(ConfigurationCommand configurationCommand) {
    def model = 
      modelGeneratorService.makeModel(configurationCommand.diagramType)
    def plantUmlScript = 
      plantUmlService.modelToScript(model, configurationCommand)
      
    log.info "UML Script: $plantUmlScript"  
    plantUmlService.asStream(plantUmlScript)
  }

}    
