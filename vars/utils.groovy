import groovy.json.JsonBuilder

def emptyDir() {
    sh "ls -A1 | xargs rm -rf"
}


String getRepoName() {
    return scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
}

def getMaxHeapPercent(String maxMem) {

	heapPercent = "85"
	if(maxMem.contains("Gi")) {
	   number = maxMem.split("Gi")[0]
     numberInt = number.toInteger()
	} else {
	   number = maxMem.split("Mi")[0]
	   numberInt = (int)(number.toInteger() / 1024)
	}
  if(numberInt <= 2){
    heapPercent = "85"
  } else if(numberInt > 2 && numberInt <= 10){
    heapPercent = "90"
  } else if(numberInt > 10 && numberInt <= 20){
    heapPercent = "95"
  }
	return heapPercent
}

def getMinHeap(String reqMem) {
	  heapString = "-Xms256m"
	  mbString = "256"
	  if(reqMem.contains("Gi")) {
	     number = reqMem.split("Gi")[0]
	     convertToMB = number.toInteger() * 1024
	     mbString = convertToMB.toString()
	  } else {
	     number = reqMem.split("Mi")[0]
	     mbString = number.toString()
	  }
	  heapString = heapString.replace("256", mbString)
	  return heapString
}


def getMaxHeap(String maxMem, maxHeapPercent) {
	heapString = "-Xmx2048m"
	if(maxMem.contains("Gi")) {
	   number = maxMem.split("Gi")[0]
	   convertToMB = number.toInteger() * 1024
	   overhead = (int)((convertToMB * (100 - maxHeapPercent.toInteger())) /100)
	   allowOverhead = convertToMB - overhead
	   heapString = heapString.replace("2048", allowOverhead.toString())
	} else {
	   number = maxMem.split("Mi")[0]
	   overhead = (int)((number.toInteger() * (100 - maxHeapPercent.toInteger()))/100)
	   allowOverhead = number - overhead
	   heapString = heapString.replace("2048", allowOverhead.toString())
	}
	return heapString
}


def getLastCommitterEmail(){
	committerEmail = ""
	try{
		committerEmail = sh (
		script: 'git --no-pager show -s --format=\'%ae\'',
		returnStdout: true
		).trim()
		echo 'Discovered last committer: '+committerEmail+'\n'
	}catch(err){
		echo 'Cannot get the last committer: '+err
	}
	return committerEmail
}


def getLastCommit(){
	lastCommit = ""
	try{
		lastCommit = sh (
		script: 'git rev-parse HEAD',
		returnStdout: true
		).trim()
		echo 'Discovered last commit: '+lastCommit+'\n'
	}catch(err){
		echo 'Cannot get the last commit: '+err
	}
	return lastCommit
}

def getUrlContextPath(repoName) {
	def path = getDeployName(repoName)
	def contextPath = "/services/${path}"
	return contextPath
}

def getDeployName(repoName) {
	def branchName = env.BRANCH_NAME.equals("master") ? "" : env.BRANCH_NAME + "-" ;
	return (branchName + repoName).toLowerCase();
}

def createFile(fileName, input) {
	writeJSON file: fileName, json: input, pretty: 4
	archiveArtifacts artifacts: fileName, fingerprint: true
	stash includes: fileName, name: fileName
}

def createYamlFile(fileName, input) {
	writeYaml file: fileName, data: input, pretty: 4
	archiveArtifacts artifacts: fileName, fingerprint: true
	stash includes: fileName, name: fileName
}

// def writeJSONFile(fileName, json) {
// 	def builder = new JsonBuilder(json)
// 	String output = builder.toPrettyString()
// 	echo 'writing resource '+fileName+': \n' + output
// 	writeFile file: fileName, text: output
// }

def toJson(obj){
	def builder = new JsonBuilder(obj)
	String output = builder.toPrettyString()
	return output
}