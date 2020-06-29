def call(Map config) {
    podTemplate(
            label: 'kube-deploy-pod',
            cloud: 'kubernetes',
            inheritFrom: 'default',
            namespace: 'jenkins',
            containers: [
                    containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.17.5', ttyEnabled: true, command: 'cat'),
            ],
            serviceAccount: 'jenkins'
    ) {
        node('kube-deploy-pod') { 
            def serviceFile = service(config)
            def ingressFile = ingress(config)
            def deploymentFile = deployment(config)
            def hpafile = hpa(config)
            echo "service file: ${serviceFile}"
            container ('kubectl') {
                kube.deployIngress(ingressFile, config.namespace,"")
                kube.deployService(serviceFile, config.namespace)
                kube.deployDeployment(deploymentFile, config.namespace)
            }
        }
    }


}
