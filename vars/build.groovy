def call(Map config) {
    if (config.platform  == 'go') {
        gobuild(config)
    } else if  (config.platform  == 'java') {
        javabuild(config)
    } else if  (config.platform  == 'python') {
        pythonbuild(config)
    } else {
        genericbuild(config)
    }
}