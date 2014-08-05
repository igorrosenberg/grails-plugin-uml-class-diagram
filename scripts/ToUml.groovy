includeTargets << grailsScript('_GrailsInit')
includeTargets << grailsScript('_GrailsBootstrap')

target(toUml: 'Generate UML diagrams from grails code') {

  depends(configureProxy, packageApp, classpath, loadApp, configureApp)
  def umlService = appCtx.getBean('umlService')
  
  def conf = new grails.plugin.touml.ConfigurationCommand()
  def url = umlService.redirect(conf)
  
  def stream = umlService.localPlantUml(url.replaceAll('^.*/',''))
  if (stream) {
      def outFile = File.createTempFile('output_', '.png')
      println 'Output goes to ' + outFile
      outFile.append(stream) 
      println 'Output done '
  } else {
      println 'No data produced !?'
  }
}

setDefaultTarget(toUml)
