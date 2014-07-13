package grails.plugin.touml

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate diagrams from Graph using PlantUml formalism.
 */

class PlantUmlService {
         
    static final WEB_ROOT = 'http://www.plantuml.com/plantuml/img/'

    def grailsApplication
     
    /**
     * Generate custom URL to online plantUML service.
     * @param packages a map of <packageNames,List<DomainClass>>
     */
    String asUML(packages) {

        StringBuilder uml = new StringBuilder()
        
        uml .append('@startuml\n')

        // draw Packages
        packages.each { packageName, classList ->
            uml.append('package ').append(packageName).append(' <<Rect>> {\n')
            // Each model of package
            for(model in classList) {
                uml.append('class ').append(model.className).append(' {\n') 
                uml.append(model.properties.collect {"${it.name} : ${it.type}"}.join('\n'))
                uml.append('\n}\n')   // class end
            }
            uml.append('}\n') // Package end
        }
        
        // draw Relations 
        for(model in packages.values().flatten()) {
          model.associations.each() { relation ->
                  ['modelName','left','type','right','typeName'].each { key ->
                    uml.append(relation[key] + ' ')
                  }
                  uml.append(': ' + relation['assocName']).append('\n')
              // uml.append(relation).append('\n')
          }
        }

        // draw subClasses
        for(model in packages.values().flatten()) {
          model.subClasses.each() { subClass ->
              uml.append(subClass).append('\n')
          }
        }

         uml .append('@enduml\n')

        postProcess(uml)
        }
        
        private String postProcess(uml) {
          uml.append(metaData())
          def finalUml = uml.toString()
          log.debug "UML: $finalUml"
          WEB_ROOT + compressAndEncodeString(finalUml)
        }    
        

                // extra provenance information
private String metaData() {
"""
title ${grailsApplication.metadata.'app.name'} - ${grailsApplication.metadata.'app.version'}
legend left
Grails version: ${grailsApplication.metadata.'app.grails.version'}
endlegend
"""
}

    /**
    * Zlib compression (compatible plantUML).
    */
    private String compressAndEncodeString(String str) {
        byte[] xmlBytes = str.getBytes('UTF-8')
        byte[] compressed = new CompressionZlib().compress(xmlBytes)
        return new AsciiEncoder().encode(compressed)
    }
     
     
   }
