import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {
    buildType(BuildWithArtifacts)
    buildType(Build)
}

object Build : BuildType({
    name = "Build Maven"

    vcs {
        root(DslContext.settingsRoot) // use the current one
    }

    steps {
        maven {
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            dockerImage = "maven"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        swabra {
        }
    }
})

object BuildWithArtifacts : BuildType({
    name = "Build with artifacts"

    artifactRules = "=>artifacts"

    steps {
        script {
            name = "Create 1 MB file"
            scriptContent = "fallocate -l 1MB file_1mb"
        }
        script {
            name = "Create 5 MB file"
            scriptContent = "fallocate -l 5MB file_5mb"
        }
        script {
            name = "Create 1 GB file"
            scriptContent = "fallocate -l 1G file_1gb"
        }
    }
})

/*object HttpsGithubComMarcobehlerSpringPetclinicRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/marcobehler/spring-petclinic/#refs/heads/main"
    url = "https://github.com/marcobehler/spring-petclinic/"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})*/
