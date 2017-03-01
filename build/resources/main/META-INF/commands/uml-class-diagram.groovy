description("Generate UML diagrams from grails code") {
    usage "grails uml-class-diagram"
}

/* NOT IMPLEMENTED : @see http://docs.grails.org/3.2.x/guide/commandLine.html#creatingCustomScripts

target(default: 'Generate UML diagrams from grails code') {

  depends(configureProxy, packageApp, classpath, loadApp, configureApp)
  def umlService = appCtx.getBean('umlService')

  def conf = classLoader.loadClass('grails.plugin.umlclassdiagram.ConfigurationCommand').newInstance()
       // class not found issues with  = new grails.plugin.umlclassdiagram.ConfigurationCommand()
  def url = umlService.redirect(conf)

  def stream = umlService.localPlantUml(url.replaceAll('^.*[/]',''))
  if (stream) {
      def outFile = File.createTempFile('output_', '.png')
      grailsConsole.updateStatus "Writing to $outFile"
      outFile.append(stream)
      grailsConsole.updateStatus "UML written to $outFile"
  } else {
      grailsConsole.error 'No data produced !?'
  }
}
*/
