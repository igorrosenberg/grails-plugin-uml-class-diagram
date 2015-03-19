package grails.plugin.umlclassdiagram.generator

import grails.plugin.umlclassdiagram.*

/**
 * Generate models.
 */
class ModelGeneratorService {

   def domainModelGeneratorService
   def layersModelGeneratorService
   def db2ModelGeneratorService

   /**
    * Generate the model of the underlying data. 
    * @return (package x class x field + links).
    */ 
   def makeModel(diagramType) {
      def modelGenerator
      switch (diagramType) {
        case DiagramType.DOMAIN: 
          modelGenerator = domainModelGeneratorService
          break 
        case DiagramType.LAYERS: 
          modelGenerator = layersModelGeneratorService
          break 
        case DiagramType.DB2: 
          modelGenerator = db2ModelGeneratorService
          break 
        default: 
          throw new IOException("DiagramType ${diagramType} not implemented")        
      }
      modelGenerator.makeModel()      
   }
  
}    
