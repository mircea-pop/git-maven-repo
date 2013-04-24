package com.epages.git.mavenrepo

import org.ajoberstar.gradle.git.plugins.GithubPagesPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin

class GitMavenRepoPlugin implements Plugin<Project> {
    private static final GIT_MV_REPO_GROUP = "Git-Maven-Repo"

    @Override
    public void apply(Project project) {
        project.apply (plugin : MavenPlugin)
        
        project.apply (plugin : GithubPagesPlugin)
        
        GitMavenRepoExtension extension = new GitMavenRepoExtension(project)

        project.extensions.add('githubRepo', extension)

        configureMaven(project, extension)

        project.githubPages {
            repoUri = {extension.gitMavenRepo}
            workingPath = {extension.workingPath}
        }

        project.cloneGhPages.branch = {extension.gitBranch}
        project.commitGhPages.message = "${project.name}-${project.version}"

        project.task("publish2Git", dependsOn: project.pushGhPages) {
            group = GIT_MV_REPO_GROUP
            description = "Runs the maven installer and pushes the artifacts to the Git-based maven repository"
        }

        project.task("commit2Git", dependsOn: project.commitGhPages){
            group = GIT_MV_REPO_GROUP
            description = "Runs the maven installer and commits the artifacts to the Git-based maven repository"
        }

        project.addGhPages.dependsOn project.uploadArchives
    }

    /**
     * FIXME - extension variables configured on the project are not passed. Default are used! 
     */
    private void configureMaven(final Project project, final GitMavenRepoExtension extension) {
        def isDevBuild
        def uploadRepositoryUrl

        if (project.hasProperty("release")) {
            uploadRepositoryUrl = "file:${extension.workingPath}/releases"
        }else if (project.hasProperty("ci")) {
            project.version += '-SNAPSHOT'
            uploadRepositoryUrl = "file:${extension.workingPath}/snapshots"
        }else {
            isDevBuild = true
        }

        project.uploadArchives {
            repositories {
                if (isDevBuild) {
                    mavenLocal()
                }else {
                    mavenDeployer {
                        name = "GitHub"

                        repository(url: uploadRepositoryUrl)

                        pom.project  {extension.pom}
                    }
                }
            }
        }
    }
}
