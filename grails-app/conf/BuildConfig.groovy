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
			build ':release:3.1.0'
	}
}
