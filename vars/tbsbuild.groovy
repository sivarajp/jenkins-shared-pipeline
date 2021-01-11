
def call(Map config) {
    podTemplate(
            label: 'kube-pbs-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'kp', image: 'sivarajp/kp', ttyEnabled: true, command: 'cat', alwaysPullImage: 'true')
            ],
            volumes: [
                    secretVolume(mountPath: '/var/kp/kube', secretName: 'kubeconfig')
            ],
            serviceAccount: 'jenkins'
    ) 
    
    {
        node('kube-pbs-build-pod') {
            try {
                stage ('TBS build and push')   {
                    container ('kp') {
                        script {
                            sh "export KUBECONFIG=/var/kp/kube/config && kp image list &&  kp image trigger ${config.repoName}"
                        }
                    }
                }
            } finally {
               // cleanWs()
            }
        }
    }
}