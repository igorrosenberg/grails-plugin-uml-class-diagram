package grails.plugin.touml

class UmlController {

    def umlService
    
    def domain() {
        def umlString = umlService.domainUml()
        render "<img src='http://www.plantuml.com/plantuml/img/$umlString' />"
    }
    
    // FIXME add the other method pointing to yUML
    
}
