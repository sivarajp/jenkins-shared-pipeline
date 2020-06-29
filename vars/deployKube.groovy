def call(Map config) {
    print "Job Name ${config}"
    config.replicas  = 1
    config.repoName = utils.getRepoName()
    config.deployName=utils.getDeployName(config.repoName)
    config.urlContextPath = "/services/${config.deployName}"
    config.environment = "dev"
    config.ingNameSuffix = "int"

    if (!config.appContext?.trim()) {
         config.appContext = "/services/${config.deployName}/"
    }
    print "context path ${config.urlContextPath}"
    deploy(config)
}
