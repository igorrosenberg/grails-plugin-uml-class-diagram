class ToUmlGrailsPlugin {
    def version = "0.2.3"
    def grailsVersion = "2.0 > *"
    def pluginExcludes = [
        "web-app/**"
    ]

    def title = "To UML Plugin"
    def author = "Igor Rosenberg, based on work by Ignacio Ocampo Millan"
    def authorEmail = "Ignacio <nafiux@gmail.com>"
    def description = 'Automagically create a UML diagram from Domain, Controller and Service classes'
    def documentation = "http://grails.org/plugin/class-domain-uml"

    def license = "APACHE"
    def organization = [ name: "Nafiux", url: "http://www.nafiux.com/" ]
    def issueManagement = [ system: "JIRA", url: "https://github.com/nafiux/grais-plugin-class-domain-uml/issues" ]
    def scm = [ url: "https://github.com/nafiux/grais-plugin-class-domain-uml" ]
}
