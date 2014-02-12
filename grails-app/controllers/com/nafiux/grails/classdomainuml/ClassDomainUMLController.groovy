package com.nafiux.grails.classdomainuml

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class ClassDomainUMLController {

    def index() {
        def packages = [:]
        def relations = []
		def subClases = []

		log.info "in ClassDomainUMLController, index"

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
                def instance = new DefaultGrailsDomainClass(c)
                uml.append("class ").append(model.getFullName()).append(" {\n") // Class start
                // Properties
                instance.getProperties().each {
                    uml.append(" ").append(it.getName()).append(": ").append(it.getType().toString().replaceAll("class ", "")).append("\n")
                }
                // Associations
                instance.getAssociations().each {
					log.info "Asociaciones: ${it}"
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
				println coponentBaseDomain.hasSubClasses()
				def symbol = "<|--" 
				def componentTypes = coponentBaseDomain.getSubClasses().each { 
						  				subClases.add("${model.getFullName() + ' ' + symbol + ' ' + p.getKey() + '.' +it.name }")
										log.info "Herencia: ${subClases}"
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
		
        render "<img src='http://www.plantuml.com/plantuml/img/${compressAndEncodeString(uml.toString())}' />"
		log.info "UML: ${uml.toString()}"
		log.info "subClases: ${subClases.toString()}"
		
		
    }
    
    def compressAndEncodeString(String str) {
        byte[] xmlBytes = str.getBytes("UTF-8")

        byte[] compressed = new CompressionZlib().compress(xmlBytes)

        return new AsciiEncoder().encode(compressed)
    }

}
