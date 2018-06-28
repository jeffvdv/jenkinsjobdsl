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

    environments.each { env ->
        def environment = env.name
        def branchname
        def deployServer = env.deployServer
        def deployLocation
        def fabDeploy

        if ("${environment}" == 'production'){
          fabDeploy = p.fabDeploy
        }
        else
        {
          branchname = env.branchname
          deployLocation = env.deployLocation
        }

        folder("${projecttitle} - ${environment}")

        job("${projecttitle} - ${environment}/${projectname}") {

        authorization {
            permissions('authenticated', [
                'hudson.model.Item.Build',
                'hudson.model.Item.Read'
            ])
        }

        if ("${environment}" != 'production'){
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
              if ("${environment}" == 'production'){
                 shell("ssh -o StrictHostKeyChecking=no root@${deployServer} `fab ${fabDeploy} deploy restart`")
              } else {
                 shell("ssh -o StrictHostKeyChecking=no www-data@${deployServer} `cd ${deployLocation} && git fetch && git reset --hard`")
              }
          }
        }
    }
}
