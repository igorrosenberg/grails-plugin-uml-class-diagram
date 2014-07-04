grails-plugin-class-domain-uml
=============================

Generate UML diagrams from your Grails app source code.

## Features
  1. [TODO] Generate Class diagram specs for Controllers & Services (only public methods, no javadoc comments)
  1. [TODO] Generate Dependency diagram specs including Controllers/Services/Spring beans
  1. [TODO] Dependency diagrams showing layers for Controllers/Services/Spring beans
  1. [TODO] Diagram generation in PNG via plantUML.jar from [PlantUML project](http://plantuml.sourceforge.net/)
  1. [TODO] Expose diagrams as a specific UmlController 
  1. [TODO] Expose diagrams as a grails script: "grails to-uml"
  1. [TODO] Draw Class diagrams for Domain  
  1. [TODO] Use online [PlantUML server](http://www.plantuml.com/plantuml) (ie no need to rely on plantUML.jar) 
  1. [TODO] Use online [yUML](http://www.yuml.me/diagram/scruffy/class/draw) (different syntax)
  1. [TODO] Inclusion in standard gdoc process
  1. [TODO] document specificly webapp interfaces (public methods of Controllers, with javadoc, input/output spec?) 
  
## Finished dev tasks  
  * None
  
## Ongoing dev tasks  
* None

## Future dev tasks  
  * Refactor script / controller / service
  * grails introspect 
      ** controllers >> list dependencies
      ** services >> list dependencies
      ** domains >> list dependencies (hasmany)
      ** controllers, services >> public methods
      ** domain >> all fields not external (ie excluding hasmany)
   * plantUml Class diagram spec (write non trivial example)
   * plantUml Dependency diagram spec (write non trivial example)
   * introspection result to diagram spec
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

-Add `runtime ":class-domain-uml:0.1.5"` to **BuildConfig.groovy** in plugins section (requires grails 2.0 > *).-

## Usage

-Run your app and navigate to `http://localhost:8080/yourApp/uml`-

## Screenshots

![Example 2](web-app/images/class-domain-uml-screenshot-2.png)

![Example 1](web-app/images/class-domain-uml-screenshot-1.png)
