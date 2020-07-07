def call(Map config) {
    podTemplate(
            label: 'kube-generic-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'kaniko', image: 'mgit/base:kaniko-executor-debug-stable', ttyEnabled: true, command: 'cat'),

            ],
            volumes: [
                    secretVolume(mountPath: '/kaniko/.docker/', secretName: 'kaniko-secret'),
            ],
            serviceAccount: 'jenkins',
            runAsUser: 'jenkins'
    ) 
    
    {
        node('kube-generic-build-pod') {
            try {
                stage ('Extract') {
                    gitcheckout(config)
                }
                stage ('Test') {

                }

                if (config.doDockerBuild == 'true') {
                    stage ('Docker build and push')   {
                        container ('kaniko') {
                            sh "executor -f `pwd`/Dockerfile -c `pwd` --insecure --skip-tls-verify --cache=true --destination=${config.registry}/${config.repoName}:${config.commitId}" 
                            config.dockerimage = "${config.registry}/${config.repoName}:${config.commitId}"
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