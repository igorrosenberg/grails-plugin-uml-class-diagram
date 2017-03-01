
withConfig(configuration) {
    inline(phase: 'CONVERSION') { source, context, classNode ->
        classNode.putNodeMetaData('projectVersion', '0.7')
        classNode.putNodeMetaData('projectName', 'uml-class-diagram')
        classNode.putNodeMetaData('isPlugin', 'true')
    }
}
