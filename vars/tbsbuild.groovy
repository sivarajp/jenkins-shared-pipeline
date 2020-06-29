
def call(Map config) {
    podTemplate(
            label: 'kube-pbs-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'pb', image: 'sivarajp/pb', ttyEnabled: true, command: 'cat', alwaysPullImage: 'true'),
            ],
            volumes: [
                    secretVolume(mountPath: '/var/pbs/kube', secretName: 'pbconfig')
            ],
            serviceAccount: 'jenkins'
    ) 
    
    {
        node('kube-pbs-build-pod') {
            try {
                stage ('Docker build and push')   {
                    container ('pb') {
                        def filename = 'pb-build.yaml'
                        script {
                            def data = readYaml(text: libraryResource(filename))
                            def repoName = utils.getRepoName()
                            data.source.git.url = env.GIT_URL
                            data.source.git.revision = env.GIT_COMMIT
                            data.image.tag = "index.docker.io/sivarajp/${repoName}"
                            writeYaml file: filename, data: data
                            sh "mkdir ~/.kube && cp /var/pbs/kube/config ~/.kube/config"
                            sh "kubectl get ns ${config.namespace} || kubectl create ns ${config.namespace}"
                            sh "pb project target ${config.namespace}"
                            // sh "kubectl config use-context tbs-default-siva-aws-poc --namespace ${config.namespace}"
                            // withCredentials([file(credentialsId: "${config.namespace}-registry", variable: "registry")]) {
                            //          sh "pb secrets registry apply -f $registry"
                            // }
                            // withCredentials([file(credentialsId: "${config.namespace}-repo", variable: "repo")]) {
                            //          sh "pb secrets git apply -f  $repo"
                            // }

                            sh "cat ${filename}"
                            sh "pb image apply -f ${filename}"
                            sh "sleep 15"
                            def lastBuildNumber = sh( returnStdout: true,  script:  """ pb image builds ${data.image.tag} | tail -2 | head  -1 | awk '{ print \$1}' """)
                            lastBuildNumber = lastBuildNumber.trim()
                            sh "pb image logs ${data.image.tag} -b ${lastBuildNumber} -f"
                            sh "sleep 15"
                            def latestimage = sh( returnStdout: true,  script: "pb image status ${data.image.tag} | grep \"Latest\" ")
                            config.dockerimage = latestimage.substring(17, latestimage.length()).trim()
                            print config.dockerimage

                        }
                    }
                }
            } finally {
               // cleanWs()
            }
        }
    }
}