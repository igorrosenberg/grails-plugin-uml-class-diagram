
Ant.property(environment:"env")                             
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

includeTargets << grailsScript("Init")
includeTargets << new File ( "${grailsHome}/scripts/Bootstrap.groovy" )

target(toUml: "Generate UML diagrams from code") {
    depends(clean,compile,loadApp)
    def umlService = .... // how do I get an instance ?
    umlService.generate2()
}

setDefaultTarget(toUml)
