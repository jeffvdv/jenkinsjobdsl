@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])

import org.yaml.snakeyaml.Yaml

def projecttitle = 'hosting'

configFile = readFileFromWorkspace('cfg/hosting.yaml')

def map
Yaml configFileYaml = new Yaml()
map = configFileYaml.load(configFile)

map.each() { p ->
    def projectname = p.projectname
    def gitlocation = p.gitlocation
    def environments = p.environments
    def deployServer = p.deployServer
    def branchname = p.branch
    def deployLocation = p.deployLocation
    def fabDeploy

    environments.each { env ->

        if ("${env}" == 'production'){
          fabDeploy = p.fabDeploy
        }

        folder("${projecttitle} - ${env}")

        job("${projecttitle} - ${env}/${projectname}") {

        authorization {
            permissions('authenticated', [
                'hudson.model.Item.Build',
                'hudson.model.Item.Read'
            ])
        }

        if ("${env}" != 'production'){
           scm {
                git {
                   branch("$branchname")
                   remote {
                      credentials("github")
                      url("${gitlocation}")
                   }
                }
            }

           triggers {
               githubPush()
           }
        }

        steps {
              if ("${env}" == 'production'){
                 shell("ssh -o StrictHostKeyChecking=no root@${deployServer} `fab ${fabDeploy} deploy restart`")
              } else {
                 shell("ssh -o StrictHostKeyChecking=no www-data@${deployServer} `cd ${deployLocation} && git fetch && git reset --hard`")
              }
          }
        }
    }
}
