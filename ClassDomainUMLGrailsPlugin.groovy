class ClassDomainUMLGrailsPlugin {
    def version = "0.1.5"
    def grailsVersion = "2.0 > *"
    def pluginExcludes = [
        "web-app/**"
    ]

    def title = "Class Domain UML Plugin"
    def author = "Ignacio Ocampo Millan"
    def authorEmail = "nafiux@gmail.com"
    def description = 'Automagically create a UML diagram from all your domain classes of your Grails app'
    def documentation = "http://grails.org/plugin/class-domain-uml"

    def license = "APACHE"
    def organization = [ name: "Nafiux", url: "http://www.nafiux.com/" ]
    def issueManagement = [ system: "JIRA", url: "https://github.com/nafiux/grais-plugin-class-domain-uml/issues" ]
    def scm = [ url: "https://github.com/nafiux/grais-plugin-class-domain-uml" ]
}
