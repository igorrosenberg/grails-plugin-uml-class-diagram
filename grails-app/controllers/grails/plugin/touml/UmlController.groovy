package grails.plugin.touml

class UmlController {

    static defaultAction = "domain"
    
    def umlService
         
    def domain() {
        def umlURL = umlService.domain()
        redirect url:umlURL
    }

    def layers() {
        def umlURL = umlService.layers()
        redirect url:umlURL
    }
    
}
