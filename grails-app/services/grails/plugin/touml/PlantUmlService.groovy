package grails.plugin.touml

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
/**
 * Generate diagrams from Graph.
 */

class PlantUmlService {
         
    static final WEB_ROOT = 'http://www.plantuml.com/plantuml/img/'

def grailsApplication

     String mockToSpec() {
      """
@startuml
Bob -> Alice : hello
@enduml      
      """
     }

    /**
    * Zlib compression (compatible plantUML).
    */
    private String compressAndEncodeString(String str) {
        byte[] xmlBytes = str.getBytes("UTF-8")
        byte[] compressed = new CompressionZlib().compress(xmlBytes)
        return new AsciiEncoder().encode(compressed)
    }

 
     private propertiesToSpec(properties) {
     	 log.debug "Properties: ${properties}"
       properties.collect { 
          def name = it.getName()
          def type =  it.getType().toString().replaceAll("class ", "")
          "$name: $type"
        }
      }

      private associationsToSpec(modelName, associations) {
      associations.collect {
					log.debug "Association for $modelName: ${it}"
          def left = "", right = "", type = "o--"
          if(it.isManyToOne()) {
              left = '"*"'
              right = '"1"'
          } else if(it.isOneToMany()) {
              left = '"1"'
              right = '"*"'
          } else if(it.isOneToOne()) {
              left = '"1"'
              right = '"1"'
          } else if(it.isManyToMany()) {
              left = '"*"'
              right = '"*"'
          } else if(it.isEmbedded()) {
              type = "*--"
          }
          def typeName  = it.getType().name 
          def assocName = it.getName()
          "$modelName $left $type $right $typeName : $assocName"
      }
      }
    
     private subClassesToSpec (fullName, p) {
				log.debug "subClassesToSpec BUGGED"
        return []
				def coponentBaseDomain = grailsApplication.domainClasses.find { it.name == fullName}
				log.debug "fullName has sub classes ? " + coponentBaseDomain.hasSubClasses()
				coponentBaseDomain.getSubClasses().collect { 
						  			"$fullName <|-- $p.${it.name}"
				                  	}	
    }	

    /**
     *  Given a Class, extract useful data in a map (className, properties, associations, subClasses)
     *   @param p TODO remove this param
     */
    private extractData(model, p) {
      log.debug "Extracting data from ${model.fullName}"
      def c = grailsApplication.classLoader.loadClass("${model.fullName}")
      // FIXME really need an object  ?already got a class...
      def instance = new DefaultGrailsDomainClass(c)      
      [
      className: model.fullName,
      properties: propertiesToSpec(instance.getProperties()),
      associations: associationsToSpec(model.fullName, instance.getAssociations()),
      subClasses: subClassesToSpec (model.fullName, p),
      ]
      }

      // a map of <packageNames,List<DomainClass>>
      private getPackages(domainClasses) {
        def packages = [:]           
        for (model in domainClasses) {
            def packageName = model.getPackageName()
            if (!packages[packageName]) { 
              packages[packageName] = []
              }
            packages[packageName].add(extractData(model,packageName))
        }
        packages
       }
      
    /**
     * Generate custom URL to online plantUML service.
     */
    String domain() {

          // a map of <packageNames,List<DomainClass>>
        def packages = getPackages(grailsApplication.domainClasses)
              
        StringBuilder uml = new StringBuilder()
        
        // Packages
        packages.each { packageName, classList ->
            uml.append("package ").append(packageName).append(" <<Rect>> {\n")
            // Each model of package
            for(model in classList) {
                uml.append('class ').append(model.className).append(' {\n') 
                uml.append(model.properties.join('\n'))
                uml.append("}\n")   // class end
            }
            uml.append("}\n") // Package end
        }
        
        // Relations 
        for(model in packages.values().flatten()) {
          model.associations.each() { relation ->
              uml.append(relation).append("\n")
          }
        }

        // subClasses
        for(model in packages.values().flatten()) {
          model.subClasses.each() { subClass ->
              uml.append(subClass).append("\n")
          }
        }

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
     
   }
