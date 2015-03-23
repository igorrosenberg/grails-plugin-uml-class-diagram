package grails.plugin.umlclassdiagram

import net.sourceforge.plantuml.*
import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate diagrams from a Model.
 */
class PlantUmlService {

    /*
    TODO add shortening all Java class names
      if (!config.showCanonicalJavaClassNames) {
        classList.each { classData ->
          classData.properties.collect { property ->
            'java.lang,java.util,java.io'.split(',').each {
              property.type =  property.type.replaceAll("^$it\\.",'')
            }
          }
        }
      }
     */

    /**
     * Add to StringBuilder the PlantUML syntax describing the properties.
     * @param configurationCommand : user preferences
     */
    private void drawProperties(propertiesMap, StringBuilder umlBuilder, configurationCommand) {
        StringBuilder uml = new StringBuilder()
        propertiesMap.each { propertyName, classifiers ->
            uml.setLength(0)  // empty string builder for each new iteration
            uml.append(propertyName)
            if (classifiers.type) {
                uml.append(': ').append(classifiers.type)
                if (classifiers.length) {
                    uml.append('[').append(classifiers.length).append(']')
                }
            }
            // TODO: add decorators for properties index, abstract, static
            if (!configurationCommand.fieldFilter.validate(uml.toString()))
                return
            umlBuilder.append(uml).append('\n')
        }
    }

    /**
     * Add to StringBuilder the PlantUML syntax describing the packages.
     * @param configurationCommand : user preferences
     */
    private void drawPackages(packageMap, StringBuilder uml, configurationCommand) {
        packageMap.each { packageName, classMap ->
            if (!configurationCommand.packageFilter.validate(packageName))
                return
            uml.append('namespace ').append(packageName ?: '_').append(' {\n')
            classMap.each { className, propertiesMap ->
                if (!configurationCommand.classFilter.validate(className.toString()))
                    return
                uml.append('class ').append(className).append(' {\n')
                drawProperties(propertiesMap, uml, configurationCommand)
                uml.append('}\n')   // class end
            }
            uml.append('}\n') // Package end
        }
    }

    /**
     * Add to StringBuilder the PlantUML syntax describing the relations.
     * @param configurationCommand : user preferences
     */
    private void drawRelations(relationList, StringBuilder umlTarget, configurationCommand) {
        relationList.each() { relation ->

            StringBuilder uml = new StringBuilder()
            uml.append(relation.from.package)
            uml.append('.')
            uml.append(relation.from.class)
            if (relation.from.field) {
                uml.append(' "')
                uml.append(relation.from.field)
                uml.append('"')
            }
            uml.append(' --> ')
            if (relation.to.field) {
                uml.append('"')
                uml.append(relation.to.field)
                uml.append('" ')
            }
            uml.append(relation.to.package)
            uml.append('.')
            uml.append(relation.to.class)

            if (!configurationCommand.linkFilter.validate(uml.toString()))
                return

            log.info "  relation $uml"
            umlTarget.append(uml).append('\n')
        }
    }

    /**
     * @param model : (package x class x field + links)
     * @param configurationCommand : user preferences
     * @return String well-formed PlantUML script.
     */
    String modelToScript(model, configurationCommand) {
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
    def asStream(finalUml) {
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
