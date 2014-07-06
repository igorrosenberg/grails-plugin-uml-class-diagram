grails-plugin-to-uml
=============================

Generate UML diagrams from your Grails app source code.

## Features
  1. [TODO] Global Class diagrams (layered) for Controllers & Services & other beans (only public methods, no javadoc comments)
  1. [TODO] Global Class diagrams for Domain  
  1. [TODO] Diagram generation in PNG via plantUML.jar from [PlantUML project](http://plantuml.sourceforge.net/)
  1. [TODO] Diagrams exposed as specific UmlController (http interface)
  1. [TODO] Diagrams exposed as a grails script: "grails to-uml" (cli interface)
  1. [TODO] HTML summary page using online [PlantUML server](http://www.plantuml.com/plantuml) (ie no need to rely on plantUML.jar - no local image generation) 
  1. [TODO] HTML summary page using online [yUML](http://www.yuml.me/diagram/scruffy/class/draw) (different syntax)
  1. [TODO] Inclusion in standard gdoc process
  1. [TODO] document specifically webapp interfaces (public methods of Controllers, with javadoc, input/output spec?) 
  
## Finished dev tasks  
* None
  
## Ongoing dev tasks  
Started refactoring previous work - perimeter: 
* (Igor) Refactor script / controller / service (UmlController | UmlService | PlantUmlService |YumlService)
* (Igor) domains >> list dependencies (hasmany)
* (Igor) domain >> all fields not external (ie excluding hasmany)
* domain introspection result to diagram spec

## Future dev tasks  
* grails introspect 
** controllers >> list dependencies
** services >> list dependencies
** controllers, services >> public methods
** introspection result to diagram spec
* plantUml Class diagram spec (write non trivial example)
* plantUml Dependency diagram spec (write non trivial example)
* plantUml diagram spec to PNG file
* plantUml diagram spec to PNG byteStream  
* script : controller/services/domains >> to PNG files
* UmlController : html file containing 1 link to 1 generated PNG file
* UmlController : controller/services/domains >> to html file containing links to generated PNG files
* Config options
* gdoc inclusion

  
## Inspiration from 
* http://grails.org/plugin/class-domain-uml
* http://www.grails.org/plugin/create-domain-uml
* https://github.com/david-w-millar/grails-plantuml-plugin
* https://code.google.com/p/grails-domain-uml/source/browse/#svn%2FCreateDomainUml

## Installation

[TODO]
Add `runtime ":to-uml:0.1"` to **BuildConfig.groovy** in plugins section (requires grails 2.0 > *).

## Usage

Run your app and navigate to `http://localhost:8080/yourApp/uml`

## Screenshots

![Example 2](web-app/images/class-domain-uml-screenshot-2.png)

![Example 1](web-app/images/class-domain-uml-screenshot-1.png)
