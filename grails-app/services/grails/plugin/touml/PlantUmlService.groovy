package grails.plugin.touml

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate diagrams from Graph using PlantUml formalism.
 */
class PlantUmlService {
         
    static final WEB_ROOT = 'http://www.plantuml.com/plantuml/img/'

    def grailsApplication         
          
    private shortName(name) {
      name.replaceAll('^.*\\.', '')      
      }
      
   /**
   * Generate custom URL to online plantUML service.
   * @param layers of type map of <layerName, packages>, where packages is a map of <packageNames, List<Artefacts>>
   */
   String asUmlLayers(layers){
        StringBuilder uml = new StringBuilder()
        
        uml.append('@startuml\n')
        
        def artefactNameList = []
        
        // draw classes
        layers.each { layerName, layerPackages ->
            uml.append('package ').append(layerName).append('s <<Rect>> {\n')
            layerPackages.each { packageName, artefactList ->
                // skip packageName
                // for each class inside that package: 
                for (artefact in artefactList) {
                    def artefactName = shortName(artefact.className)
                    artefactNameList << artefactName
                    uml.append('class ').append(artefactName) // class start
                    uml.append(' {\n') 
                    uml.append(
                      artefact.properties.join('\n')
                      )
                    uml.append('\n}\n')   // class end
                }
            }
            uml.append('}\n') // Package end
        }
        
          // draw Relations 
        layers.each { layerName, layerPackages ->
          for (artefact in layerPackages.values().flatten()) {
            artefact.properties.each() { destArtefact ->
                    def dest = destArtefact.capitalize()
                    if (dest in artefactNameList) {
                        uml.append("${shortName(artefact.className)} *-- ${dest}\n")
                      }
            }
          }
        }
        
        uml .append('@enduml\n')

        postProcess(uml)

   }

    /**
     * Generate custom URL to online plantUML service.
     * @param packages a map of <packageNames,List<DomainClass>>
     */
    String asUml(packages) {

        StringBuilder uml = new StringBuilder()
        
        uml .append('@startuml\n')       

        // draw Packages
        packages.each { packageName, classList ->
            uml.append('package ').append(packageName).append(' <<Rect>> {\n')
            // Each model of package
            for(model in classList) {
                uml.append('class ').append(model.className).append(' {\n') 
                model.properties.each {
                    uml.append(it.name).append(' : ').append(it.type).append('\n')
                  }
                uml.append('}\n')   // class end
            }
            uml.append('}\n') // Package end
        }
        
        // draw Relations 
        for(model in packages.values().flatten()) {
          model.associations.each() { relation ->
                  ['modelName','left','type','right','typeName'].each { key ->
                    uml.append(relation[key]).append(' ')
           }
           uml.append(': ').append(relation['assocName']).append('\n')
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
