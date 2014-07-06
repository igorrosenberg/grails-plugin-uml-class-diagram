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
  plantUmlService.domain()
  }

}    
