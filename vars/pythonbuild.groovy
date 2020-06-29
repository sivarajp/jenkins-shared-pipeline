def call(Map config) {
    podTemplate(
            label: 'kube-generic-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            serviceAccount: 'jenkins'
    ) 
    
    {
        node('kube-generic-build-pod') {
            try {
                stage ('Extract') {
                    checkout scm
                    config.commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    config.repoName = utils.getRepoName()
                    config.lastCommitterEmail = utils.getLastCommitterEmail()
                    config.lastCommit = utils.getLastCommit()
                    config.branchName = env.BRANCH_NAME
                }
                stage ('Test') {

                }

                if (config.doDockerBuild == 'true') {
                    stage ('Docker build and push')   {
                        container ('kaniko') {
                            sh "executor -f `pwd`/Dockerfile -c `pwd` --insecure --skip-tls-verify --cache=true --destination=sivarajp/${config.repoName}:${config.commitId}" 
                            config.dockerimage = "sivarajp/${config.repoName}:${config.commitId}"
                        }
                    }
                }

                if (config.doTBSBuild == 'true') {
                    stage ('TBS Docker build and push')   {
                        tbsbuild(config)
                    }
                }
            } finally {
               // cleanWs()
            }
        }
    }
}