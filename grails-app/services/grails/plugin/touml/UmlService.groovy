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
  * @param classList  List<ClassData> 
  * TODO heavy optimization probably possible here
  */
   private customize(classList, config) {
   
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
          }
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

    String getTypeOld(beanName) { 
    log.debug "Hey info for $beanName - " 
    
    // def beans = grailsApplication.mainContext.beanDefinitionNames
    // log.debug "bean Names: ${beans} ..."

    def count = 0 
    def cc = 0
    grailsApplication.mainContext.beanDefinitionNames.each {if (it =~ /.*${beanName}.*/) log.info"Found bean: $it" ; else count ++ }
    log.debug "not matched beans: $count" 
    if (grailsApplication.mainContext.beanDefinitionNames.contains(beanName)) {
      def b = grailsApplication.mainContext.getBean(beanName).getClass()
      b.superclass.canonicalName    
    } else {
    log.debug "------not matched bean: $beanName" 
    beanName.capitalize()
    }
    
     //def beans = grailsApplication.mainContext.beanDefinitionNames
//println "beans: ${beans[0..2]} ..."
//beans.each {if (it =~ /.*Service/) println it ; else System.out.print '.' }
    //println 'UmlController Bean: ' + grailsApplication.getMainContext().getBean('grails.plugin.touml.UmlController').getClass()
    /*
    println beanName + ' Bean: ' + b
    println beanName + " getSuperclass()  " +  b.getSuperclass() 
    println beanName + " getFields()   " +  b.getFields() 
    
    // println b.properties.keySet()
    // println b.declaredFields
    //println grailsApplication.getMainContext().getBeanDefinitionNames().findAll{  
     // it =~ /.*Controller/ || it =~ /.*Service/  
     // }
     */      
    }
    
    String getType(beanName) { 
        // FIXME sometimes artefact.propertyName is capitalized, sometimes not ?!
        def type 
        beanName = beanName.capitalize()
        if (beanName =~ /.*Service/) {
           def serviceArtefact = grailsApplication.getArtefacts('Service').find{ a ->              
              a.propertyName.capitalize() == beanName
            }
            if (serviceArtefact) {
               log.debug "   --- $beanName has matching artefact: $serviceArtefact" 
              type =  serviceArtefact.fullName
              }
        }
        
        type  = type ?: beanName
        log.debug "Hey type for $beanName : $type " 
        type
    }
  /**
  * Expose UML. 
  * @return a String to be read as a URL to an online rendering service.
  */
  String generate(config) {
      println "TESTING: " + getType('yUmlService')
      println "TESTING: " + getType('plantUmlService')
      grailsApplication.getArtefacts('Service').each { a ->
      log.trace '' + 
      "\n a.fullName "+ a.fullName + 
      "\n a.logicalPropertyName "+ a.logicalPropertyName + 
      "\n a.propertyName "+ a.propertyName + 
      "\n a.shortName "+ a.shortName 
      } 
  /*
      println "artefact: " + grailsApplication. getArtefactByLogicalPropertyName('Service', 'yUml' )
      println "artefact Contr: " + grailsApplication.getArtefactByLogicalPropertyName('Controller', 'uml' )
      println "artefact umlService: " + grailsApplication.mainContext.getBean("umlService"); 
      println "Service artefacts " + grailsApplication.getArtefacts('Service')
      grailsApplication.getArtefacts('Service').each { a ->
      log.info '' + 
      "\n a.fullName "+ a.fullName + 
      "\n a.logicalPropertyName "+ a.logicalPropertyName + 
      "\n a.propertyName "+ a.propertyName + 
      "\n a.shortName "+ a.shortName 
      }
      
      println "artefact yUmlService: " + getType("yUmlService"); 
      return '0'
      */
      //println "TESTING: " + getType('grailsApplication')      
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
    
    plantUmlService.asUml(map)
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
  
    println 'beans # ' + grailsApplication.getMainContext(). getBeanDefinitionNames().size()
    /*
    println 'contr ' + grailsApplication.getMainContext().getBean('grails.plugin.touml.UmlController').getClass()
    println 'contr ' + grailsApplication.getMainContext().getBean('toUmlUmlService').getClass()
    println grailsApplication.getMainContext().getBeanDefinitionNames().findAll{  
      it =~ /.*Controller/ || it =~ /.*Service/ 
    throw new IOException('no')
    }*/

    
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
     // maybe need to get the bean instance via grailsApplication.getMainContext().getBean() 
     
     def data = 
     [
     packageName: model.packageName,
     className: model.fullName, 
     properties: propertiesToSpec3(model,exclusionList),
     associations: [],
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
            // [name: k , type: (v ? v.getClass().getCanonicalName() : k.capitalize() ) ]
            // Why so many NullObject ?
            [name: k , type: v ? v.getClass().canonicalName : getType(k)]
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
