package com.epages.git.mavenrepo

import org.ajoberstar.gradle.util.ObjectUtil
import org.gradle.api.Project

class GitMavenRepoExtension {
    private final Project project
    /**
     * closure to configure the pom for maven 
     */
    Closure pom = {
        name project.name
        packaging 'jar'
        description project.description
    }

    /**
     * GitHub repository url
     */
    String gitMavenRepo
    
    /**
     * GitHub repository branch
     */
    String gitBranch = "gh-pages"

    /**
     * The path to put the github repository in.
     */
    String workingPath = "${project.buildDir}/maven-repo"

    GitMavenRepoExtension(Project project) {
        this.project = project
    }

    String getGitMavenRepo() {
        return ObjectUtil.unpackString(gitMavenRepo)
    }
    
    String getWorkingPath() {
        return ObjectUtil.unpackString(workingPath)
    }
    
    String getGitBranch() {
        return ObjectUtil.unpackString(gitBranch)
    }
}
