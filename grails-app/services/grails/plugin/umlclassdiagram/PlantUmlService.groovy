package grails.plugin.umlclassdiagram

import net.sourceforge.plantuml.*
import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate diagrams from Graph using PlantUml formalism.
 */
class PlantUmlService {
         
    static final WEB_ROOT = 'http://www.plantuml.com/plantuml/img/'

    def grailsApplication

    def asWebUrl(finalUml) {
          WEB_ROOT + finalUml
    }
    
    /**
     * Generate plantUML specification.
     * @param packages a map of <packageNames,List<DomainClass>>
     * @return Compressed String
     */
    def asUmlSpec(packages) {

        StringBuilder uml = new StringBuilder()
        
        uml.append('@startuml\n')       

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

         uml.append('@enduml\n')

        postProcess(uml)
    }
        
    /**
    * Convert compressed Uml Spec into PNG byte Stream
    */
    def asStream (finalUml) {
          def s = new SourceStringReader(decodeAndDecompress(finalUml))
          def os = new ByteArrayOutputStream()
          s.generateImage(os, new FileFormatOption(FileFormat.PNG))
          os.close()

          // ready to send it over the wire! 
          os.toByteArray()
    }
          
    private String postProcess(uml) {
          uml.append(metaData())
          def finalUml = uml.toString()
          log.debug "UML: $finalUml"
          compressAndEncodeString(finalUml)
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
        new AsciiEncoder().encode(compressed)
    }

    /**
    * Zlib decompression (compatible plantUML).
    */
    private String decodeAndDecompress(String encoded) {
        def compressed = new AsciiEncoder().decode(encoded)
        def bytes = new CompressionZlib().decompress(compressed)
        new String(bytes, "UTF-8")
    }    

    private shortName(name) {
      name.replaceAll('^.*\\.', '')      
      }      
    
   }
