
@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])

import org.yaml.snakeyaml.Yaml

def project = 'hosting'
folder("${project}")

configFile = readFileFromWorkspace('cfg/hosting.yaml')

def map
Yaml configFileYaml = new Yaml()
map = configFileYaml.load(configFile)

map.each() { p ->
    def projectname = p.project
    def gitlocation = p.gitlocation
    def environment = p.environment
    def deployServer = p.deployServer
    def branch = p.branch
    def deployLocation = p.deployLocation

    job('example-1') {
      steps {
          shell('echo Hello World!')
      }
    }
    /*job("${project}/${projectname}") {
        logRotator {
            numToKeep(5)
            artifactNumToKeep(10)
        }

        scm {
            git("${gitlocation}", "${branch}")
            }
        }

        steps { 
            shell("echo test")
            shell('echo hallo')
        }
      }*/
