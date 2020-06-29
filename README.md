# jenkins-shared-pipeline

This shared library has 4 pipelines

### Pipelines

#### 1. BuildMerge.groovy
    This pipeline excludes master branch. At the end of the build, it merges the branch to master.
    Pipeline steps:
        1. Checkout
        2. Run Tests & Code Coverage
        3. If enough code coverage and tests, it automatically merges the branch to master. 
           You can configure master branch in TBS for the build of the container. The TBS 
           will start the build automatically

#### 2. BuildTBS.groovy
    Prerequisites:
        1. TBS running in the cluster
    Pipeline steps:
     1. Checkout
     2. Run Build
     3. Run Tests & Code Coverage
     4. Create Docker image using TBS, It summits the git commit id to tbs to build the image

#### 3. BuildDeploy.groovy 
    Prerequisites:
        1. Nginx ingress controller
    Pipeline steps:
        1. Checkout
        2. Run Build
        3. Run Tests & Code Coverage
        4. Create Docker image using kanico
        5. Deploys Deployment, Service and Nginx ingress to kubernetes cluster

#### 4. BuildTBSDeploy.groovy
    Prerequisites:
        1. TBS running in the cluster
        2. Nginx ingress controller
    This pipeline does the following
     1. Checkout
     2. Run Build
     3. Run Tests & Code Coverage
     4. Create Docker image using TBS, It summits the git commit id to tbs to build the 
        image (Kube deployment needs to handled seperatly)
     5. Once the image is built, Deploys Deployment, Service and Nginx ingress to kube cluster


### How to use

Jenkins:
 1. I have preconfigured jenkins with all necessary workflow in this repor. You can install the jenkins by following the instruction there  https://github.com/sivarajp/jenkins.
Application:
   In your application project add Jenkinsfile, Select the pipleine you want to use from above list and like below. 
```sh
BuildMerge{
   platform = "java"
   namespace = "account"
}
```

Nginx:
  If you dont have ngnix installed already, you can use the nginx from https://github.com/sivarajp/jenkins.
