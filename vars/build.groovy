def call(Map config) {
    if (config.platform  == 'go') {
        gobuild(config)
    } else if  (config.platform  == 'java') {
        if (config.build == 'gradle') {
            javabuildgradle(config)
        } else {
            javabuild(config)
        }
    } else if  (config.platform  == 'python') {
        pythonbuild(config)
    } else {
        genericbuild(config)
    }
}