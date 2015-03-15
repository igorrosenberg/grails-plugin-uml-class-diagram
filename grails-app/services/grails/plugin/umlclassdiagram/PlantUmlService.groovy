package grails.plugin.umlclassdiagram

import net.sourceforge.plantuml.*
import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate diagrams from Graph using PlantUml formalism.
 */
class PlantUmlService {
         
    def grailsApplication
         
    /**
     * Add to StringBuilder the PlantUML syntax describing the properties.
     * @param configurationCommand: user preferences
     */
    private void drawProperties(propertiesMap, StringBuilder uml, configurationCommand) {
      propertiesMap.each { propertyName, classifiers ->
          uml.append(propertyName)
          if (classifiers.type) {
            uml.append(': ').append(classifiers.type)
            if (classifiers.length) {
              uml.append('[').append(classifiers.length).append(']').
            }
          }
          // TODO: add decorators for properties index, abstract, static
          uml.append('\n')
      }             
    }

    /**
     * Add to StringBuilder the PlantUML syntax describing the packages.
     * @param configurationCommand: user preferences
     */
    private void drawPackages(packageMap, StringBuilder uml, configurationCommand) {
        model.partition.each { packageName, classMap ->
            uml.append('package ').append(packageName).append(' <<Rect>> {\n')
            // draw classes
            classMap.each { className, propertiesMap ->
                uml.append('class ').append(className).append(' {\n') 
                drawProperties(propertiesMap, uml, configurationCommand) 
                uml.append('}\n')   // class end
            }
            uml.append('}\n') // Package end
        }
    }

    /**
     * Add to StringBuilder the PlantUML syntax describing the relations.
     * @param configurationCommand: user preferences
     */
    private void drawRelations(relationList, StringBuilder uml, configurationCommand) {
        relationList.each() { relation ->
            uml.append(from.package)
            uml.append('.')
            uml.append(from.class)
            uml.append(' "')
            uml.append(from.field)
            uml.append('" --> "')
            uml.append(to.field)
            uml.append('" ')
            uml.append(to.package)
            uml.append('.')
            uml.append(to.class)
            uml.append('\n')
        }
    }

    /**
     * @param model: (package x class x field + links)
     * @param configurationCommand: user preferences
     * @return well-formed PlantUML script.
     */     
    def modelToScript(model, configurationCommand) {
        StringBuilder uml = new StringBuilder()        
        uml.append('@startuml\n')       
        drawPackages(model.partition, uml, configurationCommand)
        drawRelations(model.links, uml, configurationCommand)
        uml.append('@enduml\n')
        uml.toString()
    }
        
    /**
     * Convert compressed Uml Spec into PNG byte Stream
     */
    def asStream (finalUml) {
          def s = new SourceStringReader(finalUml)
          def os = new ByteArrayOutputStream()
          s.generateImage(os, new FileFormatOption(FileFormat.PNG))
          os.close()

          // ready to send it over the wire! 
          os.toByteArray()
    }
          
    private shortName(name) {
      name.replaceAll('^.*\\.', '')      
    }      
    
}
