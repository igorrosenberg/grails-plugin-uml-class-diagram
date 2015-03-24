package grails.plugin.umlclassdiagram.generator

/**
 * Generate models from DB2 files.
 */
class Db2ModelGeneratorService {

   def grailsApplication


      private def getInputFiles() {
        def argsMap = grailsApplication.config.sqlDataFiles
        if (!argsMap)
          throw new IOException("Configuration key sqlDataFiles is empty. Set it in Config.groovy")

        'tables,primaryKeys,foreignKeys'.split(',').each {
            if (!argsMap[it])
              throw new IOException("Configuration key sqlDataFiles.$it is missing. Set it in Config.groovy")
            if (!new File(argsMap[it]).exists())
              throw new IOException("Configuration key sqlDataFiles.$it points to non-existent file: ${argsMap[it]}")
        }      
        argsMap
      }

   /**
    * Generate the model (package x class x field + links) of the underlying DB2 data.
    * @return (schema x table x column + foreign keys).
    */ 
   def makeModel() {
      def argsMap = getInputFiles()
      def universe = getColumns(new File(argsMap.tables))
      addPrimaryKeys(universe, new File(argsMap.primaryKeys))
      def foreignKeys = getForeignKeys(new File(argsMap.foreignKeys))
      [partition: universe, links: foreignKeys]
   }
  
  
    /**
    * @param inputFile, result of a DB2 "select ..." 
    * @return column data in a 3d matrix: [schema_name][table_name][column_name]
    */  
    private def getColumns ( inputFile ) { 
        def universe = [:]  // 3 dimensions : schema, table, column

        def index = [TABSCHEMA:0,TABNAME:1,COLNAME:2,TYPENAME:5,LENGTH:6]

        boolean isFirstLine = true
        inputFile.eachLine { x ->
            if (isFirstLine) {
              // always skip first line
              isFirstLine = false
              return
            }  
            def tokens = x.tokenize()
            
            // name  
            def schema = tokens[index.TABSCHEMA]
            def table = tokens[index.TABNAME]
            def column = tokens[index.COLNAME]
            def type = tokens[index.TYPENAME]
            def length = tokens[index.LENGTH]

            // store
            universe[schema] = universe[schema] ?: [:]
            universe[schema][table] = universe[schema][table] ?: [:]
            universe[schema][table][column] = universe[schema][table][column] ?: [:]
            universe[schema][table][column].type = type
            universe[schema][table][column].length = length

        }
        universe
    }

    /**
    * Insert in the universe the primary keys
    * @param inputFile, result of a DB2 "select ..." 
    */  
    private void addPrimaryKeys ( univ, inputFile ) {
        def index = [TABLE_CAT:0,TABLE_SCHEM:1,TABLE_NAME:2,COLUMN_NAME:3]
        boolean isFirstLine = true
        inputFile.eachLine { x ->
            if (isFirstLine) {
              // always skip first line
              isFirstLine = false
              return
            }  
            def tokens = x.tokenize()

            // name  
            def schema = tokens[index.TABLE_SCHEM]
            def table = tokens[index.TABLE_NAME]
            def column = tokens[index.COLUMN_NAME]


            // store
            // FIXME uncomment isPrimary
            // univ[schema][table][column].isPrimary = true
        }
        log.warn 'Not adding primary keys'
    }

    /**
    * @param inputFile, result of a DB2 "select ..." 
    * @return foreign key data as a list of "from-->to links
    */  
    private getForeignKeys (inputFile) { 
        def index = [TABSCHEMA:1,TABNAME:2,FK_COLNAMES:13,REFTABSCHEMA:6,REFTABNAME:7,REFKEYNAME:14]	
        def foreignKeys = []
        boolean isFirstLine = true
        inputFile.eachLine { x ->
            if (isFirstLine) {
              // always skip first line
              isFirstLine = false
              return
            }  
            def tokens = x.tokenize()
                
            // name  
            def schema1 = tokens[index.TABSCHEMA]
            def table1 = tokens[index.TABNAME]
            def column1 = tokens[index.FK_COLNAMES]
            def schema2 = tokens[index.REFTABSCHEMA]
            def table2 = tokens[index.REFTABNAME]
            def column2 = tokens[index.REFKEYNAME]

            // store
            foreignKeys << 
              [
              from: [ package:schema1, class:table1, field:column1 ], 
              to:   [ package:schema2, class:table2, field:column2 ]                
              ]
        }
        foreignKeys
    }
 
}

