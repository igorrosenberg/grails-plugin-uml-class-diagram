package grails.plugin.touml

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate UML diagrams from grails classes.
 */

class UmlService {

   def grailsApplication
   
   def plantUmlService
   
   def yUmlService
  
  /**
  * @return a String to be read as a URL to an online rendering service
  */
  String domain () {
    def packages = [:]             // a map of <packageNames,List<DomainClass>>
    for (model in grailsApplication.domainClasses ){
        def packageName = model.getPackageName()
        if (!packages[packageName]) { 
          packages[packageName] = []
          }
        packages[packageName].add(extractDomainData(model,packageName))
    }
    plantUmlService.asUML(packages)
  }

     /**
     * @return List<map(propertyName, propertyType)>
     */
     private propertiesToSpec(properties) {
     	 log.debug "Properties: ${properties}"
       properties.collect { 
          def name = it.getName()
          def type =  it.getType().toString().replaceAll("class ", "")
          [name:name, type:type]
        }
      }

     /**
     * @return List<(modelName, left, type, right, typeName,assocName)>
     */
      private associationsToSpec(modelName, associations) {
        associations.collect {
            log.debug "Association for $modelName: ${it}"
            def data  = [modelName: modelName, type: 'o--']
            if(it.isManyToOne()) {
                data.left = '"*"'
                data.right = '"1"'
            } else if(it.isOneToMany()) {
                data.left = '"1"'
                data.right = '"*"'
            } else if(it.isOneToOne()) {
                data.left = '"1"'
                data.right = '"1"'
            } else if(it.isManyToMany()) {
                data.left = '"*"'
                data.right = '"*"'
            } else if(it.isEmbedded()) {
                data.left = ''
                data.right = ''
                data.type = "*--"
            } else {
                data.left = ''
                data.right = ''
            }
            data.typeName = it.getType().name 
            data.assocName = it.getName()
            
            data
        }
      }
    
     private subClassesToSpec (fullName, p) {
				log.warn "subClassesToSpec BUGGED"
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
    private extractDomainData(model, p) {
      log.debug "Extracting data from ${model.fullName.getClass()}"
      def c = grailsApplication.classLoader.loadClass("${model.fullName}")
      // FIXME really need an object  ? already got a class...
      def instance = new DefaultGrailsDomainClass(c)      
      [
      className: model.fullName,
      properties: propertiesToSpec(instance.getProperties()),
      associations: associationsToSpec(model.fullName, instance.getAssociations()),
      subClasses: subClassesToSpec (model.fullName, p),
      ]
      }


}    
