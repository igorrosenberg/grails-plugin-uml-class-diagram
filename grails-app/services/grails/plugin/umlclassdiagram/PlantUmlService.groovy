package grails.plugin.umlclassdiagram

import net.sourceforge.plantuml.*
import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate diagrams from a Model.
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
              uml.append('[').append(classifiers.length).append(']')
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
        packageMap.each { packageName, classMap ->  
            def matchedFilter = 
              configurationCommand.packageFilter.regexps?.find {regexp  -> 
                log.info "comparing package $packageName with regexp $regexp, result=${packageName?.matches(regexp)}"
                packageName?.matches(regexp) ? regexp : null
                }
            if (configurationCommand.packageFilter.inclusion && !matchedFilter) {
                log.info "Skipping package $packageName : no match for inclusion regexps"
                return                   
              }
            if (!configurationCommand.packageFilter.inclusion && matchedFilter) {
                log.info "Skipping package $packageName : it matched exclusion regexp=$matchedFilter"
                return                   
              }
            uml.append('package ').append(packageName).append(' <<Rect>> {\n')
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
            println "  relation $relation"
            println "  relation.from ${relation.from}"
            println "  relation.to ${relation.to}"
            uml.append(relation.from.package)
            uml.append('.')
            uml.append(relation.from.class)
            uml.append(' "')
            uml.append(relation.from.field)
            uml.append('" --> "')
            uml.append(relation.to.field)
            uml.append('" ')
            uml.append(relation.to.package)
            uml.append('.')
            uml.append(relation.to.class)
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
          s.generateImage(os, new FileFormatOption(FileFormat.SVG))
          os.close()

          // ready to send it over the wire! 
          os.toByteArray()
    }
          
    private shortName(name) {
      name.replaceAll('^.*\\.', '')      
    }      
    
}
