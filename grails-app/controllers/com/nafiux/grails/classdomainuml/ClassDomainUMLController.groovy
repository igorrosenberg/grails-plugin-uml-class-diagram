package com.nafiux.grails.classdomainuml

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class ClassDomainUMLController {

    def index() {
        def packages = [:]
        def relations = []
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
            }
            uml.append("}\n") // Package end
        }
        for(r in relations) {
            uml.append(r).append("\n")
        }

        uml.append("""
title ${grailsApplication.metadata.'app.name'} - ${grailsApplication.metadata.'app.version'}
legend left
  Powered by Nafiux (nafiux.com)
  Grails version: ${grailsApplication.metadata.'app.grails.version'}
endlegend
""")

        render "<img src='http://www.plantuml.com/plantuml/img/${compressAndEncodeString(uml.toString())}' />"
    }
    
    def compressAndEncodeString(String str) {
        byte[] xmlBytes = str.getBytes("UTF-8")

        byte[] compressed = new CompressionZlib().compress(xmlBytes)

        return new AsciiEncoder().encode(compressed)
    }

}
