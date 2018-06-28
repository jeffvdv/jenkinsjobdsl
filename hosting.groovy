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
    def environment = p.environment
    def deployServer = p.deployServer
    def branch = p.branch
    def deployLocation = p.deployLocation
    def fabDeploy
    if ("${environment}" == 'production'){
      fabDeploy = p.fabDeploy
    }

    folder("${projecttitle} - ${environment}")

    job("${projecttitle} - ${environment}/${projectname}") {
     
    authorization {
        permissions('Authenticated Users', [
            'hudson.model.Item.Build',
            'hudson.model.Item.Read'
        ])
    }
    
    if ("${environment}" != 'production'){
       scm {
           git("${gitlocation}", "${branch}")
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
