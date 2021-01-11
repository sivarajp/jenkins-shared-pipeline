def DOCKER_IMAGE // Global Variable

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
                            sh """
                                export KUBECONFIG=/var/kp/kube/config 
                                kp image list -n acme-builds
                                kp image trigger ${config.repoName} -n acme-builds
                                sleep 30
                                X=$( kp image status  user-service -n acme-builds | grep Status  | cut -d':' -f2 | xargs )
                                while [ "$X" != "Ready" ]
                                do
                                    sleep 10
                                    X=$( kp image status  user-service -n acme-builds | grep Status  | cut -d':' -f2 | xargs )
                                done
                            """
                            //DOCKER_IMAGE = sh(script: "export KUBECONFIG=/var/kp/kube/config  && kp image status  ${config.repoName} -n acme-builds | grep LatestImage  | cut -d':' -f2 | xargs ", returnStdout: true).trim()
                            
                        }
                    }
                }
                echo $DOCKER_IMAGE
            } finally {
               // cleanWs()
            }
        }
    }
}

//

