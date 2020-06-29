import groovy.json.JsonSlurperClassic

def call(Map config) {
    
    def json = new JsonSlurperClassic().parseText(
    """
    {
    	  "apiVersion": "v1",
    	  "kind": "Service",
    	  "metadata": {
    	    "name": "${config.deployName}",
    	    "namespace": "${config.namespace}",
    	    "annotations": {
    	        "prometheus.io/scrape": "true",
    	        "prometheus.io/path": "${config.urlContextPath}/prometheus"
    	    },
    	    "labels": {
    	      "managed-by": "service-workflow-library"
    	    }
    	  },
    	  "spec": {
    	    "ports": [
    	      {
    	    	"name": "http",
    	        "port": 8080,
    	        "targetPort": ${config.appPort}
    	      }
    	    ],
    	    "selector": {
    	      "app": "${config.deployName}"
    	    }
    	  }
    	}

    """
    )
    print "${json}"

	def fileName = config.environment + "-svc.json"
    utils.createFile(fileName, json)
	return fileName
    
}