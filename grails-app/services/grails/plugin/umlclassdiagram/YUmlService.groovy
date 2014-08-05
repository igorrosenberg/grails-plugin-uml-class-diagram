package grails.plugin.umlclassdiagram

class YUmlService { 

    static fileName = 'DomainUML.html'
    static url = 'http://yuml.me'

    /**
     * Generate yUML syntax for class diagrams from Grails Domain classes.
     * Currently creates an HTML file, containing links to online service.
     * @see http://www.yuml.me/ 
     */
    private void generate2(){
      def domainClasses = grailsApplication.domainClasses
        def classes = ''
        def relationships = ''
        domainClasses.each { domainClass ->
            def relations = ''
            def classDef = ''
            domainClass.properties.each{prop ->
                if(prop.name != 'id' && prop.name != 'version'){
                    if (prop.isAssociation()){
                        // if its association only show the do the owning side
                        if(!prop.isBidirectional() || prop.isOwningSide())
                            relations += getRelationship(domainClass.name, prop)
                    } else {
                        classDef += resolveName(prop.getType().getName()) + ' ' + prop.name + ';'
                    }
                }
            }
            classDef = (classDef == "") ? '' : '|' + classDef
            classDef += "[${domainClass.name}${classDef}],"
            classes += classDef
            relationships += relations
        }
        createFile(classes + relationships)
    }

    private String getRelationship(name, prop){

        def association = ''
        if (prop.isOneToMany()){
            association = prop.isOptional() ? '1-0..*>':'1-1..*>'
        } else if (prop.isOneToOne()){
            association = prop.isOptional() ? '1-0..1>':'1-1>'
        } else if (prop.isManyToMany()){
            association = prop.isOptional() ? '*-*>':'1..*-1..*>'
        }
        if(prop.isBidirectional()){
            association = '<' + association
        }
        "[${name}]${association}[${resolveName(prop.getReferencedPropertyType().getName())}],"
    }


    private resolveName(def name){
        // remove bracket if an array
        if(name.lastIndexOf('[') > -1){
            name = name.replace('[','');
        }
        // remove package name
        if (name.lastIndexOf('.') > -1){
            return name.substring(name.lastIndexOf('.')+1)
        }
        return name
    }

    private void createFile(umlStuff){
        def scruffyURL = url + "/diagram/scruffy/class/" +umlStuff
        def﻿ orderedURL = url + "/diagram/class/" +umlStuff
        def﻿ orderedRLURL = url + "/diagram/dir:rl/class/" +umlStuff
        def﻿ scruffyRLURL = url + "/diagram/scruffy;dir:rl/class/" +umlStuff
        def contentsHTML = """
    <html>
    <body>
    <p>
    Click one of these to open a UML Class diagram for your Domain.
    Then right click and save the image</p>
    <p>
    <ul>
    <li> <a href='${scruffyURL}'>Top down</a> </li>
    <li> <a href='${orderedURL}'>Neat - top down</a> </li>
    <li> <a href='${scruffyRLURL}'>Right left</a> </li>
    <li> <a href='${orderedRLURL}'>Neat - right to left</a> </li>
    </ul>
    </body>
    </html>
    """
        File f = new File(fileName)
        f.write(contentsHTML)
        log.debug "Created file ${f.name}"
    }
}