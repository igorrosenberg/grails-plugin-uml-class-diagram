package grails.plugin.touml

class UmlController {

    static defaultAction = "domain"
    
    def umlService
         
    def domain() {
        def umlURL = umlService.domain()
        render "<img src='$umlURL' />"
    }

    def layers() {
        def umlURL = umlService.layers()
        render "<img src='$umlURL' />"
    }
    
    // FIXME add other methods pointing to yUML
    
}
