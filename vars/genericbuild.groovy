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
                    gitcheckout(config)
                }
            } finally {
               // cleanWs()
            }
        }
    }
}