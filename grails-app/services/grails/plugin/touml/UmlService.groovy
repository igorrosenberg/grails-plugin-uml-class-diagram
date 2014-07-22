package grails.plugin.touml

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate UML diagrams from grails classes.
 */
class UmlService {

   static final ARTEFACTS  = 
   [
   Controller:   
      'defaultAction,instanceControllerTagLibraryApi,controllerUri,instanceControllerTagLibraryApi,session,servletContext,controllerClass,response,controllerName,webRequest,assetProcessorService,grailsAttributes,instanceControllersRestApi,applicationContext,flash,class,actionName,actionUri,modelAndView,pluginContextPath,errors,instanceControllersApi,params,chainModel,grailsApplication,request,controllerNamespace',
    Service:'class'
    ].collectEntries{ k, v -> [k, v.split(',')] }

   def grailsApplication
   
   def plantUmlService
   
   def yUmlService
   
  /**
  * Expose UML. 
  * @return a String to be read as a URL to an online rendering service.
  */
  String generate(config) {
     if (config.diagramType == DiagramType.DOMAIN) {
       domain(config)
     } else {
        layers(config)
     }

  }

  /**
  *  @param packages a map of <packageNames,List<DomainClass>>   
  * TODO heavy optimization probably possible here
  */
   private customize(packages, config) {
   
   packages.keySet().each {  packageName ->
      def classList  = packages[packageName] 
      
      // Filter fields based on regexps
      classList.each { classData ->
        classData.properties = classData.properties.findAll { propName ->
          for (regexp in config.fieldFilterRegexps) {
            if(propName.name.matches(regexp)) {
              return false
              }
          }
          return true
          }
      }
      
      // Filter classes based on regexps
      classList = classList.findAll{ classData ->
           // OPTIMIZE as return ! config.classFilterRegexps.find {regexp  -> classData.className.matches(regexp) }
          for (regexp in config.classFilterRegexps) {
            if(classData.className.matches(regexp)) {
              return false
              }
          }
          return true
          }

      // Also Filter (based on regexps) classes referenced in the associations 
      classList.each { classData ->
        classData.associations = classData.associations.findAll { association ->
          for (regexp in config.classFilterRegexps) {   
            println "FILTERING " + association.typeName + " AND " + association.modelName+ " WITH " + regexp
            if( association.typeName.matches(regexp)  || association.modelName.matches(regexp) ) {
              return false
              }
          }
          return true
          }
      }
          

      // shorten all Java class names  
      if (!config.showCanonicalJavaClassNames) {
        
        classList.each { classData ->
          classData.properties.collect { propName ->
            'java.lang,java.util,java.io'.split(',').each {
              propName.type =  propName.type.replaceAll("^$it\\.",'') 
            }         
          }    
        }
      }

      packages[packageName]  = classList 
   }
   
   }
  
  /**
  * @return a String to be read as a URL to an online rendering service
  */
  String domain (config) {
    def packages = [:]             // a map of <packageNames,List<DomainClass>>
    for (model in grailsApplication.domainClasses ){
        def packageName = model.getPackageName()
        if (!packages[packageName]) { 
          packages[packageName] = []
          }
        packages[packageName].add(extractDomainData(model,packageName))
    }
    
    customize(packages, config)
    
    plantUmlService.asUml(packages)
  }
  
  /**
  * Expose internal layered architecture (controllers and services).
  * @return a String to be read as a URL to an online rendering service
  */
  String layers(config) {
    def artefacts = [:]     // a map of <artefactType, mapAsBelow>
    ARTEFACTS.each {  artefactType, exclusionList ->
      def packages = [:]             // a map of <packageNames, List<Artefacts>>
      for (model in grailsApplication.getArtefacts(artefactType)){
          def packageName = model.getPackageName()
          if (!packages[packageName]) { 
            packages[packageName] = []
            }
          packages[packageName].add(extractArtefactData(model,packageName, exclusionList))
      }
      artefacts[artefactType] = packages
    }
    
    artefacts.each {artefactType, packages ->
      customize(packages, config)
    }
    

    plantUmlService.asUmlLayers(artefacts)
    }
  
  /**
  * @return data exposed by the artefact as a map (className, properties, associations)
  */
  private extractArtefactData(model,packageName, exclusionList){
     log.debug "Artefact data: " + model.name       
     def properties = model.getReferenceInstance().properties.keySet() 
     exclusionList.each { properties -= it }
     [
     className: model.fullName, 
     properties: properties,
     associations: [],
      ]
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
      ]
      }


}    
