package grails.plugin.touml

class UmlController {

    def umlService
    
    def domain() {
        def umlURL = umlService.domain()
        render "<img src='$umlURL' />"
    }

    def architecture() {
        def umlURL = umlService.domain()
        render "<img src='$umlURL' />"
    }
    
    // FIXME add the other method pointing to yUML
    
}
