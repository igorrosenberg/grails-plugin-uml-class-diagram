package grails.plugin.umlclassdiagram.generator

import static grails.plugin.umlclassdiagram.Constants.*

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate UML diagrams from grails domains.
 */
class DomainModelGeneratorService {

   def grailsApplication
   
   def plantUmlService
   
   def yUmlService

  /**
  * Expose UML.   
  * @return a redirection URL.
  */
  def redirect(config) {
    def map = getPackageMap(config)
     def redirectUrl
     switch (config.renderingEngine) {
         case RenderingEngine.LOCAL_PLANT_UML: 
            def umlSpec = plantUmlService.asUmlSpec(map)
            redirectUrl  = "/uml/localPlantUml/$umlSpec"
            break
         case RenderingEngine.WEB_PLANT_UML: 
            def umlSpec = plantUmlService.asUmlSpec(map)
            redirectUrl = plantUmlService.asWebUrl(umlSpec)
            break
         default: 
            throw new IOException("${config.renderingEngine} not implemented")  
      }
  }  

 
  /**
  * @param classList  List<ClassData> 
  * TODO heavy optimization probably possible here
  */
   private customize(classList, config) {
   
      if (!config.showGrailsInternalClasses) {      
        // add grails internals to list of regexps 
	// TODO could be optimized via groovy-ninjutsu	
	config.classFilterRegexps = config.classFilterRegexps ?: []
	for (filter in GRAILS_ARTEFACTS)		
		config.classFilterRegexps += filter
      }      

      // Filter classes based on regexps
      classList = classList.findAll { classData ->
        // OPTIMIZE as return ! config.classFilterRegexps.find {regexp  -> classData.className.matches(regexp) }
        for (regexp in config.classFilterRegexps) {
          if(classData.className.matches(regexp)) {
            return false
          }
        }
        return true
      }

      // Also remove these classes when referenced in the associations (reusing previous regexps)
      classList.each { classData ->
        classData.associations = classData.associations.findAll { association ->
          for (regexp in config.classFilterRegexps) {   
            log.debug "Filtering ${association.modelName} --> ${association.typeName} with $regexp"
            if ( association.typeName.matches(regexp) || association.modelName.matches(regexp) ) {
              return false
              }
          }
          return true
          }
      }
      
      // Filter fields based on regexps
      classList.each { classData ->
        classData.properties = classData.properties.findAll { property ->
          for (regexp in config.fieldFilterRegexps) {
            if(property.name.matches(regexp)) {
              return false
              }
          }
          return true
          }.sort {it.name}
      }
      
      // shorten all Java class names  
      if (!config.showCanonicalJavaClassNames) {
        classList.each { classData ->
          classData.properties.collect { property ->
            'java.lang,java.util,java.io'.split(',').each {
              property.type =  property.type.replaceAll("^$it\\.",'') 
            }         
          }    
        }
      }
      
      classList
   
   }

    /**
    * @return String, the (possibly) fully qualified class name of the bean.
    */
    private getType(beanName) { 
        // FIXME sometimes artefact.propertyName is capitalized, sometimes not ?!
        def type = UNKNOWN 
        beanName = beanName.capitalize()
        if (beanName =~ /.*Service/) {
           def serviceArtefact = grailsApplication.getArtefacts('Service').find{ it ->
              it.propertyName.capitalize() == beanName
            }
            if (serviceArtefact) {
              log.debug "   --- $beanName has matching artefact: $serviceArtefact" 
              type =  serviceArtefact.fullName
              }
        }
        
        log.debug "Type for $beanName : $type"
        type
    } 
    
   /**
    * Find all classes to expose in diagram. 
    * @return a Map<packageName, listClasses>.
    */   
  private getPackageMap(config) {
    def listClasses     // a List<ClassData>
    if (config.diagramType == DiagramType.DOMAIN)
      listClasses = getDomain(config) 
    else       
      listClasses = getControllersAndServices(config)     
    
    listClasses = customize(listClasses, config)
    def map = [:]
    listClasses.each{
        def packageName = it.packageName ?: '_'
        if (!map[packageName]) {
          map[packageName] = []
          }
        map[packageName].add(it)
    }
    map
  }
    
  /**
  * Introspect all domain classes. 
  * @return a List<ClassData> 
  */
  private getDomain(config) {
    grailsApplication.domainClasses.collect {
      extractDomainData(it)
    }
  }

  /**
  * Introspect the internal layered architecture (controllers and services).
  * @return a List<ClassData> 
  */
  private getControllersAndServices(config) {
  
    ARTEFACTS.collect {  artefactType, exclusionList ->
      if (!config.filterGrailsFields) 
        exclusionList = []
        grailsApplication.getArtefacts(artefactType).collect{
          extractArtefactData(it, exclusionList)
      }
    }.flatten()
    
    }
  
  /**
  *  Introspect an Artefact.
  * @return useful data in a ClassData map (className, properties, associations).
  */
  private extractArtefactData(model, exclusionList){
     log.debug "Introspect artefact data for bean=" + model.name
     
     def data = 
     [
     packageName: model.packageName,
     className: model.fullName, 
     properties: propertiesToSpec3(model,exclusionList),
     associations: associationsToSpec3(model,exclusionList),
      ]
      log.debug "artefact properties: ${data.properties}"
      data
  }
     /**
     * @return List<map(propertyName, propertyType)>
     */
     private propertiesToSpec3(model, exclusionList){
        def properties = model.getReferenceInstance().properties.findAll { k, v -> ! (k in exclusionList)}
        properties.collect { k,v ->
            [name: k , type: v ? v.getClass().canonicalName.replaceAll('\\$.*$', '') : getType(k)]
        }
     }
  
     /**
     * @return List<(modelName, left, type, right, typeName,assocName)>
     */
      private associationsToSpec3(model, exclusionList){
        def properties = model.getReferenceInstance().properties.findAll { k, v -> ! (k in exclusionList)}
        properties.collect { k,v ->
            def type = getType(k)
            def data  = [type: 'o--', left: '', right:'"1"', 
                modelName:  model.fullName, typeName: type.replaceAll('\\$.*$', ''), assocName: k] 
            log.debug "Association ${data}"
            data
        }. findAll {
          // also add conditional filter here
          it.typeName  != UNKNOWN
        }
      }
  
     /**
     * @return List<map(propertyName, propertyType)>
     */
     private propertiesToSpec(properties) {
     	 log.debug "Properties: ${properties}"
       properties.collect { 
          def name = it.getName()
          
          def type =  it.getType().name
          def ref = it.getReferencedPropertyType().name 
          if (type != ref) {
                type = "${type}<${ref}>"
            }
            
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
            data.typeName = it.getReferencedPropertyType().name 
            data.assocName = it.getName()
            
            data
        }
      }
    
    /**
     *  Introspect a domain class.
     * @return useful data in a ClassData map (className, properties, associations).
     */
    private extractDomainData(model) {
      def c = grailsApplication.classLoader.loadClass("${model.fullName}")
      log.debug "Introspect artefact data for ${model.fullName}"
      // FIXME really need an object ? already got a class...
      def instance = new DefaultGrailsDomainClass(c)      
      [
      packageName: model.packageName,
      className: model.fullName,
      properties: propertiesToSpec(instance.getProperties()),
      associations: associationsToSpec(model.fullName, instance.getAssociations()),
      ]
      }

}    
