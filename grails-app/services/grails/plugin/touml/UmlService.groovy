package grails.plugin.touml

import com.nafiux.grails.classdomainuml.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * Generate UML diagrams from grails classes.
 */

class UmlService {

   def grailsApplication
   
    /**
    * Zlib compression (compatible plantUML).
    */
    private String compressAndEncodeString(String str) {
        byte[] xmlBytes = str.getBytes("UTF-8")
        byte[] compressed = new CompressionZlib().compress(xmlBytes)
        return new AsciiEncoder().encode(compressed)
    }

    /**
     * Generate custom URL to online plantUML service.
     */
    String umlService.domainUml() {
        def packages = [:]
        def relations = []
		def subClases = []

        // List of packages and classes
        for (model in grailsApplication.domainClasses) {
            if (!packages[model.getPackageName()]) packages[model.getPackageName()] = []
            packages[model.getPackageName()].add(model)
        }
        StringBuilder uml = new StringBuilder()
        // Packages
        for (p in packages) {
            uml.append("package ").append(p.getKey()).append(" <<Rect>> {\n")
            // Each model of package
            for(model in p.getValue()) {
                def c = grailsApplication.classLoader.loadClass("${model.fullName}")
                // FIXME really need an object  ?already got a class...
                def instance = new DefaultGrailsDomainClass(c)
                uml.append("class ").append(model.getFullName()).append(" {\n") // Class start
                // Properties
                instance.getProperties().each {
                    uml.append(" ").append(it.getName()).append(": ").append(it.getType().toString().replaceAll("class ", "")).append("\n")
                }
                // Associations
                instance.getAssociations().each {
					log.debug "Associations: ${it}"
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
                    relations.add(model.getFullName() + ' ' + left + ' ' + type + ' ' + right +' ' + it.getType().name + " : " + it.getName())
                }
                uml.append("}\n") // Class end

				// Subclasses
				def coponentBaseDomain = grailsApplication.domainClasses.find { it.name == model.getName()}
				log.debug coponentBaseDomain.hasSubClasses()
				def symbol = "<|--" 
				def componentTypes = coponentBaseDomain.getSubClasses().each { 
						  				subClases.add("${model.getFullName() + ' ' + symbol + ' ' + p.getKey() + '.' +it.name }")
										log.debug "Inherit: ${subClases}"
				                  	}	

            }
            uml.append("}\n") // Package end
        }
        for(r in relations) {
            uml.append(r).append("\n")
        }
		for(s in subClases) {
            uml.append(s).append("\n")
        }

        uml.append("""
		title ${grailsApplication.metadata.'app.name'} - ${grailsApplication.metadata.'app.version'}
		legend left
  		PPPowered by Nafiux (nafiux.com)
  		Grails version: ${grailsApplication.metadata.'app.grails.version'}
		endlegend
		""")
		log.debug "UML: ${uml.toString()}"
		log.debug "subClases: ${subClases.toString()}"		
		compressAndEncodeString(uml.toString())
}    

static fileName = 'DomainUML.html'
static url = 'http://yuml.me'


/**
 * Generate yUML syntax for class diagrams from Grails Domain classes.
 * @see http://www.yuml.me/ 
 */
private void generate2(){
  def domainClasses = grailsApplication.domainClasses
    def classes = ''
    def relationships = ''
    domainClasses.each { domainClass ->
        def relations = ''
﻿  def classDef = ""
﻿  domainClass.properties.each{prop ->
            if(prop.name != 'id' && prop.name != 'version'){
                if (prop.isAssociation()){
                    // if its association only show the do the owning side
                    if(!prop.isBidirectional() || prop.isOwningSide())
                        relations += getRelationship(domainClass.name, prop)
                } else {
                    classDef += resolveName(prop.getType().getName()) + ' ' + prop.name + ';'
                }
            }
        }
        classDef = (classDef == "") ? '' : '|' + classDef
        classDef += "[${domainClass.name}${classDef}],"
        classes += classDef
        relationships += relations
    }
    createFile(classes + relationships)
}

private String getRelationship(name, prop){

    def association = ''
    if (prop.isOneToMany()){
        association = prop.isOptional() ? '1-0..*>':'1-1..*>'
    } else if (prop.isOneToOne()){
        association = prop.isOptional() ? '1-0..1>':'1-1>'
    } else if (prop.isManyToMany()){
        association = prop.isOptional() ? '*-*>':'1..*-1..*>'
    }
    if(prop.isBidirectional()){
        association = '<' + association
    }
    "[${name}]${association}[${resolveName(prop.getReferencedPropertyType().getName())}],"
}


private resolveName(def name){
    // remove bracket if an array
    if(name.lastIndexOf('[') > -1){
        name = name.replace('[','');
    }
    // remove package name
    if (name.lastIndexOf('.') > -1){
        return name.substring(name.lastIndexOf('.')+1)
    }
    return name
}

private void createFile(umlStuff){
    def scruffyURL = url + "/diagram/scruffy/class/" +umlStuff
    def﻿ orderedURL = url + "/diagram/class/" +umlStuff
    def﻿ orderedRLURL = url + "/diagram/dir:rl/class/" +umlStuff
    def﻿ scruffyRLURL = url + "/diagram/scruffy;dir:rl/class/" +umlStuff
    def contentsHTML = """
<html>
<body>
<p>
Click one of these to open a UML Class diagram for your Domain.
Then right click and save the image</p>
<p>
<ul>
<li> <a href='${scruffyURL}'>Top down</a> </li>
<li> <a href='${orderedURL}'>Neat - top down</a> </li>
<li> <a href='${scruffyRLURL}'>Right left</a> </li>
<li> <a href='${orderedRLURL}'>Neat - right to left</a> </li>
</ul>
</body>
</html>
"""
    File f = new File(fileName)
    f.write(contentsHTML)
    log.debug "Created file ${f.name}"
}

}    
