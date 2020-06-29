def deployService(String fileName, String namespace) {
    sh "kubectl apply -f ${fileName} --namespace ${namespace}"
}

def deployIngress(String fileName, String namespace, String basicAuthData) {
    sh "kubectl apply -f ${fileName} --namespace ${namespace}"
}


def deployDeployment(String fileName, String namespace) {
    echo "deployment file: ${fileName}"
    sh "kubectl apply -f ${fileName} --namespace ${namespace}"
}