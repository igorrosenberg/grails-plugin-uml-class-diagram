package grails.plugin.umlclassdiagram

import static grails.plugin.umlclassdiagram.Constants.*

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

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
  def localPlantUml(ConfigurationCommand configurationCommand) {
    def model = 
      modelGeneratorService.makeModel(configurationCommand.diagramType)
    def plantUmlScript = 
      plantUmlService.modelToScript(model, configurationCommand)
    plantUmlService.asStream(plantUmlScript)
  }

}    
