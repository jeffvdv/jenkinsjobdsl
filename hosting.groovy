/* @Grapes(
        ·ck
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
)*/
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

    job("${project}/${projectname}") {
        logRotator {
            numToKeep(5)
            artifactNumToKeep(10)
        }

        blockOn("${project}/${projectname}.*") {
            blockLevel('GLOBAL')
            scanQueueFor('ALL')
        }

        scm {
            git("${gitlocation}", "${branch}")
            }
        }
        triggers {
            scm('H/5 * * * *')
        }

        steps {
//            shell("ssh -o StrictHostKeyChecking=no www-data@${deployServer} \"cd ${deployLocation} && git fetch && git reset --hard\"")
        }
      }
