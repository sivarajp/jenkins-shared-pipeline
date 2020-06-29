import groovy.json.JsonSlurperClassic

def call(Map config) {



	def json = new JsonSlurperClassic().parseText(
		"""
		{
		"apiVersion": "apps/v1",
		"kind": "Deployment",
		"metadata": {
			"name": "${config.deployName}",
			"namespace": "${config.namespace}"
		},
		"spec": {
			"replicas":  ${config.replicas},
			"revisionHistoryLimit": 5,
			"selector": {
				"matchLabels": {
					"app": "${config.deployName}"
				}
			},
			"template": {
				"metadata": {
					"labels": {
						"app": "${config.deployName}",
						"env": "${config.environment}",
						"managed-by": "jenkins-shared-pipeline"
					},
					"annotations": {
						"prometheus.io/scrape": "${config.prometheusEnabled}",
						"prometheus.io/path": "${config.appContext}prometheus",
						"branch": "${config.branchName}",
						"project": "${config.deployName}",
						"owner-email": "${config.lastCommitterEmail}",
						"owner-repo": "${config.ownerRepo}",
						"last-committer": "${config.lastCommitterEmail}",
						"git-commit": "${config.commitId}",
						"platform": "${config.platform}"
					}
				},
				"spec": {
					"securityContext": {
						"runAsUser": ${config.uid},
						"fsGroup": ${config.gid}
					},
					"dnsPolicy": "${config.dnsPolicy}",
					"hostNetwork": ${config.hostNetwork},
					"terminationGracePeriodSeconds": ${config.terminationGracePeriodSeconds},
					"containers": [
					{
						"name": "${config.deployName}",
						"image": "${config.dockerimage}",
						"securityContext": {
								"allowPrivilegeEscalation": false,
								"runAsUser": ${config.uid},
								"runAsGroup": ${config.gid}
						},
						"ports": [
						{
							"containerPort": ${config.appPort},
							"name": "standard"
						}
						],
						"env": [
							{
								"name": "spring_profiles_active",
								"value": "${config.environment}"
							},
							{
								"name": "SERVER_SERVLET_CONTEXT_PATH",
								"value": "${config.appContext}"
							},
							{
								"name": "SERVICE_NAME",
								"value": "${config.deployName}"
							},
							{
								"name": "NAMESPACE",
								"value": "${config.namespace}"
							},
							{
								"name": "configEnvironment",
								"value": "${config.environment}"
							},
							{
								"name": "BRANCH_NAME",
								"value": "${env.BRANCH_NAME}"
							}
						],
						"resources": {
							"limits": {
								"cpu": "${config.maxCpu}",
								"memory": "${config.maxMemory}"
							},
							"requests": {
								"cpu": "${config.requestCpu}",
								"memory": "${config.requestMemory}"
							}
						}
					}
					]
				}
			}
		}
		}
		"""
		)

        def isJava = "java".equalsIgnoreCase(config.platform)
		if (isJava){
		} else {
            json.spec.template.spec.containers[0].args = []
        }

		def fileName = config.environment + "-dep.json"
  		utils.createFile(fileName, json)
		return fileName

}
