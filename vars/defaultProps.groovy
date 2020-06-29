
def call(Map config) {
    if (config.platform?.trim()){
        config.platform = config.platform
    } else {
        config.platform = "java"
    }

    if (config.namespace?.trim()){
        config.namespace = config.namespace
    } else {
        config.namespace = "default"
    }
    
    if (config.project?.trim()){
        config.project = config.project
    } else {
        config.project = "default"
    }    

    if (!config.appPort){
        config.appPort = "8080"
    }

    if (!config.healthUri?.trim()) {
        config.healthUri = "/health"
    }

    if  (!config.requestCpu?.trim()) {
        config.requestCpu =  "100m"
    }  

    if  (!config.maxCpu?.trim()) {
        config.maxCpu =  "1000m"
    }  

    if (!config.requestMemory?.trim()) {
        config.requestMemory =  "700Mi"
    }

    if (!config.maxMemory?.trim()) {
        config.maxMemory =  "2Gi"
    }

    if (!config.initialDelaySeconds?.trim()) {
        config.initialDelaySeconds = 60
    }

    if (!config.maxHeapPercent?.trim() || config.maxHeapPercent?.trim() > 100) {
        config.maxHeapPercent =  utils.getMaxHeapPercent(config.maxMemory)
    }

    config.maxHeap = utils.getMaxHeap(config.maxMemory, utils.getMaxHeapPercent(config.maxHeapPercent)) 
    config.minHeap = utils.getMinHeap(config.requestMemory)


    if (!config.livenessProbe?.trim()) {
        config.livenessProbe = "true"
    }
 
    if (!config.livenessProbeType?.trim()) {
        config.livenessProbeType = "endpoint"
    }

    if (!config.livenessProbePeriod?.trim()) {
        config.livenessProbePeriod =   10
    }

    if (!config.readinessProbe?.trim()) {
        config.readinessProbe = "true"
    }
    
    if (!config.readinessProbeType?.trim()) {
        config.readinessProbeType = "endpoint"
    }

    if (!config.readinessProbePeriod?.trim()) {
        config.readinessProbePeriod =   10
    }

    if (!config.terminationGracePeriodSeconds?.trim()) {
        config.terminationGracePeriodSeconds =   60
    }

    if (!config.hostNetwork?.trim()) {
        config.hostNetwork = "false"
    }

    if (!config.prometheusEnabled?.trim()) {
        config.prometheusEnabled = "true"
    }

    if (!config.dnsPolicy?.trim()) {
        config.dnsPolicy = "ClusterFirst"
    }

    if (!config.ingressType?.trim()) {
        config.ingressType = "context"
    }

    if (!config.ownerRepo?.trim()) {
        config.ownerRepo = scm.getUserRemoteConfigs().get(0).getUrl()
    }

    if (!config.uid?.trim() || config.uid ==0) {
        config.uid = 2000
    }

    if (!config.gid?.trim() || config.gid ==0) {
        config.gid = 2000
    }

    if (!config.enableFileUpload?.trim()) {
        config.enableFileUpload = "false"
    }

    if (!config.uploadFileSize?.trim()) {
        config.uploadFileSize =   "1m"
    }

    config.registry = "sivarajp"

    if (config.ingressType?.trim() == "no-context")  {
        config.appContext = "/"
    }

}