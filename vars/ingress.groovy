import groovy.json.JsonSlurperClassic

def call(Map config) {

	print "ingress.config: ${config}"

	def classNameExists = ("".equals(config.className))? false : true
    def ingressNodeName = (config.ingressNodeName!=null)?"${config.ingressNodeName}" : ""
	def ingressType = (config.ingressType != null) ? "${config.ingressType}" : "context"
    def ingressHostMapping = (config.ingressHostMapping!=null) ? config.ingressHostMapping: []
	config.host = "dev.kubeeight.com"

    def annotationsJson = getAnnotationJson(config.appContext, config.urlContextPath, config.nginxAnnotations, config.context)


	def json = new JsonSlurperClassic().parseText(
    """
    		{
    			  "apiVersion": "networking.k8s.io/v1beta1",
    			  "kind": "Ingress",
    			  "metadata": {
    			    "annotations": ${annotationsJson},
    			    "name": "${config.deployName}-${config.ingNameSuffix}",
    			    "namespace": "${config.namespace}"
    			  },
    			  "spec": {
    			    "rules": [
    			      {
						"host": "${config.host}",
    			        "http": {
    			          "paths": [
    			            {
    			              "backend": {
    			                "serviceName": "${config.deployName}",
    			                "servicePort": ${config.appPort}
    			              },
    			              "path": "${config.urlContextPath}(/|\$)(.*)"
    			            }
    			          ]
    			        }
    			      }
    			    ]
    			  }
    			}

    """
    )
	def fileName = config.environment + "-ingress-${config.ingNameSuffix}.json"
  	utils.createFile(fileName, json)
	return fileName

    
}


def getAnnotationJson(appContext, externalContextPath, nginxAnnotations, context){
	def annotations =  [ "nginx.ingress.kubernetes.io/rewrite-target": "${appContext}\$2",
						 "nginx.ingress.kubernetes.io/ssl-redirect": "false",
						 "kubernetes.io/ingress.class": "nginx"]
	  print "Prepping up ingress annotations"

	  try{
	      if (nginxAnnotations!=null){
	    	  for (kv in nginxAnnotations){
	    		  try{
		    		  if (kv.value instanceof String){
		    			  annotations[kv.key] = kv.value.replaceAll("__CONTEXT__", "${externalContextPath}")

		    		  }else{
		    			  if(kv.value instanceof Collection ){
		    				  def annotationValue = ""
		    			      for (item in kv.value){
		    			    	  annotationValue += item.replaceAll("__CONTEXT__", "${externalContextPath}")+"\n"
		    			      }
		    			      annotations[kv.key] = annotationValue
		    			  }
		    		  }
	    		  }catch(err){
	    			  print "Failed checking ${kv.key}:"+err
	    		  }
	    	  }
	      }

	  }catch(err1){
		  print "The whole thing failed: "+err1
	  }

	  print annotations

	  def annotationsJson = utils.toJson(annotations)
	  print "JSON:"+annotationsJson
	  return annotationsJson
}