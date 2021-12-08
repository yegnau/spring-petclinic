import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.ant
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
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
    buildType(BuildMaven)
    buildType(BuildWithArtifacts)
}

object BuildMaven : BuildType({
    name = "Maven Build"

    vcs {

        root(DslContext.settingsRoot) // use the current one
    }

    steps {

        maven {

            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        swabra {  }
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

fun cleanFiles(buildType: BuildType): BuildType {
    if (buildType.features.items.find { it.type == "swabra" } == null) {
        buildType.features {
            swabra {
            }
        }
    }
    return buildType
}
