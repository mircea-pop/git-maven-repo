package com.epages.git.mavenrepo

import org.ajoberstar.gradle.git.tasks.GitAdd
import org.ajoberstar.gradle.git.tasks.GitBase
import org.ajoberstar.gradle.git.tasks.GitClone
import org.ajoberstar.gradle.git.tasks.GitCommit
import org.ajoberstar.gradle.git.tasks.GitPush
import org.eclipse.jgit.api.Git
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskCollection

class GitMavenRepoPlugin implements Plugin<Project> {
    static final String USERNAME_PROP = 'github.credentials.username'
    static final String PASSWORD_PROP = 'github.credentials.password'

    static final String TASK_GROUP_NAME = "git-mvn-Repo"
    static final String CLEAN_TASK_NAME = 'cleanGitRepo'
    static final String CLONE_TASK_NAME = 'cloneGitRepo'
    static final String ADD_TASK_NAME = 'addGitRepo'
    static final String COMMIT_TASK_NAME = 'commitGitRepo'
    static final String PUSH_TASK_NAME = 'pushGitRepo'
    static final String PUBLISH_TASK_NAME = 'publishGitRepo'

    @Override
    public void apply(final Project project) {
        project.apply (plugin : MavenPlugin)

        GitMavenRepoExtension extension = new GitMavenRepoExtension(project)

        project.extensions.add('githubRepo', extension)
        
        setDefaultCredentials(project, extension)

        configureTasks(project, extension)

        TaskCollection tasks = project.tasks.matching { it.name.endsWith('GitRepo') }

        tasks.all { it.group = TASK_GROUP_NAME }

        tasks.withType(GitBase) {
            it.repoPath = { extension.workingPath }
        }
    }

    /**
     * Configures the tasks to publish to gh-pages.
     * @param project the project to configure
     * @param extension the plugin extension
     */
    private void configureTasks(final Project project, final GitMavenRepoExtension extension) {
        Delete clean = project.tasks.add(CLEAN_TASK_NAME, Delete)
        clean.description = 'Cleans the working path of the repo.'
        clean.delete { extension.workingPath }

        GitClone clone = project.tasks.add(CLONE_TASK_NAME, GitClone)
        clone.description = 'Clones the Github repo checking out the defined branch'
        clone.dependsOn clean
        clone.conventionMapping.credentials = { extension.credentials }
        clone.uri = { extension.gitMavenRepo }
        clone.branch = { extension.gitBranch }
        clone.destinationPath = { extension.workingPath }
        clone.doLast {
            String currentBranch = Git.open(clone.destinationDir).repository.branch
            if (currentBranch != clone.branch) {
                throw new GradleException("Intended to checkout ${clone.branch}, but currently on ${currentBranch}.  You may need to create ${clone.branch}.")
            }
        }

        GitAdd add = project.tasks.add(ADD_TASK_NAME, GitAdd)
        add.description = 'Adds all changes to the working defined repo'
        add.dependsOn clone

        GitCommit commit = project.tasks.add(COMMIT_TASK_NAME, GitCommit)
        commit.description = 'Commits all changes to the working defined repo'
        commit.message = {"${project.name}-${project.version}"}
        commit.dependsOn add

        GitPush push = project.tasks.add(PUSH_TASK_NAME, GitPush)
        push.description = 'Pushes all changes in the working defined repo to Github'
        push.dependsOn commit
        push.conventionMapping.credentials = { extension.credentials }

        Task publish = project.tasks.add(PUBLISH_TASK_NAME)
        publish.description = 'Publishes all defined repo changes to Github'
        publish.dependsOn push

        add.dependsOn  project.uploadArchives
    }

    /**
     * Sets the default credentials based on project properties.
     * @param project the project to get properties from
     * @param extension the extension to configure credentials for
     */
    private void setDefaultCredentials(Project project, GitMavenRepoExtension extension) {
        if (project.hasProperty(USERNAME_PROP)) {
            extension.credentials.username = project[USERNAME_PROP]
        }
        if (project.hasProperty(PASSWORD_PROP)) {
            extension.credentials.password = project[PASSWORD_PROP]
        }
    }
}
