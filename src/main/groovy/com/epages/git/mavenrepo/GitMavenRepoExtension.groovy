package com.epages.git.mavenrepo

import org.ajoberstar.gradle.git.auth.BasicPasswordCredentials
import org.ajoberstar.gradle.util.ObjectUtil
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publication.maven.internal.ant.BaseMavenDeployer
import org.gradle.util.ConfigureUtil


class GitMavenRepoExtension {
    private final Project project
    PasswordCredentials credentials = new BasicPasswordCredentials()
    /**
     * GitHub repository url
     */
    String gitMavenRepo

    /**
     * GitHub repository branch
     */
    String gitBranch = "gh-pages"

    GitMavenRepoExtension(Project project) {
        this.project = project
    }

    String getGitMavenRepo() {
        return ObjectUtil.unpackString(gitMavenRepo)
    }

    String getWorkingPath() {
        def workingPath = "${project.buildDir}/maven-repo"
        
        def mavenRepositories = project.tasks.getByName("uploadArchives").repositories

        def hasMavenDeployer = mavenRepositories.find() instanceof BaseMavenDeployer

        if (hasMavenDeployer){
            workingPath = mavenRepositories.find().repository.url
            if (workingPath.endsWith("/releases")) {
                workingPath = workingPath.minus("/releases")
            }
            if (workingPath.endsWith("/snapshots")) {
                workingPath = workingPath.minus("/snapshots")
            }
        }
        return workingPath
    }

    String getGitBranch() {
        return ObjectUtil.unpackString(gitBranch)
    }

    /**
     * Configured the credentials to be used when interacting with
     * the repo. This will be passed a {@link PasswordCredentials}
     * instance.
     * @param closure the configuration closure
     */
    void credentials(Closure closure) {
        ConfigureUtil.configure(closure, credentials)
    }
}
