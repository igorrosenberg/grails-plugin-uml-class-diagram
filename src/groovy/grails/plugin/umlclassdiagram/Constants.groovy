package grails.plugin.umlclassdiagram

class Constants {

  static final UNKNOWN = 'UnknownType'
  static final GRAILS_ARTEFACTS  = 'JQueryService,grails.plugin.cache.GrailsCacheAdminService,asset.pipeline.AssetsController,asset.pipeline.AssetProcessorService,grails.plugin.databasemigration.DbdocController'.split(',')
           
   static final ARTEFACTS  = 
   [
   Controller:   
      'defaultAction,instanceControllerTagLibraryApi,controllerUri,instanceControllerTagLibraryApi,session,servletContext,controllerClass,response,controllerName,webRequest,assetProcessorService,grailsAttributes,instanceControllersRestApi,applicationContext,flash,class,actionName,actionUri,modelAndView,pluginContextPath,errors,instanceControllersApi,params,chainModel,grailsApplication,request,controllerNamespace',
    Service:'class'
    ].collectEntries{ k, v -> [k, v.split(',')] }

}