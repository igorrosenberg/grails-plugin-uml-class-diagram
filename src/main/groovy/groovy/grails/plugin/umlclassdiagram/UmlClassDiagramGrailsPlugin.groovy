package umlclassdiagram

import grails.plugins.*

class UmlClassDiagramGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def profiles = ['web']

    def title = "Uml Class Diagram" // Headline display name of the plugin
    def author = "Igor Rosenberg"
    def description = 'Generate UML class diagrams from your Grails app source code.'
    def documentation = "http://grails.org/plugin/uml-class-diagram"
	def license = "APACHE"
    def issueManagement = [ system: 'github', url: "https://github.com/igorrosenberg/grails-plugin-uml-class-diagram/issues" ]
    def scm = [ url: "https://github.com/igorrosenberg/grails-plugin-uml-class-diagram/" ]

}
