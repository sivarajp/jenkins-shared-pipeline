def call(Map config) {
    podTemplate(
            label: 'kube-go-build-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'go', image: 'golang:latest', ttyEnabled: true, command: 'cat'),
                    containerTemplate(name: 'dind', image: 'amidos/dcind', ttyEnabled: true, command: 'cat'),
            ],
            serviceAccount: 'jenkins'
    ) 
    
    {
        node('kube-go-build-pod') {
            try {
                stage ('Checkout') {
                    gitcheckout(config)
                     sh 'ls -R'
                     sh 'pwd'
                }

                stage ('Build') {
                    // container ('go') {
                    //     sh 'go version'
                    // }
                    container ('dind') {
                        sh 'docker build -t dind-test .'
                    }
                }
            } finally {
               // cleanWs()
            }
        }
    }
}