
@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])

import org.yaml.snakeyaml.Yaml

def projecttitle = 'hosting'
folder("${projecttitle}")

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

    job("${projecttitle}/${projectname}") {
    
    scm {
        git("${gitlocation}", "${branch}")
    }

    steps {
          shell("ssh -o StrictHostKeyChecking=no www-data@${deployServer} `cd ${deployLocation} && git fetch && git reset --hard`")
      }
    }
}
