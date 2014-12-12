grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'info'

    repositories {
        mavenLocal()
        mavenCentral()
        grailsCentral()
    }

    dependencies {
       runtime 'net.sourceforge.plantuml:plantuml:8000'
    }

    plugins {
			build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			   export = false
			}
	}
}
