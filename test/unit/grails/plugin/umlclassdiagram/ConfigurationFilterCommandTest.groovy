package grails.plugin.umlclassdiagram

import org.junit.Test

class ConfigurationFilterCommandTest {

    def clazz = 'Race'

    @Test
    void testFail() {
        def c = new ConfigurationFilterCommand(inclusion: true)
        c.regexps = ['xxx']
        assert ! c.validate(clazz)
    }

    @Test
    void testExact() {
        def c = new ConfigurationFilterCommand(inclusion: true)
        c.regexps = ['Race']
        assert c.validate(clazz)
    }

    @Test
    void testGlob() {
        def c = new ConfigurationFilterCommand(inclusion: true)
        c.regexps = ['.*ac.*']
        assert c.validate(clazz)
    }

    @Test
    void testMultiple() {
        def c = new ConfigurationFilterCommand(inclusion: true)
        c.regexps = ['xxx','yyy','Race']
        assert c.validate(clazz)
    }

    @Test
    void testExclusion() {
        def c = new ConfigurationFilterCommand(inclusion: false)
        c.regexps = ['.*x']
        assert c.validate(clazz)
    }

}